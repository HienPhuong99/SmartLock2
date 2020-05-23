package com.lyoko.smartlock.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lyoko.smartlock.Adapters.CanRemotePersonInfoAdapter;
import com.lyoko.smartlock.Interface.iCanRemote;
import com.lyoko.smartlock.LyokoActivity;
import com.lyoko.smartlock.Models.CanRemotePersonInfo;
import com.lyoko.smartlock.R;
import com.lyoko.smartlock.Utils.Database_Helper;
import com.lyoko.smartlock.Utils.LoadingDialog;
import com.lyoko.smartlock.Utils.SetupStatusBar;

import java.util.ArrayList;

import static com.lyoko.smartlock.Utils.LyokoString.DEVICE_ADDRESS;
import static com.lyoko.smartlock.Utils.LyokoString.OWNER_PHONE_NUMBER;

public class CanRemoteDevicesActivity extends LyokoActivity implements iCanRemote, CanRemotePersonInfoAdapter.OnCanRemoteDeviceClickedListener {
    public static final int REQUEST_REMOTE_PHONE_NUMBER = 123;
    Toolbar can_remote_devices_toolbar;
    RecyclerView can_remote_recycle_view;
    Button btn_scan_qr;
    LoadingDialog loadingDialog;
    String current_device_address;
    String owner_phone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_can_remote_devices);
        SetupStatusBar.setup(this);
        can_remote_devices_toolbar = findViewById(R.id.can_remote_devices_toolbar);
        can_remote_recycle_view = findViewById(R.id.can_remote_recycle_view);
        btn_scan_qr = findViewById(R.id.btn_scan_qr);
        loadingDialog = new LoadingDialog(this);
        can_remote_devices_toolbar.setTitle("Quản lý thiết bị được truy cập");
        Bundle bundle = getIntent().getExtras();
        current_device_address = bundle.getString(DEVICE_ADDRESS);
        owner_phone_number = bundle.getString(OWNER_PHONE_NUMBER);

        new Database_Helper().getCanRemoteDevice(current_device_address, this);
        loadingDialog.startLoading("Đang lấy giữ liệu");

        btn_scan_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CanRemoteDevicesActivity.this, BarcodeScannerActivity.class);
                startActivityForResult(intent, REQUEST_REMOTE_PHONE_NUMBER);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_REMOTE_PHONE_NUMBER) {
            if(resultCode == Activity.RESULT_OK) {
                final String dataString = data.getStringExtra(BarcodeScannerActivity.PHONE_NUM);
                String phoneNum = dataString.substring(0,9);
                String authID = dataString.substring(10);
                new Database_Helper().addRemoteDevices(current_device_address,phoneNum,authID);

            }
        }
    }

    @Override
    public void onGetCanRemoteDevices(ArrayList<CanRemotePersonInfo> list) {
        loadingDialog.stopLoading();
        CanRemotePersonInfoAdapter adapter = new CanRemotePersonInfoAdapter(this, list);
        adapter.setOnCanRemoteDeviceClickedListener(this);
        can_remote_recycle_view.setAdapter(adapter);
        can_remote_recycle_view.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void nothingToShow() {
        loadingDialog.stopLoading();
    }

    @Override
    public void onCanRemoteDeviceItemClick(String name, final String phone, final String authid) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_can_remote_device_action,null);
        final ImageView img_call_to_remote_person = view.findViewById(R.id.img_call_to_remote_person);
        final ImageView img_delete_can_remote_device = view.findViewById(R.id.img_delete_can_remote_device);
        final TextView tv_can_remote_device_name = view.findViewById(R.id.tv_can_remote_device_name);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        tv_can_remote_device_name.setText(name);
        img_call_to_remote_person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String call = "+84"+ phone ;
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", call, null));
                startActivity(intent);
                dialog.dismiss();
            }
        });

        img_delete_can_remote_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database_Helper().removeCanRemoteDevice(current_device_address,phone,authid);
                new Database_Helper().getCanRemoteDevice(current_device_address, CanRemoteDevicesActivity.this);
                dialog.dismiss();
            }
        });
        dialog.show();
        Toast.makeText(this, name+": "+phone, Toast.LENGTH_SHORT).show();

    }
}

package com.lyoko.smartlock.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.lyoko.smartlock.Adapters.TotalDevicesAdapter;
import com.lyoko.smartlock.Interface.iAuth;
import com.lyoko.smartlock.LyokoActivity;
import com.lyoko.smartlock.LyokoNotificationService;
import com.lyoko.smartlock.Models.Device_info;
import com.lyoko.smartlock.R;
import com.lyoko.smartlock.Utils.Database_Helper;
import com.lyoko.smartlock.Interface.iDeviceList;
import com.lyoko.smartlock.Utils.DialogShow;
import com.lyoko.smartlock.Utils.SetupStatusBar;

import java.util.ArrayList;

import static com.lyoko.smartlock.Utils.LyokoString.LOGGED_NAME;
import static com.lyoko.smartlock.Utils.LyokoString.LOGGED_PREFERENCE;
import static com.lyoko.smartlock.Utils.LyokoString.phone_login;
import static com.lyoko.smartlock.Utils.LyokoString.phone_name;

public class MainActivity extends LyokoActivity implements iAuth,iDeviceList, TotalDevicesAdapter.OnDeviceClickedListener  {
    Database_Helper db = new Database_Helper();
    RecyclerView device_list_recycle_view, sub_device_list_recycle_view;
    TextView tv6;
    Toolbar devices_toolbar;
    ImageView img_find_device;
    int listEmptyCount = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SetupStatusBar.setup(MainActivity.this);
        tv6 = findViewById(R.id.tv6);
        img_find_device = findViewById(R.id.img_find_device);
        devices_toolbar = findViewById(R.id.devices_toolbar);
        device_list_recycle_view = findViewById(R.id.device_list_recycle_view);
        sub_device_list_recycle_view = findViewById(R.id.sub_device_list_recycle_view);
        setSupportActionBar(devices_toolbar);
        db.getOwnerName(this);
//        startForegroundService(new Intent(this, LyokoNotificationService.class));
        db.getOwnDevices(this,this);
        db.getRemoteDevice(this);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        devices_toolbar.setTitle(phone_name.substring(0,1).toUpperCase() + phone_name.substring(1).toLowerCase()+"'s"+" home");
        getMenuInflater().inflate(R.menu.menu_devices, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_device:
                DialogShow.showAddDeviceMethods(this);
                return true;
                case R.id.menu_my_account:
                    Intent intent = new Intent(MainActivity.this, MyAccountActivity.class);
                    startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showOwnDevices(ArrayList<Device_info> own) {
        TotalDevicesAdapter adapter = new TotalDevicesAdapter(this, own);
        device_list_recycle_view.setAdapter(adapter);
        adapter.setOnDeviceClickedListener(this);
        device_list_recycle_view.setLayoutManager(new GridLayoutManager(this,2));
        device_list_recycle_view.setVisibility(View.VISIBLE);
    }

    @Override
    public void showRemoteDevices(ArrayList<Device_info> remove) {
        TotalDevicesAdapter adapter = new TotalDevicesAdapter(this, remove);
        sub_device_list_recycle_view.setAdapter(adapter);
        adapter.setOnDeviceClickedListener(this);
        sub_device_list_recycle_view.setLayoutManager(new GridLayoutManager(this,2));
    }

    @Override
    public void notThingToShow() {
        listEmptyCount ++;
        if (listEmptyCount == 2){
            img_find_device.setVisibility(View.VISIBLE);
            tv6.setVisibility(View.VISIBLE);
            img_find_device.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listEmptyCount = 0;
                    Intent intent = new Intent(MainActivity.this, AddDeviceActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }


    @Override
    public void onDeviceClickedListener(String owner, String device_address, String device_name, String type) {
        boolean master;
        if (phone_login.equals(owner)) master = true; else master = false;
        DialogShow.showLockFunction(this, owner, device_address, device_name, type, master );
    }

    @Override
    public void onGetName(String name) {
        if (name.equals(phone_name)) {
            SharedPreferences.Editor editor;
            editor = getSharedPreferences(LOGGED_PREFERENCE, MODE_PRIVATE).edit();
            editor.putString(LOGGED_NAME, phone_name).apply();
            phone_name = name;
            devices_toolbar.setTitle(phone_name.substring(0, 1).toUpperCase() + phone_name.substring(1).toLowerCase() + "'s" + " home");
        }
    }
}

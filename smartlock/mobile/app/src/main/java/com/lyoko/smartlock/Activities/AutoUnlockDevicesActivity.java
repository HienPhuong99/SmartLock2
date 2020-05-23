package com.lyoko.smartlock.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lyoko.smartlock.Adapters.TrustedDevicesAdapter;
import com.lyoko.smartlock.Adapters.UnknownDevicesAdapter;
import com.lyoko.smartlock.Interface.iTrustedDevice;
import com.lyoko.smartlock.LyokoActivity;
import com.lyoko.smartlock.Models.BLE_Device;
import com.lyoko.smartlock.R;
import com.lyoko.smartlock.Utils.Database_Helper;
import com.lyoko.smartlock.Utils.LoadingDialog;
import com.lyoko.smartlock.Utils.SetupStatusBar;

import java.util.ArrayList;

import static com.lyoko.smartlock.Utils.LyokoString.DEVICE_ADDRESS;
import static com.lyoko.smartlock.Utils.LyokoString.DEVICE_TYPE;
import static com.lyoko.smartlock.Utils.LyokoString.OWNER_PHONE_NUMBER;
import static com.lyoko.smartlock.Utils.LyokoString.TRUSTED_DEVICES_ADDRESS;
import static com.lyoko.smartlock.Utils.LyokoString.TRUSTED_DEVICES_NAME;
import static com.lyoko.smartlock.Utils.LyokoString.TRUSTED_DEVICES_UPDATE;
import static com.lyoko.smartlock.Utils.LyokoString.phone_login;

public class AutoUnlockDevicesActivity extends LyokoActivity implements UnknownDevicesAdapter.OnUnknownBLEDeviceClickedListener, TrustedDevicesAdapter.OnTrustedDeviceClickedListener, iTrustedDevice {
    private static final int REQUEST_ENABLE_BT = 1;
    Toolbar trusted_devices_toolbar;
    static RecyclerView trusted_device_recycle_view;
    Button btn_add_trusted_device;
    String device_count = "0";
    String add_trusted_device_name, add_trusted_device_address, device_type;
    ArrayList<BLE_Device> unknownList = new ArrayList<>();
    ArrayList<String> _unknownList = new ArrayList<>();
    ArrayList<String> trustedList = new ArrayList<>();
    BluetoothAdapter bluetoothAdapter;
    BluetoothManager bluetoothManager;
    LoadingDialog loadingDialog ;
    private boolean mScanning;
    private Handler handler;
    private static final long SCAN_PERIOD = 3000;
    String current_device_address, owner_phone_number;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_unlock_devices);
        SetupStatusBar.setup(AutoUnlockDevicesActivity.this);
        trusted_devices_toolbar = findViewById(R.id.trusted_devices_toolbar);
        trusted_device_recycle_view = findViewById(R.id.trusted_device_recycle_view);
        btn_add_trusted_device = findViewById(R.id.btn_add_trusted_device);
        loadingDialog = new LoadingDialog(AutoUnlockDevicesActivity.this);
        trusted_devices_toolbar.setTitle("Quản lý thiết bị Auto Unlock");
//        setSupportActionBar(trusted_devices_toolbar);

        Bundle bundle = getIntent().getExtras();
        current_device_address = bundle.getString(DEVICE_ADDRESS);
        owner_phone_number = bundle.getString(OWNER_PHONE_NUMBER);
        device_type = bundle.getString(DEVICE_TYPE);




        new Database_Helper().getTrustedDevice(current_device_address, this);
        loadingDialog.startLoading("Đang lấy giữ liệu");

        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        handler = new Handler();
        mScanning = false;
        btn_add_trusted_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mScanning) scanLeDevice(false);
                loadingDialog.startLoading("Đang tìm kiếm");
                _unknownList.clear();
                unknownList.clear();
                scanLeDevice(true);
            }
        });

    }
    private BluetoothAdapter.LeScanCallback leScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (device.getName() != null )
                            if (!trustedList.contains(device.getName()))
                            if (!_unknownList.contains(device.getAddress()) && rssi > -69){
                                _unknownList.add(device.getAddress());
                                unknownList.add(new BLE_Device(device.getName(),device.getAddress().toLowerCase()));
                            }
                        }
                    });
                }
            };

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothAdapter.stopLeScan(leScanCallback);
                    loadingDialog.stopLoading();
                    showUnknownDevice();
                }
            }, SCAN_PERIOD);
            mScanning = true;
            bluetoothAdapter.startLeScan(leScanCallback);
        } else {
            mScanning = false;
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
    }

    private void showUnknownDevice() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AutoUnlockDevicesActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_scanned_unknown_devices,null);
        RecyclerView unknown_device_recycle_view = view.findViewById(R.id.unknown_device_recycle_view);
        TextView tv_rescan = view.findViewById(R.id.tv_rescan);
        TextView tv_add_trusted_device_confirm = view.findViewById(R.id.tv_add_trusted_device_confirm);
        TextView tv_nothing_to_show = view.findViewById(R.id.tv_nothing_to_show);
        if (unknownList.size()== 0) {
            tv_add_trusted_device_confirm.setEnabled(false);
            tv_nothing_to_show.setVisibility(View.VISIBLE);
        }
        UnknownDevicesAdapter unknownDevicesAdapter = new UnknownDevicesAdapter(this, unknownList);
        unknownDevicesAdapter.setOnUnknownBLEDeviceClickedListener(this);
        unknown_device_recycle_view.setAdapter(unknownDevicesAdapter);
        unknown_device_recycle_view.setLayoutManager(new LinearLayoutManager(this));
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        tv_add_trusted_device_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AutoUnlockDevicesActivity.this, "Thêm Thành Công", Toast.LENGTH_SHORT).show();
                new Database_Helper().addTrustedDevice(current_device_address, device_count, add_trusted_device_name, add_trusted_device_address, device_type);
                dialog.dismiss();
            }
        });

        tv_rescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                loadingDialog.startLoading("Đang tìm lại");
                _unknownList.clear();
                unknownList.clear();
                scanLeDevice(true);
            }
        });
        dialog.show();

    }

    @Override
    public void onUnknownBLEDeviceClickedListener(final String ble_name, final String ble_address) {
        add_trusted_device_name = ble_name;
        add_trusted_device_address = ble_address;

    }

    @Override
    public void onTrustedDeviceClickedListener(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AutoUnlockDevicesActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_remove_trusted_device,null);
        TextView tv_trusted_device_name = view.findViewById(R.id.tv_trusted_device_name);
        TextView tv_dialog_remove_trusted_device_cancel = view.findViewById(R.id.tv_dialog_remove_trusted_device_cancel);
        TextView tv_dialog_remove_trusted_device_confirm = view.findViewById(R.id.tv_dialog_remove_trusted_device_confirm);

        tv_trusted_device_name.setText("Bạn có muốn xóa thiết bị "+ trustedList.get(position)+ " ra khỏi danh sách thiết bị tin cậy không?");

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        tv_dialog_remove_trusted_device_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trustedList.remove(position);
                UnknownDevicesAdapter unknownDevicesAdapter = new UnknownDevicesAdapter(AutoUnlockDevicesActivity.this, unknownList);
                unknownDevicesAdapter.setOnUnknownBLEDeviceClickedListener(AutoUnlockDevicesActivity.this);
                trusted_device_recycle_view.setAdapter(unknownDevicesAdapter);
                trusted_device_recycle_view.setLayoutManager(new LinearLayoutManager(AutoUnlockDevicesActivity.this));
                Toast.makeText(AutoUnlockDevicesActivity.this, "Xóa Thành Công", Toast.LENGTH_SHORT).show();
                new Database_Helper().updateTrustedDevices(position,current_device_address,TRUSTED_DEVICES_ADDRESS);
                new Database_Helper().updateTrustedDevices(position,current_device_address,TRUSTED_DEVICES_NAME);
                new Database_Helper().changed_update_code(owner_phone_number,current_device_address,device_type,TRUSTED_DEVICES_UPDATE);

                dialog.dismiss();
            }
        });

        tv_dialog_remove_trusted_device_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    @Override
    public void showTrustedDevice(ArrayList<String> list) {
        trustedList = list;
        loadingDialog.stopLoading();
        device_count = String.valueOf(list.size());
        TrustedDevicesAdapter adapter = new TrustedDevicesAdapter(AutoUnlockDevicesActivity.this, trustedList);
        adapter.setOnTrustedDeviceClickedListener(this);
        trusted_device_recycle_view.setAdapter(adapter);
        trusted_device_recycle_view.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void noDeviceToShow() {
        Toast.makeText(AutoUnlockDevicesActivity.this, "Không có gì", Toast.LENGTH_SHORT).show();

    }
}

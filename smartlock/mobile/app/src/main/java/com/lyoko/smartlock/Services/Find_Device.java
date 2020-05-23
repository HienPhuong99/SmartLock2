package com.lyoko.smartlock.Services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;

import com.lyoko.smartlock.Interface.iFindLock;



public class Find_Device {
    private String device_address ;
    private BluetoothAdapter bluetoothAdapter;
    private Context context;
    private Boolean isFound;
    private static final long SCAN_PERIOD = 2000;
    private static final int SIGNAL_STRENGTH = -65;
    boolean mScanning;
    Handler handler;
    iFindLock iFindLock;

    public Find_Device(Context context, String device_address, BluetoothAdapter adapter, iFindLock iFindLock) {
        this.context = context;
        this.device_address = device_address;
        this.iFindLock = iFindLock;
        this.mScanning = false;
        isFound = false;
        handler = new Handler();
        this.bluetoothAdapter = adapter;

    }

    private final BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (rssi > SIGNAL_STRENGTH){
                            if (device.getAddress().equalsIgnoreCase(device_address) && !isFound){
                                isFound = true;
                                iFindLock.onDeviceFound(device);
                            }
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
                    if (!isFound) iFindLock.onDeviceNotFound();
                    bluetoothAdapter.stopLeScan(leScanCallback);
                }
            }, SCAN_PERIOD);
            mScanning = true;
            bluetoothAdapter.startLeScan(leScanCallback);

        } else {
            mScanning = false;
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
    }

    public void startScan() {

        scanLeDevice(true);
    }

    public void stopScan() {
        scanLeDevice(false);
    }



}

package com.lyoko.smartlock.Models;

import android.bluetooth.BluetoothDevice;

public class BLE_Device {

    String ble_name, ble_address;

    public BLE_Device(String ble_name, String ble_address) {
        this.ble_name = ble_name;
        this.ble_address = ble_address;
    }

    public String getBle_name() {
        return ble_name;
    }

    public void setBle_name(String ble_name) {
        this.ble_name = ble_name;
    }

    public String getBle_address() {
        return ble_address;
    }

    public void setBle_address(String ble_address) {
        this.ble_address = ble_address;
    }
}

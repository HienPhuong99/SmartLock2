package com.lyoko.smartlock.Services;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import static com.lyoko.smartlock.Utils.FormatData.hexToString;
import static com.lyoko.smartlock.Utils.LyokoString.CHARACTERISTIC_APPLICATION_COMMAND_UUID;
import static com.lyoko.smartlock.Utils.LyokoString.CHARACTERISTIC_CHIP_ID_UUID;
import static com.lyoko.smartlock.Utils.LyokoString.CHARACTERISTIC_CLIENT_CONFIG_UUID;
import static com.lyoko.smartlock.Utils.LyokoString.CHARACTERISTIC_OWNER_PHONE_NUMBER_UUID;
import static com.lyoko.smartlock.Utils.LyokoString.CHARACTERISTIC_RESPONSE_UUID;
import static com.lyoko.smartlock.Utils.LyokoString.CHARACTERISTIC_WIFI_CREDENTIAL_UUID;
import static com.lyoko.smartlock.Utils.LyokoString.SERVICE_LYOKO_UUID;
import static com.lyoko.smartlock.Utils.LyokoString.add_device_address;
import static com.lyoko.smartlock.Utils.LyokoString.add_device_name;
import static com.lyoko.smartlock.Utils.LyokoString.phone_login;
import static com.lyoko.smartlock.Utils.LyokoString.phone_login_password;

public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();
    private BluetoothGatt gatt;
    private com.lyoko.smartlock.Interface.iFindLock iFindLock;
    private int mConnectionState = STATE_DISCONNECTED;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private Context context;
    private  boolean autoConnect;

    private BluetoothDevice bluetoothDevice;
    BluetoothGattCharacteristic wifiCredential, ownerPhoneNumber, response, chipID, command;





    public BluetoothLeService(Context context, boolean autoConnect, BluetoothDevice bluetoothDevice, com.lyoko.smartlock.Interface.iFindLock iFindLock) {
        this.context = context;
        this.autoConnect = autoConnect;
        this.iFindLock = iFindLock;
        this.bluetoothDevice = bluetoothDevice;

    }
    public void connectDevice(){
        gatt = bluetoothDevice.connectGatt(context,autoConnect, gattCallback);
    }
    public void close(){
        if (gatt == null) {
            return;
        }
        gatt.close();
        gatt = null;
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery:" +
                        gatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT server.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                iFindLock.onConnected();
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            enableNotify(response);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            add_device_address = chipID.getStringValue(0);
             Log.d("chipID",add_device_address);
             command.setValue("143");
             gatt.writeCharacteristic(command);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            int format = BluetoothGattCharacteristic.FORMAT_UINT8;
            String responseCode = String.format(characteristic.getIntValue(format, 0).toString());
            Log.d("Value", responseCode);
            switch (responseCode) {
                case "1":

                        ownerPhoneNumber.setValue(phone_login+"|"+phone_login_password+"*"+add_device_name);
                        gatt.writeCharacteristic(ownerPhoneNumber);
                        break;


                case "2":
                    gatt.readCharacteristic(chipID);
                    command.setValue("143");
                    break;
                case "3":
                    iFindLock.onComplete();
                    break;
            }

        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void enableNotify(BluetoothGattCharacteristic characteristic){
        gatt.setCharacteristicNotification(characteristic,true);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                CHARACTERISTIC_CLIENT_CONFIG_UUID);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descriptor);
    }

    public void disableNotify(){
        gatt.setCharacteristicNotification(response,false);
        BluetoothGattDescriptor descriptor = response.getDescriptor(
                CHARACTERISTIC_CLIENT_CONFIG_UUID);
        descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descriptor);
    }


    public void sendInitialInfo(String wifi_ssid, String wifi_password) {
        response = gatt.getService(SERVICE_LYOKO_UUID).getCharacteristic(CHARACTERISTIC_RESPONSE_UUID);
        command = gatt.getService(SERVICE_LYOKO_UUID).getCharacteristic(CHARACTERISTIC_APPLICATION_COMMAND_UUID);
        chipID = gatt.getService(SERVICE_LYOKO_UUID).getCharacteristic(CHARACTERISTIC_CHIP_ID_UUID);
        ownerPhoneNumber = gatt.getService(SERVICE_LYOKO_UUID).getCharacteristic(CHARACTERISTIC_OWNER_PHONE_NUMBER_UUID);
        wifiCredential = gatt.getService(SERVICE_LYOKO_UUID).getCharacteristic(CHARACTERISTIC_WIFI_CREDENTIAL_UUID);
        wifiCredential.setValue(wifi_ssid+"|"+wifi_password);
        gatt.writeCharacteristic(wifiCredential);

    }

    public void sendWifi(String wifi_ssid, String wifi_password) {
        response = gatt.getService(SERVICE_LYOKO_UUID).getCharacteristic(CHARACTERISTIC_RESPONSE_UUID);
        command = gatt.getService(SERVICE_LYOKO_UUID).getCharacteristic(CHARACTERISTIC_APPLICATION_COMMAND_UUID);
        wifiCredential = gatt.getService(SERVICE_LYOKO_UUID).getCharacteristic(CHARACTERISTIC_WIFI_CREDENTIAL_UUID);
        wifiCredential.setValue(wifi_ssid+"|"+wifi_password);
    }
}

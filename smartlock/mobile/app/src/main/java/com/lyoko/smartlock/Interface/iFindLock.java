package com.lyoko.smartlock.Interface;

        import android.bluetooth.BluetoothDevice;

public interface iFindLock {
    void onDeviceFound(BluetoothDevice device);
    void onDeviceNotFound();
    void onConnected();
    void onComplete();

}

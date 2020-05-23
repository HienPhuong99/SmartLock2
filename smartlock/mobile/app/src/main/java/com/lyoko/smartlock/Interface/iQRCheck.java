package com.lyoko.smartlock.Interface;

public interface iQRCheck {
    void onNotOwner(String address);
    void onAsOwner(String address);
    void onReadyToAddDevice(String address);

}

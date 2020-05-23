package com.lyoko.smartlock.Models;

public class Device_settings {
    String device_name, device_address, type;
    int delay_unlock, otp_limit_entry;



    public Device_settings(String device_name, String device_address, String type, int delay_unlock, int otp_limit_entry) {
        this.device_name = device_name;
        this.device_address = device_address;
        this.type = type;
        this.delay_unlock = delay_unlock;
        this.otp_limit_entry = otp_limit_entry;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDevice_address() {
        return device_address;
    }

    public void setDevice_address(String device_address) {
        this.device_address = device_address;
    }

    public int getDelay_unlock() {
        return delay_unlock;
    }

    public void setDelay_unlock(int delay_unlock) {
        this.delay_unlock = delay_unlock;
    }

    public int getOtp_limit_entry() {
        return otp_limit_entry;
    }

    public void setOtp_limit_entry(int otp_limit_entry) {
        this.otp_limit_entry = otp_limit_entry;
    }
}

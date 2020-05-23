package com.lyoko.smartlock.Models;


public class NewSMLock {
    String  device_name, device_type, otp;
    int state, delay, update_code, otp_limit_entry;

    public NewSMLock(String device_name, String device_type, String otp, int state, int delay, int update_code, int otp_limit_entry) {
        this.device_name = device_name;
        this.device_type = device_type;
        this.otp = otp;
        this.state = state;
        this.delay = delay;
        this.update_code = update_code;
        this.otp_limit_entry = otp_limit_entry;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getUpdate_code() {
        return update_code;
    }

    public void setUpdate_code(int update_code) {
        this.update_code = update_code;
    }

    public int getOtp_limit_entry() {
        return otp_limit_entry;
    }

    public void setOtp_limit_entry(int otp_limit_entry) {
        this.otp_limit_entry = otp_limit_entry;
    }
}

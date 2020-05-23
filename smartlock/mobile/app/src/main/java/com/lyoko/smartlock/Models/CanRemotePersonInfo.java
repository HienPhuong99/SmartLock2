package com.lyoko.smartlock.Models;

public class CanRemotePersonInfo {
    String phoneNumber;
    String name;
    String authid;


    public CanRemotePersonInfo(String phoneNumber, String name, String authid) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.authid = authid;
    }

    public CanRemotePersonInfo(String phoneNumber, String authid) {
        this.phoneNumber = phoneNumber;
        this.authid = authid;
    }

    public String getAuthid() {
        return authid;
    }

    public void setAuthid(String authid) {
        this.authid = authid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

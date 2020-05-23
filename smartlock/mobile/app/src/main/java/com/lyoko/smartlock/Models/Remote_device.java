package com.lyoko.smartlock.Models;

public class Remote_device {
    String ownerPhoneNumber, address, device_name;


    public Remote_device(String ownerPhoneNumber, String address, String device_name) {
        this.ownerPhoneNumber = ownerPhoneNumber;
        this.address = address;
        this.device_name = device_name;
    }

    public Remote_device(String ownerPhoneNumber, String address) {
        this.ownerPhoneNumber = ownerPhoneNumber;
        this.address = address;
    }

    public String getOwnerPhoneNumber() {
        return ownerPhoneNumber;
    }

    public void setOwnerPhoneNumber(String ownerPhoneNumber) {
        this.ownerPhoneNumber = ownerPhoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }
}

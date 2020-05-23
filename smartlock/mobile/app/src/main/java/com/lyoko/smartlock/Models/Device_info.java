package com.lyoko.smartlock.Models;

public class Device_info {
    String owner,address,name, type;
    int state;


    public Device_info(String owner, String address, String name, String type, int state) {
        this.owner = owner;
        this.address = address;
        this.name = name;
        this.type = type;
        this.state = state;
    }

    public Device_info(String owner, String address ) {
        this.owner = owner;
        this.address = address;
    }

    public Device_info(String owner, String address, String type) {
        this.owner = owner;
        this.address = address;
        this.type = type;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}

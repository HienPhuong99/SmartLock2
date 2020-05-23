package com.lyoko.smartlock.Models;

public class NewUser {
    String owner_name, password, auth_id;

    public NewUser(String owner_name, String password, String auth_id) {
        this.owner_name = owner_name;
        this.password = password;
        this.auth_id = auth_id;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuth_id() {
        return auth_id;
    }

    public void setAuth_id(String auth_id) {
        this.auth_id = auth_id;
    }
}

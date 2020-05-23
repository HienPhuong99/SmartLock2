package com.lyoko.smartlock.Models;




public class History {
    private String unlock_name;
    private String unlock_time;
    private String unlock_type;

    public History(String unlock_name, String unlock_time, String unlock_type) {
        this.unlock_name = unlock_name;
        this.unlock_time = unlock_time;
        this.unlock_type = unlock_type;
    }

    public String getUnlock_name() {
        return unlock_name;
    }

    public String getUnlock_time() {
        return unlock_time;
    }

    public String getUnlock_type() {
        return unlock_type;
    }
}

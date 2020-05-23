package com.lyoko.smartlock;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.lyoko.smartlock.Interface.iTimeOut;
import com.lyoko.smartlock.Utils.Database_Helper;

import java.util.Timer;
import java.util.TimerTask;

public class Lyoko extends Application {

    private iTimeOut timeOut;
    private Timer timer;



    public void startLoginSession() {
        if (timer != null) timer.cancel();
        Log.d("timer", "bắt đầu");
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeOut.onLoginSessionTimeOut();
            }
        },120000);
    }

    public void onUserInteracted() {
        startLoginSession();
    }

    public void checkTimer(){
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void registerLoginSession(iTimeOut iTimeOut) {
        this.timeOut = iTimeOut;
    }



}

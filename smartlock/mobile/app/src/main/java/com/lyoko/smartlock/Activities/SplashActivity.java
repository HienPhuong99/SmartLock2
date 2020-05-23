package com.lyoko.smartlock.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.lyoko.smartlock.R;

import static com.lyoko.smartlock.Utils.LyokoString.COLOR_BLUE;
import static com.lyoko.smartlock.Utils.LyokoString.LOGGED_NAME;
import static com.lyoko.smartlock.Utils.LyokoString.LOGGED_PHONE;
import static com.lyoko.smartlock.Utils.LyokoString.LOGGED_PREFERENCE;
import static com.lyoko.smartlock.Utils.LyokoString.LOGIN_SAVED;
import static com.lyoko.smartlock.Utils.LyokoString.phone_login;
import static com.lyoko.smartlock.Utils.LyokoString.phone_name;

public class SplashActivity extends AppCompatActivity {
    private  static int SPLASH_TIME_OUT=1500;
    private SharedPreferences loginPreferences;
    private String LoggedPhoneNumber, LoggedName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!LoggedPhoneNumber.equals("")){
                    LoggedName = loginPreferences.getString(LOGGED_NAME, "");
                    phone_login = LoggedPhoneNumber;
                    phone_name = LoggedName;
                    Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                    intent.putExtra(LOGIN_SAVED, true);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this,CheckPhoneNumberActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },SPLASH_TIME_OUT);
        getWindow().setStatusBarColor(COLOR_BLUE);
        loginPreferences = getSharedPreferences(LOGGED_PREFERENCE, MODE_PRIVATE);
        LoggedPhoneNumber = loginPreferences.getString(LOGGED_PHONE, "");
    }
}

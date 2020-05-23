package com.lyoko.smartlock;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.lyoko.smartlock.Activities.LoginActivity;
import com.lyoko.smartlock.Interface.iTimeOut;

import static com.lyoko.smartlock.Utils.LyokoString.LOGIN_SAVED;

public class LyokoActivity extends AppCompatActivity implements iTimeOut {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((Lyoko)getApplication()).registerLoginSession(this);
        ((Lyoko)getApplication()).startLoginSession();

    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        ((Lyoko)getApplication()).onUserInteracted();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((Lyoko)getApplication()).startLoginSession();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((Lyoko)getApplication()).checkTimer();
    }

    @Override
    public void onLoginSessionTimeOut() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(LyokoActivity.this);
                LayoutInflater inflater = LyokoActivity.this.getLayoutInflater();
                final View view = inflater.inflate(R.layout.dialog_login_timeout,null);
                TextView tv_out = view.findViewById(R.id.tv_out);
                TextView tv_reLogin = view.findViewById(R.id.tv_reLogin);
                builder.setView(view);
                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                tv_out.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id= android.os.Process.myPid();
                        android.os.Process.killProcess(id);
                    }
                });
                tv_reLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LyokoActivity.this, LoginActivity.class);
                        intent .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(LOGIN_SAVED, true);
                        startActivity(intent);
                        finish();
                    }
                });
                dialog.show();
            }
        });

    }
}

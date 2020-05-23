package com.lyoko.smartlock.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.lyoko.smartlock.R;

public class DeniedDialog {
    Activity activity;
    AlertDialog dialog;
    TextView success_message;

    public DeniedDialog(Activity activity) {
        this.activity = activity;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_denied,null);
        success_message = view.findViewById(R.id.denied_message);
        builder.setView(view);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void startLoading(String message, int delay){
        success_message.setText(message);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        },delay);
        dialog.show();
    }


}

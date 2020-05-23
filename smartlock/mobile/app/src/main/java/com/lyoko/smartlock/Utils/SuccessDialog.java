package com.lyoko.smartlock.Utils;

import android.app.Activity;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.lyoko.smartlock.R;

public class SuccessDialog {
    Activity activity;
    AlertDialog dialog;

    public SuccessDialog(Activity activity) {
        this.activity = activity;
    }

    public void startLoading(String message, int delay){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_success,null);
        TextView success_message = view.findViewById(R.id.success_message);
        success_message.setText(message);
        builder.setView(view);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        },delay);
        dialog.show();
    }

}

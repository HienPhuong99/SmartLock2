package com.lyoko.smartlock.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.lyoko.smartlock.R;

public class Permission {
    private static Context context;
    public static final int REQUEST_PERMISSION = 111;
    public static final int REQUEST_CAMERA_PERMISSION = 222;

    public Permission(Context context) {
        this.context = context;
    }

    public static void getPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ){
            new AlertDialog.Builder(context)
                    .setTitle("Thông Báo Quan Trọng")
                    .setMessage("Chúng tôi cần bạn cấp quyền để có thể tìm Lyoko Smart Lock")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions((Activity) context,
                                    new String[] {Manifest.permission.CAMERA,Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSION);
                        }
                    })
                    .create()
                    .show();
        }
    }



    public boolean checkBLESupport() {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            ((Activity) context).finish();
            return false;
        } else{
            return true;
        }
    }
}
    


package com.lyoko.smartlock.Utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lyoko.smartlock.Activities.AddDeviceActivity;
import com.lyoko.smartlock.Activities.AutoUnlockDevicesActivity;
import com.lyoko.smartlock.Activities.CanRemoteDevicesActivity;
import com.lyoko.smartlock.Activities.HistoryActivity;
import com.lyoko.smartlock.Activities.LockSettingsActivity;
import com.lyoko.smartlock.Models.Device_settings;
import com.lyoko.smartlock.R;

import static android.widget.Toast.LENGTH_SHORT;
import static com.lyoko.smartlock.Utils.LyokoString.DELAY;
import static com.lyoko.smartlock.Utils.LyokoString.DEVICE_ADDRESS;
import static com.lyoko.smartlock.Utils.LyokoString.DEVICE_NAME;
import static com.lyoko.smartlock.Utils.LyokoString.DEVICE_TYPE;
import static com.lyoko.smartlock.Utils.LyokoString.OTP_LIMIT_ENTRY;
import static com.lyoko.smartlock.Utils.LyokoString.OTP_NOT_SAVE;
import static com.lyoko.smartlock.Utils.LyokoString.OTP_NULL;
import static com.lyoko.smartlock.Utils.LyokoString.OTP_REMOVED;
import static com.lyoko.smartlock.Utils.LyokoString.OTP_SAVED;
import static com.lyoko.smartlock.Utils.LyokoString.OTP_SHARE_MESSAGE;
import static com.lyoko.smartlock.Utils.LyokoString.OTP_SHARE_SUB_MESSAGE;
import static com.lyoko.smartlock.Utils.LyokoString.OTP_SHOW;
import static com.lyoko.smartlock.Utils.LyokoString.OTP_UPDATE;
import static com.lyoko.smartlock.Utils.LyokoString.OTP_UP_TO_DATE;
import static com.lyoko.smartlock.Utils.LyokoString.OWNER_PHONE_NUMBER;
import static com.lyoko.smartlock.Utils.LyokoString.auth_id;
import static com.lyoko.smartlock.Utils.LyokoString.phone_login;

public class DialogShow {

    public static void showMyQR(Activity activity) {
        QRCodeWriter writer = new QRCodeWriter();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_myqr,null);
        final ImageView img_myQR = view.findViewById(R.id.img_myQR);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        try {
            BitMatrix bitMatrix = writer.encode(phone_login+","+auth_id, BarcodeFormat.QR_CODE, 512, 512);
            Bitmap bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565);
            for (int x = 0; x<512; x++){
                for (int y=0; y<512; y++){
                    bitmap.setPixel(x,y,bitMatrix.get(x,y)? Color.BLACK : Color.WHITE);
                }
            }
            img_myQR.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.show();
    }

    public static void showAddDeviceMethods(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_add_new_device_method,null);
        final ImageView img_add_normally = view.findViewById(R.id.img_add_normally);
        final ImageView img_add_via_qr = view.findViewById(R.id.img_add_via_qr);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        img_add_normally.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AddDeviceActivity.class);
                activity.startActivity(intent);
                dialog.dismiss();
            }
        });

        img_add_via_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showMyQR(activity);
            }
        });
        dialog.show();
    }

    public static void showOTP(final Activity activity, final String device_owner_phoneNumber, final String device_address, String device_name, final String type){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_otp_show,null);
        final ImageView img_share = view.findViewById(R.id.img_share);
        final ImageView img_remove_otp = view.findViewById(R.id.img_remove_otp);
        final TextView tv_otp = view.findViewById(R.id.tv_otp);
        final TextView tv_generate_new_otp = view.findViewById(R.id.tv_generate_new_otp);
        final TextView tv_confirm_otp_changed = view.findViewById(R.id.tv_confirm_otp_changed);
        final AlertDialog dialog = builder.setView(view).create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (GetOTP_Helper.otp.equals("")) tv_otp.setText(OTP_NULL);
        else tv_otp.setText(OTP_SHOW + GetOTP_Helper.otp);

        img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GetOTP_Helper.otp_save){
                    Toast.makeText(activity, OTP_NOT_SAVE, LENGTH_SHORT).show();
                } else {
                    Intent sentIntent = new Intent(Intent.ACTION_SEND);
                    sentIntent.putExtra(Intent.EXTRA_TEXT,
                            OTP_SHARE_MESSAGE + tv_otp.getText().toString() + OTP_SHARE_SUB_MESSAGE);
                    sentIntent.setType("text/plain");
                    Intent chooser = Intent.createChooser(sentIntent, "Chia sẻ OTP: ");
                    if (sentIntent.resolveActivity(activity.getPackageManager()) != null) {
                        activity.startActivity(chooser);
                    }
                }

            }
        });

        img_remove_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetOTP_Helper.otp = "";
                GetOTP_Helper.otp_save = true;
                new Database_Helper().setOTP(device_address, type,GetOTP_Helper.otp);
                new Database_Helper().changed_update_code(device_owner_phoneNumber,device_address, type, OTP_UPDATE);
                tv_otp.setText(OTP_NULL);
                Toast.makeText(activity, OTP_REMOVED, LENGTH_SHORT).show();
            }
        });

        tv_confirm_otp_changed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GetOTP_Helper.otp_save){
                    GetOTP_Helper.otp_save = true;
                    new Database_Helper().setOTP(device_address,type, GetOTP_Helper.otp);
                    new Database_Helper().changed_update_code(device_owner_phoneNumber,device_address, type, OTP_UPDATE);
                    Toast.makeText(activity, OTP_SAVED, LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, OTP_UP_TO_DATE, LENGTH_SHORT).show();
                }

            }
        });
        tv_generate_new_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetOTP_Helper.otp_save  = false;
                tv_otp.setText(OTP_SHOW + GetOTP_Helper.generateOTP());
            }
        });

        dialog.show();
    }

    public static void showLockFunction(final Activity activity, final String device_owner_phoneNumber, final String device_address, final String device_name, final String device_type, final boolean master) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_lock,null);
        final TextView tv_dialog_lock_name = view.findViewById(R.id.tv_dialog_lock_name);
        final TextView tv_dialog_lock_setting = view.findViewById(R.id.tv_dialog_lock_setting);
        final ImageView img_dialog_lock_histories = view.findViewById(R.id.img_dialog_lock_histories);
        final ImageView img_dialog_lock_otp = view.findViewById(R.id.img_dialog_lock_otp);
        final ImageView img_dialog_controller_device = view.findViewById(R.id.img_dialog_controller_device);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        tv_dialog_lock_name.setText(device_name);

        tv_dialog_lock_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!master) new DeniedDialog(activity).startLoading("Từ chối truy cập",1000);
                else {
                    new Database_Helper().getDeviceSettings(activity,device_address,device_type);
                    dialog.dismiss();
                }

            }
        });

        img_dialog_lock_histories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, HistoryActivity.class);
                intent.putExtra(DEVICE_ADDRESS, device_address);
                intent.putExtra(OWNER_PHONE_NUMBER, device_owner_phoneNumber);
                activity.startActivity(intent);
                dialog.dismiss();
            }
        });

        img_dialog_lock_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!master) new DeniedDialog(activity).startLoading("Từ chối truy cập",1000);
                else {
                    dialog.dismiss();
                    showOTP(activity, device_owner_phoneNumber, device_address, device_name, device_type);
                }
            }
        });

        img_dialog_controller_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!master) new DeniedDialog(activity).startLoading("Từ chối truy cập",1000);
                else {
                    showDevicesManagement(activity, device_owner_phoneNumber, device_address, device_type, device_name);
                    dialog.dismiss();
                }

            }
        });
        dialog.show();
    }

    public static void showDevicesManagement(final Activity activity, final String device_owner_phoneNumber, final String device_address, final String device_type, String deviceName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_add_controller_device_method,null);
        final ImageView img_scan_trusted_devices = view.findViewById(R.id.img_scan_trusted_devices);
        final ImageView img_scan_qr = view.findViewById(R.id.img_scan_qr);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        img_scan_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, CanRemoteDevicesActivity.class);
                intent.putExtra(DEVICE_ADDRESS, device_address);
                intent.putExtra(OWNER_PHONE_NUMBER, device_owner_phoneNumber);
                activity.startActivity(intent);
                dialog.dismiss();
            }
        });

        img_scan_trusted_devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AutoUnlockDevicesActivity.class );
                intent.putExtra(DEVICE_ADDRESS, device_address);
                intent.putExtra(OWNER_PHONE_NUMBER, device_owner_phoneNumber);
                intent.putExtra(DEVICE_TYPE, device_type);
                activity.startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void onGetDeviceSettings(Activity activity, Device_settings device_settings){
        Intent intent = new Intent(activity, LockSettingsActivity.class);
        intent.putExtra(DEVICE_ADDRESS, device_settings.getDevice_address());
        intent.putExtra(DEVICE_NAME, device_settings.getDevice_name());
        intent.putExtra(DELAY, device_settings.getDelay_unlock());
        intent.putExtra(OTP_LIMIT_ENTRY, device_settings.getOtp_limit_entry());
        intent.putExtra(DEVICE_TYPE, device_settings.getType());
        activity.startActivity(intent);
    }

}

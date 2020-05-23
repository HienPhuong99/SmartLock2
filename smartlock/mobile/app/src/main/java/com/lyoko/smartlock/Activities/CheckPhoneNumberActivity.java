package com.lyoko.smartlock.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lyoko.smartlock.R;
import com.lyoko.smartlock.Utils.Database_Helper;
import com.lyoko.smartlock.Interface.iCheckPhoneNumber;

import static com.lyoko.smartlock.Utils.LyokoString.COLOR_GRAY;
import static com.lyoko.smartlock.Utils.LyokoString.COLOR_UNLOCK;
import static com.lyoko.smartlock.Utils.LyokoString.LOGIN;
import static com.lyoko.smartlock.Utils.LyokoString.REGISTER;
import static com.lyoko.smartlock.Utils.LyokoString.VERIFIED_MODE;
import static com.lyoko.smartlock.Utils.LyokoString.phone_login;
import static com.lyoko.smartlock.Utils.LyokoString.PHONE_NUMBER_UNSUITABLE;
import static com.lyoko.smartlock.Utils.LyokoString.phone_name;

public class CheckPhoneNumberActivity extends AppCompatActivity implements iCheckPhoneNumber {
    Database_Helper db_helper = new Database_Helper();
    public EditText et_phoneNumForCheck;
    public Button btn_checkPhoneNum;
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_sign);
        et_phoneNumForCheck = findViewById(R.id.et_phoneNumForCheck);
        btn_checkPhoneNum = findViewById(R.id.btn_checkPhoneNum);

        btn_checkPhoneNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = et_phoneNumForCheck.getText().toString().trim();
                if (s == null || s == "" || s.length()<10){
                    Toast.makeText(CheckPhoneNumberActivity.this, PHONE_NUMBER_UNSUITABLE, Toast.LENGTH_SHORT).show();
                    return;
                }
                phoneNumber = String.valueOf(Long.parseLong(s));
                db_helper.checkPhoneNumber(phoneNumber, CheckPhoneNumberActivity.this);
            }
        });

    }


    @Override
    public void phoneNumExist() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.sign_in_dialog,null);
        TextView dialogPhone = view.findViewById(R.id.tv_sign_in_dialog);
        dialogPhone.setText(et_phoneNumForCheck.getText().toString().trim());
        builder.setView(view)
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        phone_login = phoneNumber;
                        Intent intent = new Intent(CheckPhoneNumberActivity.this, AuthenticationActivity.class);
                        intent.putExtra(VERIFIED_MODE, LOGIN);
                        startActivity(intent);
                        finish();
                    }
                });
        builder.create().show();
    }

    @Override
    public void phoneNumNotExist() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.register_dialog,null);
        TextView dialogPhone = view.findViewById(R.id.tv_register_dialog);
        final TextView tv_dialogBack = view.findViewById(R.id.tv_back_dialog);
        final TextView tv_dialogContinue = view.findViewById(R.id.tv_continue_dialog);
        final CheckBox checkBox = view.findViewById(R.id.cb_checkbox);
        final AlertDialog dialog = builder.setView(view).create();
        dialogPhone.setText(et_phoneNumForCheck.getText().toString().trim());
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBox.isChecked()){
                    tv_dialogContinue.setEnabled(false);
                    tv_dialogContinue.setTextColor(COLOR_GRAY);
                } else{
                    tv_dialogContinue.setEnabled(true);
                    tv_dialogContinue.setTextColor(COLOR_UNLOCK);
                }
            }
        });
        tv_dialogBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_dialogContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone_login = phoneNumber;
                Intent intent = new Intent(CheckPhoneNumberActivity.this, AuthenticationActivity.class);
                intent.putExtra(VERIFIED_MODE, REGISTER);
                startActivity(intent);
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }
}

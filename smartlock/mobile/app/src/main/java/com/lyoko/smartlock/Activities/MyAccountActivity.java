package com.lyoko.smartlock.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.lyoko.smartlock.LyokoActivity;
import com.lyoko.smartlock.R;
import com.lyoko.smartlock.Utils.Database_Helper;
import com.lyoko.smartlock.Utils.CheckView;
import com.lyoko.smartlock.Utils.SetupStatusBar;
import com.lyoko.smartlock.Utils.SuccessDialog;

import static com.lyoko.smartlock.Utils.LyokoString.LOGGED_PHONE;
import static com.lyoko.smartlock.Utils.LyokoString.LOGGED_PREFERENCE;
import static com.lyoko.smartlock.Utils.LyokoString.LOGIN_SAVED;
import static com.lyoko.smartlock.Utils.LyokoString.NOT_EMPTY;
import static com.lyoko.smartlock.Utils.LyokoString.RESET;
import static com.lyoko.smartlock.Utils.LyokoString.VERIFIED_MODE;
import static com.lyoko.smartlock.Utils.LyokoString.phone_name;

public class MyAccountActivity extends LyokoActivity {
    LinearLayout my_name, change_password, logout;
    TextView tv_current_name;
    SharedPreferences.Editor editor;
    SuccessDialog successDialog;
    Toolbar account_toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        SetupStatusBar.setup(this);
        my_name = findViewById(R.id.my_name);
        change_password = findViewById(R.id.change_password);
        logout = findViewById(R.id.logout);
        tv_current_name = findViewById(R.id.tv_current_name);
        account_toolbar = findViewById(R.id.account_toolbar);
        account_toolbar.setTitle("Tài khoản của tôi");
        tv_current_name.setText(phone_name);
        editor = getSharedPreferences(LOGGED_PREFERENCE, MODE_PRIVATE).edit();
        successDialog = new SuccessDialog(this);
        my_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeName();
            }
        });

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, AuthenticationActivity.class);
                intent.putExtra(VERIFIED_MODE, RESET);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout_action();
            }
        });
    }

    private void changeName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_change_device_name,null);
        final TextView tv_dialog_change_name_cancel = view.findViewById(R.id.tv_dialog_change_name_cancel);
        final TextView dialog_change_device_name_title = view.findViewById(R.id.dialog_change_device_name_title);
        final TextView tv_dialog_change_name_confirm = view.findViewById(R.id.tv_dialog_change_name_confirm);
        final EditText et_new_device_name = view.findViewById(R.id.et_new_device_name);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog_change_device_name_title.setText("đổi tên hiển thị");
        tv_dialog_change_name_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckView.isEmpty(et_new_device_name)){
                    et_new_device_name.setError(NOT_EMPTY);
                    return;
                }
                new Database_Helper().change_name(et_new_device_name.getText().toString());
                dialog.dismiss();
                successDialog.startLoading("Sửa tên thành công", 800);
            }
        });

        tv_dialog_change_name_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    private void logout_action() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_logout,null);
        final TextView tv_close_app = view.findViewById(R.id.tv_close_app);
        final TextView tv_exit = view.findViewById(R.id.tv_exit);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        tv_close_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyAccountActivity.this, LoginActivity.class);
                i.putExtra(LOGIN_SAVED, true);
                startActivity(i);
                finish();
            }
        });

        tv_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 editor.remove(LOGGED_PHONE).apply();
                 FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(MyAccountActivity.this, CheckPhoneNumberActivity.class);
                startActivity(i);
                finish();

            }
        });

        dialog.show();
    }


}

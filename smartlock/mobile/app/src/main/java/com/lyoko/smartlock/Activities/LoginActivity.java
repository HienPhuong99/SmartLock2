package com.lyoko.smartlock.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.lyoko.smartlock.R;
import com.lyoko.smartlock.Utils.Database_Helper;
import com.lyoko.smartlock.Interface.iLogin;

import static com.lyoko.smartlock.Utils.LyokoString.FORGOT;
import static com.lyoko.smartlock.Utils.LyokoString.LOGGED_NAME;
import static com.lyoko.smartlock.Utils.LyokoString.LOGGED_PHONE;
import static com.lyoko.smartlock.Utils.LyokoString.LOGGED_PREFERENCE;
import static com.lyoko.smartlock.Utils.LyokoString.LOGIN_SAVED;
import static com.lyoko.smartlock.Utils.LyokoString.VERIFIED_MODE;
import static com.lyoko.smartlock.Utils.LyokoString.phone_login;
import static com.lyoko.smartlock.Utils.LyokoString.phone_name;


public class LoginActivity extends AppCompatActivity implements iLogin {
    EditText et_login_password;
    TextView tv_change_login_phoneNumber,tv_login_phoneNumber,tv_login_name,tv_forgot_password;
    SharedPreferences.Editor editor;
    Button btn_login;
    Database_Helper db_helper = new Database_Helper();
    String loginPassword;
    Boolean saved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_login = findViewById(R.id.btn_login);
        et_login_password = findViewById(R.id.et_login_password);
        tv_login_phoneNumber = findViewById(R.id.tv_login_phoneNumber);
        tv_login_name = findViewById(R.id.tv_login_name);
        tv_forgot_password = findViewById(R.id.tv_forgot_password);
        tv_change_login_phoneNumber = findViewById(R.id.tv_change_login_phoneNumber);
        editor = getSharedPreferences(LOGGED_PREFERENCE, MODE_PRIVATE).edit();

        Bundle bundle = getIntent().getExtras();
        saved = bundle.getBoolean(LOGIN_SAVED);
        if (saved){
            db_helper.getAuthID(phone_login);
            tv_change_login_phoneNumber.setText("THOÁT TÀI KHOẢN");
        } else {
            tv_change_login_phoneNumber.setText("ĐỔI TÀI KHOẢN");
        }


        tv_login_phoneNumber.setText("0"+phone_login);
        tv_login_name.setText(phone_name);

        tv_change_login_phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saved){
                    editor.remove(LOGGED_PHONE).apply();
                } else FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(LoginActivity.this, CheckPhoneNumberActivity.class);
                startActivity(i);
                finish();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginPassword = et_login_password.getText().toString();
                if (loginPassword == null || loginPassword ==""){
                    return;
                }
                db_helper.checkPassword(loginPassword,LoginActivity.this);
            }
        });

        tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, AuthenticationActivity.class);
                intent.putExtra(VERIFIED_MODE, FORGOT);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onPasswordMatched() {
        if (!saved){
            editor.putString(LOGGED_PHONE,phone_login).apply();
            editor.putString(LOGGED_NAME,phone_name).apply();
        }
        Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onPasswordNotMatch() {
        Toast.makeText(LoginActivity.this, "Sai mật khẩu", Toast.LENGTH_SHORT).show();
        et_login_password.setText("");
    }

}

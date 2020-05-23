package com.lyoko.smartlock.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.lyoko.smartlock.Interface.iAuth;
import com.lyoko.smartlock.R;
import com.lyoko.smartlock.Utils.Database_Helper;

import java.util.concurrent.TimeUnit;
import static com.lyoko.smartlock.Utils.LyokoString.FORGOT;
import static com.lyoko.smartlock.Utils.LyokoString.LOGGED_NAME;
import static com.lyoko.smartlock.Utils.LyokoString.LOGGED_PHONE;
import static com.lyoko.smartlock.Utils.LyokoString.LOGIN;
import static com.lyoko.smartlock.Utils.LyokoString.LOGIN_SAVED;
import static com.lyoko.smartlock.Utils.LyokoString.REGISTER;
import static com.lyoko.smartlock.Utils.LyokoString.RESET;
import static com.lyoko.smartlock.Utils.LyokoString.VERIFIED_MODE;

import static com.lyoko.smartlock.Utils.LyokoString.auth_id;
import static com.lyoko.smartlock.Utils.LyokoString.phone_login;
import static com.lyoko.smartlock.Utils.LyokoString.phone_name;

public class AuthenticationActivity extends AppCompatActivity implements iAuth {
    TextView tv_phoneNumForVerify, tv_change_phoneNum, tv_resend_otp;
    EditText ed_otp_code;
    Button btn_Continue_auth;
    String verificationId;
    FirebaseAuth mAuth;
    String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentical);
        tv_phoneNumForVerify = findViewById(R.id.tv_phoneNumForVerify);
        tv_change_phoneNum = findViewById(R.id.tv_change_phoneNum);
        tv_resend_otp = findViewById(R.id.tv_resend_otp);
        ed_otp_code = findViewById(R.id.ed_otp_code);
        btn_Continue_auth = findViewById(R.id.btn_verify);

        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("vi");

        Bundle bundle = getIntent().getExtras();
        mode = bundle.getString(VERIFIED_MODE);
        if (mode==RESET){
            tv_change_phoneNum.setVisibility(View.INVISIBLE);
            tv_resend_otp.setVisibility(View.INVISIBLE);
        }

        tv_phoneNumForVerify.setText("0"+phone_login);
        sendVerificationCode();

        tv_change_phoneNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthenticationActivity.this, CheckPhoneNumberActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tv_resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode();
            }
        });

        btn_Continue_auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = ed_otp_code.getText().toString();
                if (otp.isEmpty() || otp.length() < 6) {
                    ed_otp_code.setError("Enter otp");
                    ed_otp_code.requestFocus();
                    return;
                } else verifyCode(otp);
            }
        });
    }

    private void verifyCode(String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            switch (mode){
                                case REGISTER:
                                    auth_id = task.getResult().getUser().getUid();
                                    Intent intent = new Intent(AuthenticationActivity.this, RegisterActivity.class);
                                    startActivity(intent);
                                    finish();
                                    return;
                                case LOGIN:
                                    auth_id = task.getResult().getUser().getUid();
                                    new Database_Helper().getOwnerName(AuthenticationActivity.this);
                                    return;
                                case FORGOT:
                                    Intent forgetIntent = new Intent(AuthenticationActivity.this, ForgotPasswordActivity.class);
                                    forgetIntent.putExtra(LOGGED_PHONE,phone_login);
                                    forgetIntent.putExtra(LOGGED_NAME,phone_name);
                                    startActivity(forgetIntent);
                                    finish();
                                    return;
                                case RESET:
                                    Intent resetIntent = new Intent(AuthenticationActivity.this, ForgotPasswordActivity.class);
                                    resetIntent.putExtra(LOGGED_PHONE,phone_login);
                                    resetIntent.putExtra(LOGGED_NAME,phone_name);
                                    startActivity(resetIntent);
                                    finish();
                                    return;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + mode);
                            }
                        } else {
                            Toast.makeText(AuthenticationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void sendVerificationCode() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+84" + phone_login,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(final PhoneAuthCredential credential) {
            Log.d("onVerify:" , credential+"");
            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(AuthenticationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

    };

    @Override
    public void onGetName(String name) {
        phone_name = name;
        Intent loginIntent = new Intent(AuthenticationActivity.this, LoginActivity.class);
        loginIntent.putExtra(LOGIN_SAVED,false);
        startActivity(loginIntent);
        finish();
    }
}

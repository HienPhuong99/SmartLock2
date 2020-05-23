package com.lyoko.smartlock.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lyoko.smartlock.Adapters.WifiScanAdapter;
import com.lyoko.smartlock.Fragment.AutoSetupDeviceFragment;
import com.lyoko.smartlock.Interface.iFindLock;
import com.lyoko.smartlock.Interface.iSetting;
import com.lyoko.smartlock.LyokoActivity;
import com.lyoko.smartlock.R;
import com.lyoko.smartlock.Services.BluetoothLeService;
import com.lyoko.smartlock.Services.Find_Device;
import com.lyoko.smartlock.Utils.Database_Helper;
import com.lyoko.smartlock.Utils.CheckView;
import com.lyoko.smartlock.Utils.DeniedDialog;
import com.lyoko.smartlock.Utils.LoadingDialog;
import com.lyoko.smartlock.Utils.SetupStatusBar;
import com.lyoko.smartlock.Utils.SuccessDialog;

import java.util.ArrayList;
import java.util.List;

import static com.lyoko.smartlock.Utils.LyokoString.DELAY;
import static com.lyoko.smartlock.Utils.LyokoString.DEVICE_ADDRESS;
import static com.lyoko.smartlock.Utils.LyokoString.DEVICE_NAME;
import static com.lyoko.smartlock.Utils.LyokoString.DEVICE_TYPE;
import static com.lyoko.smartlock.Utils.LyokoString.NOT_EMPTY;
import static com.lyoko.smartlock.Utils.LyokoString.OPEN_DELAY;
import static com.lyoko.smartlock.Utils.LyokoString.OTP_LIMIT_ENTRY;
import static com.lyoko.smartlock.Utils.LyokoString.OTP_LIMIT_UPDATE;
import static com.lyoko.smartlock.Utils.LyokoString.phone_login;

public class LockSettingsActivity extends LyokoActivity implements iSetting, iFindLock , WifiScanAdapter.OnItemClickedListener{
    String device_address;
    String device_name;
    String device_type;
    String wifi_ssid,wifi_password;
    String scan_address;
    WifiScanAdapter wifiScanAdapter;
    ArrayList<String> wifi_ssid_list = new ArrayList<String>();
    BroadcastReceiver wifiScanReceiver;
    WifiManager wifiManager;
    LoadingDialog loadingDialog;
    SuccessDialog successDialog;
    DeniedDialog deniedDialog;
    int lock_delay, lock_otp_limit_entry;
    public static BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    BluetoothLeService bluetoothLeService;
    Find_Device findDevice;
    Toolbar settings_toolbar;
    TextView tv_current_device_name, tv_current_unlock_delay, tv_current_limit_entry_otp;
    LinearLayout settings_lock_name, setting_unlock_delay, setting_max_otp_entry, setting_reset_device,setting_change_wifi_credential;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_settings);
        SetupStatusBar.setup(LockSettingsActivity.this);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;

        device_address = bundle.getString(DEVICE_ADDRESS);
        device_name = bundle.getString(DEVICE_NAME);
        lock_delay = bundle.getInt(DELAY);
        device_type = bundle.getString(DEVICE_TYPE);
        lock_otp_limit_entry = bundle.getInt(OTP_LIMIT_ENTRY);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        new Database_Helper().getScanAddress(device_address, this);

        settings_toolbar = findViewById(R.id.settings_toolbar);
        settings_lock_name = findViewById(R.id.settings_lock_name);
        setting_unlock_delay = findViewById(R.id.setting_unlock_delay);
        setting_max_otp_entry = findViewById(R.id.setting_max_otp_entry);
        setting_reset_device = findViewById(R.id.setting_reset_device);
        setting_change_wifi_credential = findViewById(R.id.setting_change_wifi_credential);
        tv_current_device_name = findViewById(R.id.tv_current_device_name);
        tv_current_unlock_delay = findViewById(R.id.tv_current_unlock_delay);
        tv_current_limit_entry_otp = findViewById(R.id.tv_current_limit_entry_otp);
        settings_toolbar.setTitle("CÀI ĐẶT  " + device_name.toUpperCase());
        loadingDialog = new LoadingDialog(this);
        successDialog = new SuccessDialog(this);
        deniedDialog = new DeniedDialog(this);

        tv_current_device_name.setText(device_name);
        tv_current_unlock_delay.setText(lock_delay+ " giây");
        tv_current_limit_entry_otp.setText(lock_otp_limit_entry+" lần");
        bluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        settings_lock_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeName();
            }
        });
        setting_unlock_delay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeDelay();
            }
        });
        setting_max_otp_entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeOTPLimit();
            }
        });
        setting_reset_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetDevice(device_address);
//                new Database_Helper().getAlertCode(device_address,device_type, LockSettingsActivity.this);
            }
        });

        setting_change_wifi_credential.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.startLoading("Đang tìm wifi, vui lòng đợi...");

                if (!wifiManager.isWifiEnabled()) wifiManager.setWifiEnabled(true);
                wifiScanReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context c, Intent intent) {
                        boolean getResultsSuccess = intent.getBooleanExtra(
                                WifiManager.EXTRA_RESULTS_UPDATED, false);
                        if (getResultsSuccess) scanSuccess();
                        else scanFailure();
                    }

                };
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                registerReceiver(wifiScanReceiver, intentFilter);

                if (!wifiManager.startScan()) {
                    scanFailure();
                }

            }
        });

    }

    private void scanSuccess() {
        loadingDialog.stopLoading();
        List<ScanResult> results = wifiManager.getScanResults();
        for (ScanResult result : results){
            wifi_ssid_list.add(result.SSID);
        }
        showWIFI();
        successDialog.startLoading("Thành công, chọn wifi và nhập mật khẩu",1200);

    }

    private void scanFailure() {
        loadingDialog.stopLoading();
        deniedDialog.startLoading("Không tìm thấy wifi", 1200);
    }


    private void showWIFI() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_wifi_credential,null);
        final RecyclerView wifi_recycle_view = view.findViewById(R.id.wifi_recycle_view);
        final TextView tv_wifi_rescan = view.findViewById(R.id.tv_wifi_rescan);
        final TextView tv_wifi_changed = view.findViewById(R.id.tv_wifi_changed);
        final TextView tv_wifi_not_found = view.findViewById(R.id.tv_wifi_not_found);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        wifiScanAdapter = new WifiScanAdapter(LockSettingsActivity.this, wifi_ssid_list);
        wifiScanAdapter.setOnItemClickedListener(LockSettingsActivity.this);
        wifi_recycle_view.setAdapter(wifiScanAdapter);
        wifi_recycle_view.setLayoutManager(new LinearLayoutManager(LockSettingsActivity.this));

        tv_wifi_changed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findDevice = new Find_Device(LockSettingsActivity.this, scan_address, bluetoothAdapter,LockSettingsActivity.this);
                findDevice.startScan();

            }
        });

        tv_wifi_rescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showWIFI();
            }
        });
        dialog.show();

    }

    private void resetDevice(String add) {
        new Database_Helper().resetDevice(add);
        successDialog.startLoading("Đã Xóa", 1000);
    }

    private void showChangeName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_change_device_name,null);
        final TextView tv_dialog_change_name_cancel = view.findViewById(R.id.tv_dialog_change_name_cancel);
        final TextView tv_dialog_change_name_confirm = view.findViewById(R.id.tv_dialog_change_name_confirm);
        final EditText et_new_device_name = view.findViewById(R.id.et_new_device_name);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        tv_dialog_change_name_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckView.isEmpty(et_new_device_name)){
                    et_new_device_name.setError(NOT_EMPTY);
                    return;
                }
                new Database_Helper().updateDeviceName(device_address,device_type,et_new_device_name.getText().toString());
                new SuccessDialog(LockSettingsActivity.this).startLoading("Sửa tên thiết bị thành công", 800);


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
    private void showChangeDelay() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_change_lock_delay,null);
        final TextView tv_dialog_change_delay_cancel = view.findViewById(R.id.tv_dialog_change_delay_cancel);
        final TextView tv_dialog_change_delay_confirm = view.findViewById(R.id.tv_dialog_change_delay_confirm);
        final EditText et_new_delay = view.findViewById(R.id.et_new_delay);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        tv_dialog_change_delay_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckView.isEmpty(et_new_delay)){
                    et_new_delay.setError(NOT_EMPTY);
                    return;
                }
                tv_current_device_name.setText(et_new_delay.getText().toString()+" giây");
                new Database_Helper().updateDelay(device_address,device_type,et_new_delay.getText().toString());
                new SuccessDialog(LockSettingsActivity.this).startLoading("Đổi thời gian mở khóa thành công", 800);


            }
        });

        tv_dialog_change_delay_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    private void showChangeOTPLimit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_change_lock_otp_limit_entry,null);
        final TextView tv_dialog_change_limit_entry_cancel = view.findViewById(R.id.tv_dialog_change_limit_entry_cancel);
        final TextView tv_dialog_change_limit_entry_confirm = view.findViewById(R.id.tv_dialog_change_limit_entry_confirm);
        final EditText et_new_limit_entry = view.findViewById(R.id.et_new_limit_entry);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        tv_dialog_change_limit_entry_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckView.isEmpty(et_new_limit_entry)){
                    et_new_limit_entry.setError(NOT_EMPTY);
                    return;
                }
                new Database_Helper().updateOTPLimitEntry(device_address,device_type,et_new_limit_entry.getText().toString());

                new SuccessDialog(LockSettingsActivity.this).startLoading("Đổi giới hạn nhập otp thành công", 800);
            }
        });

        tv_dialog_change_limit_entry_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onAlertCodeListen(String address) {
        resetDevice(address);
    }

    @Override
    public void onGetScanAddress(String scan_address) {
        this.scan_address = scan_address;
    }

    @Override
    public void onDeviceFound(final BluetoothDevice device) {
        loadingDialog.changeMessage("Đang kết nối tới "+device.getName());
        new CountDownTimer(1500, 1) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                bluetoothLeService = new BluetoothLeService(LockSettingsActivity.this,false,device,LockSettingsActivity.this);
                bluetoothLeService.connectDevice();
            }
        }.start();

    }

    @Override
    public void onDeviceNotFound() {
        deniedDialog.startLoading("Không tìm thấy thiết bị", 1000);

    }

    @Override
    public void onConnected() {
        bluetoothLeService.sendWifi(wifi_ssid,wifi_password);

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onItemClick(String ssid) {
        wifi_ssid = ssid;
    }
}

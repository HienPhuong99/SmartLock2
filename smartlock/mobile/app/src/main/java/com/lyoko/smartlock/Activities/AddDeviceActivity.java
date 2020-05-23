package com.lyoko.smartlock.Activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.lyoko.smartlock.Fragment.AutoSetupDeviceFragment;
import com.lyoko.smartlock.Fragment.BarcodeScannerFragment;
import com.lyoko.smartlock.Fragment.GetDeviceNameFragment;
import com.lyoko.smartlock.Fragment.GetWifiFragment;
import com.lyoko.smartlock.Interface.iQRCheck;
import com.lyoko.smartlock.LyokoActivity;
import com.lyoko.smartlock.R;
import com.lyoko.smartlock.Utils.Database_Helper;
import com.lyoko.smartlock.Utils.DeniedDialog;
import com.lyoko.smartlock.Utils.LoadingDialog;
import com.lyoko.smartlock.Utils.Permission;
import com.lyoko.smartlock.Utils.SuccessDialog;

import static com.lyoko.smartlock.Utils.LyokoString.COLOR_BLUE;
import static com.lyoko.smartlock.Utils.LyokoString.add_device_address;
import static com.lyoko.smartlock.Utils.LyokoString.add_device_name;

public class AddDeviceActivity extends LyokoActivity implements iQRCheck,BarcodeScannerFragment.OnGetDeviceAddress {
    private Permission permission;
    public static final int REQUEST_ENABLE_BT = 1;
    public static BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    public static FragmentManager manager ;
    public static TextView add_lock_description;
    public static TextView add_lock_step;
    public static Button btn_next_step;

    public static String device_mac_address;
    public static String wifi_ssid;
    public static String wifi_password;
    public static String device_type;
    Database_Helper db_service = new Database_Helper();
    public static LoadingDialog loadingDialog;
    public static SuccessDialog successDialog;
    public static DeniedDialog deniedDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lock);
        btn_next_step = findViewById(R.id.btn_next_step);
        add_lock_description = findViewById(R.id.add_lock_description);
        add_lock_step = findViewById(R.id.add_lock_step);
        loadingDialog = new LoadingDialog(this);
        successDialog = new SuccessDialog(this);
        deniedDialog = new DeniedDialog(this);

        getWindow().setStatusBarColor(COLOR_BLUE);
        permission = new Permission(this);
        checkBluetoothEnable();
        checkPermission();

        bluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        manager = getSupportFragmentManager();
        displayFragment(GetDeviceNameFragment.class);

    }

    public void checkBluetoothEnable() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public static void gotoNextStep(int step) {
        Log.d("step", step+"");
        switch (step){
            case 2:
                displayFragment(GetWifiFragment.class);
                break;
            case 3:
                displayFragment(BarcodeScannerFragment.class);
                break;
            case 4:
                displayFragment(AutoSetupDeviceFragment.class);
                break;

        }
    }

    public static void displayFragment(Class fragmentName) {
        try {
            if (fragmentName != null)
                manager.beginTransaction().replace(R.id.add_lock_content, (Fragment) fragmentName.newInstance()).commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void checkPermission() {
        permission.getPermission();
}

    @Override
    public void onAttachFragment(final Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof BarcodeScannerFragment) {
            BarcodeScannerFragment barcodeScannerFragment = (BarcodeScannerFragment) fragment;
            barcodeScannerFragment.setOnGetDeviceAddress(this);
        }
    }

    @Override
    public void onDeviceAddressSuitable(String address, String type) {
        device_type = type;
        db_service.checkAuthenticDevice(address,this);

    }

    @Override
    public void onDeviceAddressUnSuitable() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AddDeviceActivity.deniedDialog.startLoading("Mã QR không phù hợp",1000);
            }
        });
        resetAddDeviceProcess();
    }

    private void resetAddDeviceProcess() {
        add_device_name = "";
        add_device_address = "";
        device_mac_address = "";
        wifi_ssid = "";
        wifi_password = "";
        device_type = "";
        displayFragment(GetDeviceNameFragment.class);
    }

    @Override
    public void onNotOwner(String address) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deniedDialog.startLoading("Thiết bị đã được đăng kí bởi số điện thoại khác",1000);
                resetAddDeviceProcess();
            }
        });

    }

    @Override
    public void onAsOwner(String address) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deniedDialog.startLoading("Bạn đã đăng kí ổ khóa này rồi",1000);
                resetAddDeviceProcess();
            }
        });
    }

    @Override
    public void onReadyToAddDevice(String address) {
        device_mac_address = address;
        gotoNextStep(4);
    }

}

package com.lyoko.smartlock.Fragment;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyoko.smartlock.Activities.AddDeviceActivity;
import com.lyoko.smartlock.Activities.MainActivity;
import com.lyoko.smartlock.R;
import com.lyoko.smartlock.Services.BluetoothLeService;
import com.lyoko.smartlock.Utils.Database_Helper;
import com.lyoko.smartlock.Services.Find_Device;
import com.lyoko.smartlock.Interface.iFindLock;
import com.lyoko.smartlock.Utils.DeniedDialog;
import com.lyoko.smartlock.Utils.LoadingDialog;
import com.lyoko.smartlock.Utils.SuccessDialog;

import static com.lyoko.smartlock.Activities.AddDeviceActivity.add_lock_description;
import static com.lyoko.smartlock.Activities.AddDeviceActivity.add_lock_step;
import static com.lyoko.smartlock.Activities.AddDeviceActivity.device_mac_address;
import static com.lyoko.smartlock.Activities.AddDeviceActivity.device_type;
import static com.lyoko.smartlock.Utils.LyokoString.add_device_address;
import static com.lyoko.smartlock.Utils.LyokoString.add_device_name;

public class AutoSetupDeviceFragment extends Fragment implements iFindLock {


    BluetoothLeService bluetoothLeService;
    Find_Device findLock;
    LoadingDialog loadingDialog;
    SuccessDialog successDialog;
    DeniedDialog deniedDialog;

    public AutoSetupDeviceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_auto_setup_device, container, false);
        add_lock_description.setText(R.string.STEP_DESCRIPTION_4);
        add_lock_step.setText(R.string.STEP_4);
        findLock = new Find_Device(getContext(), AddDeviceActivity.device_mac_address, AddDeviceActivity.bluetoothAdapter,this);
        loadingDialog = new LoadingDialog(getActivity());
        successDialog = new SuccessDialog(getActivity());
        deniedDialog = new DeniedDialog(getActivity());
        loadingDialog.startLoading("Đang tìm thiết bị");
        findLock.startScan();
        return view;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                bluetoothLeService = new BluetoothLeService(getContext(),false,device,AutoSetupDeviceFragment.this);
                bluetoothLeService.connectDevice();
            }
        }.start();



    }

    @Override
    public void onDeviceNotFound() {
        loadingDialog.stopLoading();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deniedDialog.startLoading("Không tìm thấy thiết bị", 1000);
                new CountDownTimer(1500, 1) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }.start();
            }
        });
    }

    @Override
    public void onConnected() {
        loadingDialog.stopLoading();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog.startLoading("Đang cấu hình thiết bị ");
                new CountDownTimer(2000, 1) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        bluetoothLeService.sendInitialInfo(AddDeviceActivity.wifi_ssid,AddDeviceActivity.wifi_password);
                    }
                }.start();
            }
        });



    }

    @Override
    public void onComplete() {
        loadingDialog.stopLoading();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Database_Helper().addNewDevice(add_device_address, add_device_name, device_type, device_mac_address);
                successDialog.startLoading("Thiết lập hoàn tất", 1000);
                new CountDownTimer(1500, 1) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }.start();
            }
        });

    }


}

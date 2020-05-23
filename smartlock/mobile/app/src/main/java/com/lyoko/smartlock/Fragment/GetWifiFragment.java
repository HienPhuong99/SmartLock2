package com.lyoko.smartlock.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lyoko.smartlock.Activities.AddDeviceActivity;
import com.lyoko.smartlock.Adapters.WifiScanAdapter;
import com.lyoko.smartlock.R;
import com.lyoko.smartlock.Utils.CheckView;


import java.util.ArrayList;
import java.util.List;

import static com.lyoko.smartlock.Activities.AddDeviceActivity.add_lock_description;
import static com.lyoko.smartlock.Activities.AddDeviceActivity.add_lock_step;
import static com.lyoko.smartlock.Utils.LyokoString.NOT_EMPTY;

/**
 * A simple {@link Fragment} subclass.
 */
public class GetWifiFragment extends Fragment implements WifiScanAdapter.OnItemClickedListener {
    EditText ed_wifi_password;
    ImageView img_wifi_password_show, img_wifi_password_off;
    RecyclerView wifi_list_recycleView;
    WifiManager wifiManager;
    TextView tv_ssid;
    WifiScanAdapter wifiScanAdapter;
    ArrayList<String> wifi_ssid_list = new ArrayList<String>();
    BroadcastReceiver wifiScanReceiver;


    public GetWifiFragment() {


        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_get_wifi, container, false);
        ed_wifi_password = view.findViewById(R.id.ed_wifi_password);
        tv_ssid = view.findViewById(R.id.tv_wifi_ssid);
        wifi_list_recycleView = view.findViewById(R.id.wifi_list_recycleView);
        img_wifi_password_show = view.findViewById(R.id.img_wifi_password_show);
        img_wifi_password_off = view.findViewById(R.id.img_wifi_password_off);

        add_lock_description.setText(R.string.STEP_DESCRIPTION_2);
        add_lock_step.setText(R.string.STEP_2);

        AddDeviceActivity.loadingDialog.startLoading("Đang tìm wifi, vui lòng đợi...");

        img_wifi_password_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_wifi_password_show.setVisibility(View.INVISIBLE);
                img_wifi_password_off.setVisibility(View.VISIBLE);
                ed_wifi_password.setInputType(InputType.TYPE_CLASS_TEXT);
                ed_wifi_password.setSelection(ed_wifi_password.getText().toString().length());

            }
        });
        img_wifi_password_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_wifi_password_show.setVisibility(View.VISIBLE);
                img_wifi_password_off.setVisibility(View.INVISIBLE);
                ed_wifi_password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ed_wifi_password.setSelection(ed_wifi_password.getText().toString().length());
            }
        });

        // create new wifi scan
        wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) wifiManager.setWifiEnabled(true);
        // create new wifi receiver
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
        getContext().registerReceiver(wifiScanReceiver, intentFilter);

        if (!wifiManager.startScan()) {
            scanFailure();
        }

        AddDeviceActivity.btn_next_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_ssid.getText().toString()==""){
                    Toast.makeText(getContext(), "Vui Lòng chọn wifi", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (CheckView.isEmpty(ed_wifi_password)){
                    ed_wifi_password.setError(NOT_EMPTY);
                    return;
                }
                AddDeviceActivity.wifi_password = ed_wifi_password.getText().toString();
                AddDeviceActivity.gotoNextStep(3);
            }
        });
        return view;
    }

    private void scanFailure() {
        AddDeviceActivity.loadingDialog.stopLoading();
        AddDeviceActivity.deniedDialog.startLoading("Không tìm thấy wifi",2000);
    }

    private void scanSuccess() {
        AddDeviceActivity.loadingDialog.stopLoading();
        AddDeviceActivity.successDialog.startLoading("Thành công, chọn wifi và nhập mật khẩu",1200);
        List<ScanResult> results = wifiManager.getScanResults();
        for (ScanResult result : results){
            wifi_ssid_list.add(result.SSID);
        }
        wifiScanAdapter = new WifiScanAdapter(getContext(), wifi_ssid_list);
        wifiScanAdapter.setOnItemClickedListener(this);
        wifi_list_recycleView.setAdapter(wifiScanAdapter);
        wifi_list_recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(wifiScanReceiver);
    }

    @Override
    public void onItemClick(String ssid) {
        tv_ssid.setText(ssid);
        AddDeviceActivity.wifi_ssid = ssid;
    }
}

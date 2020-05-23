package com.lyoko.smartlock.Fragment;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.lyoko.smartlock.Activities.AddDeviceActivity;
import com.lyoko.smartlock.R;
import com.lyoko.smartlock.Utils.MACSuitable;
import java.io.IOException;

import static com.lyoko.smartlock.Activities.AddDeviceActivity.add_lock_description;
import static com.lyoko.smartlock.Activities.AddDeviceActivity.add_lock_step;


public class BarcodeScannerFragment extends Fragment {
    SurfaceView surfaceView;
    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;
    OnGetDeviceAddress callback;
    View scannerLayout ;
    ObjectAnimator animator = null ;

    public BarcodeScannerFragment() {
        // Required empty public constructor
    }
    public void setOnGetDeviceAddress(OnGetDeviceAddress callback) {
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_barcode_scanner, container, false);
        add_lock_description.setText(R.string.STEP_DESCRIPTION_3);
        add_lock_step.setText(R.string.STEP_3);
        surfaceView = view.findViewById(R.id.surfaceView);
        scannerLayout = view.findViewById(R.id.scannerLayout);

        barcodeDetector = new BarcodeDetector.Builder(getContext()).setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(getContext(), barcodeDetector).setRequestedPreviewSize(640,480).build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    cameraSource.start(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCode = detections.getDetectedItems();
                if (qrCode.size() != 0) {
                    String data = (qrCode.valueAt(0).displayValue);
                    if (data.length()<=17)
                    {
                        notSuitable();
                        return;
                    }
                    String address = data.substring(0,17);
                    String type = data.substring(18);
                    if (MACSuitable.check(address))
                    {
                        shake();
                        callback.onDeviceAddressSuitable(address,type);
                        getFragmentManager().beginTransaction().remove(BarcodeScannerFragment.this).commit();
                    }
                    else notSuitable();
                }
            }
        });
        return  view;
    }

    private void notSuitable() {
        shake();
        callback.onDeviceAddressUnSuitable();
        getFragmentManager().beginTransaction().remove(BarcodeScannerFragment.this).commit();
    }

    private void shake() {
        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(300);
    }


    public  interface  OnGetDeviceAddress{
        void onDeviceAddressSuitable(String address, String type);
        void onDeviceAddressUnSuitable();
    }

}

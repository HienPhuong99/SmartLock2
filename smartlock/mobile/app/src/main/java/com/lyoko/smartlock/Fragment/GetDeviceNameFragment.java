package com.lyoko.smartlock.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.lyoko.smartlock.Activities.AddDeviceActivity;
import com.lyoko.smartlock.R;
import com.lyoko.smartlock.Utils.CheckView;

import static com.lyoko.smartlock.Activities.AddDeviceActivity.add_lock_description;
import static com.lyoko.smartlock.Activities.AddDeviceActivity.add_lock_step;
import static com.lyoko.smartlock.Activities.AddDeviceActivity.btn_next_step;
import static com.lyoko.smartlock.Activities.AddDeviceActivity.gotoNextStep;
import static com.lyoko.smartlock.Utils.LyokoString.NOT_EMPTY;
import static com.lyoko.smartlock.Utils.LyokoString.add_device_name;

/**
 * A simple {@link Fragment} subclass.
 */
public class GetDeviceNameFragment extends Fragment {
    public  static EditText device_name;
    public GetDeviceNameFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_get_device_name, container, false);
        device_name = view.findViewById(R.id.add_device_name);
        add_lock_description.setText(R.string.STEP_DESCRIPTION_1);
        add_lock_step.setText(R.string.STEP_1);

        btn_next_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckView.isEmpty(device_name)){
                    device_name.setError(NOT_EMPTY);
                    return;
                }
                add_device_name = device_name.getText().toString();
                gotoNextStep(2);
            }
        });


        return view;
    }

}

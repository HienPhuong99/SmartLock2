package com.lyoko.smartlock.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lyoko.smartlock.Models.BLE_Device;
import com.lyoko.smartlock.Models.History;
import com.lyoko.smartlock.R;
import com.lyoko.smartlock.Utils.LyokoString;

import java.util.ArrayList;

import static com.lyoko.smartlock.Utils.LyokoString.COLOR_EVEN_POSITION;
import static com.lyoko.smartlock.Utils.LyokoString.COLOR_ODD_POSITION;


public class UnknownDevicesAdapter extends RecyclerView.Adapter<UnknownDevicesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<BLE_Device> list;
    OnUnknownBLEDeviceClickedListener callback;

    public UnknownDevicesAdapter(Context context, ArrayList<BLE_Device> list) {
        this.context = context;
        this.list = list;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_ble_device, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tv_ble_device_name.setText(list.get(position).getBle_name());
        holder.item_ble_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundColor(COLOR_EVEN_POSITION);
                callback.onUnknownBLEDeviceClickedListener(list.get(position).getBle_name(),list.get(position).getBle_address());
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_ble_device_name;
        LinearLayout item_ble_device;
        public ViewHolder(View view) {
            super(view);
            this.item_ble_device = view.findViewById(R.id.item_ble_device);
            this.tv_ble_device_name = view.findViewById(R.id.tv_ble_device_name);
        }
    }
    public void setOnUnknownBLEDeviceClickedListener(OnUnknownBLEDeviceClickedListener callback){
        this.callback = callback;
    }

    public interface OnUnknownBLEDeviceClickedListener {
        void onUnknownBLEDeviceClickedListener(String ble_name, String ble_address);
    }

}

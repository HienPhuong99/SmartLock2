package com.lyoko.smartlock.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lyoko.smartlock.Models.CanRemotePersonInfo;
import com.lyoko.smartlock.R;

import java.util.ArrayList;


public class CanRemotePersonInfoAdapter extends RecyclerView.Adapter<CanRemotePersonInfoAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CanRemotePersonInfo> list;
    private OnCanRemoteDeviceClickedListener callback;

    public CanRemotePersonInfoAdapter(Context context, ArrayList<CanRemotePersonInfo> list) {
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final CanRemotePersonInfo info = list.get(position);
        holder.tv_ble_device_name.setText(info.getName());
        holder.item_ble_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCanRemoteDeviceItemClick(info.getName(),info.getPhoneNumber(),info.getAuthid());
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

    public void setOnCanRemoteDeviceClickedListener(OnCanRemoteDeviceClickedListener callback){
        this.callback = callback;
    }

    public interface OnCanRemoteDeviceClickedListener {
        void onCanRemoteDeviceItemClick(String name, String phone, String authid);
    }

}

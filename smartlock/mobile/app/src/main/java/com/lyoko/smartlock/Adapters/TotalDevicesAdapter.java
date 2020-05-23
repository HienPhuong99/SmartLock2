package com.lyoko.smartlock.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lyoko.smartlock.Models.Device_info;
import com.lyoko.smartlock.R;
import com.lyoko.smartlock.Utils.Database_Helper;

import java.util.ArrayList;

import static com.lyoko.smartlock.Utils.LyokoString.CLOSE_LOCK;
import static com.lyoko.smartlock.Utils.LyokoString.HOLD_LOCK;
import static com.lyoko.smartlock.Utils.LyokoString.OPEN_LOCK;
import static com.lyoko.smartlock.Utils.LyokoString.phone_login;
import static com.lyoko.smartlock.Utils.LyokoString.phone_name;


public class TotalDevicesAdapter extends RecyclerView.Adapter<TotalDevicesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Device_info> list;
    private OnDeviceClickedListener callback;

    public TotalDevicesAdapter(Context context, ArrayList<Device_info> list) {
        this.context = context;
        this.list = list;


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View historyActivityView = inflater.inflate(R.layout.item_device, parent, false);
        ViewHolder viewHolder = new ViewHolder(historyActivityView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Device_info info = list.get(position);
        holder.img_device_state.setBackgroundResource(getBackground(info.getState()));
        holder.tv_device_name.setText(info.getName());
        if (info.getOwner() != phone_login) holder.tv_device_name.setBackgroundResource(R.drawable.cardview_bg);
        holder.img_device_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int _state, _bg;
                Database_Helper db_helper = new Database_Helper();
                switch (info.getState()){
                    case 0: _state = OPEN_LOCK;
                            db_helper.saveHistory(info.getOwner(),info.getAddress(),phone_name);
                            break;
                    default: _state = CLOSE_LOCK;
                }
                db_helper.changed_update_code(info.getOwner(),info.getAddress(),info.getType(),_state);

            }
        });
        holder.img_device_state.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if ( info.getState() == 1) {
                    new Database_Helper().changed_update_code(phone_login,info.getAddress(), info.getType(), HOLD_LOCK);
                    holder.img_device_state.setBackgroundResource(R.drawable.ic_power_hold);
                }
                return true;
            }
        });
        holder.item_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onDeviceClickedListener(info.getOwner(),info.getAddress(), info.getName(), info.getType());
            }
        });
    }
    private int getBackground(int state){
        switch (state){
            case 1: return R.drawable.ic_power_on;
            case 2: return R.drawable.ic_power_hold;
            default: return R.drawable.ic_power_off;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_device_name;
        ImageView img_device_type,img_device_state;
        RelativeLayout item_device;
        public ViewHolder(View view) {
            super(view);
            this.img_device_type = view.findViewById(R.id.img_device_type);
            this.img_device_state = view.findViewById(R.id.img_device_state);
            this.tv_device_name = view.findViewById(R.id.tv_device_name);
            this.item_device = view.findViewById(R.id.item_device);
        }
    }
    public void setOnDeviceClickedListener(OnDeviceClickedListener callback){
        this.callback = callback;
    }

    public interface OnDeviceClickedListener {
        void onDeviceClickedListener(String owner, String device_address, String device_name, String type);
    }




}

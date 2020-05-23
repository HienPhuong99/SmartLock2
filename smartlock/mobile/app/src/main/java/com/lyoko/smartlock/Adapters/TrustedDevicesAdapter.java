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

import com.lyoko.smartlock.Models.History;
import com.lyoko.smartlock.R;
import com.lyoko.smartlock.Utils.LyokoString;

import java.util.ArrayList;

import static com.lyoko.smartlock.Utils.LyokoString.COLOR_EVEN_POSITION;
import static com.lyoko.smartlock.Utils.LyokoString.COLOR_ODD_POSITION;


public class TrustedDevicesAdapter extends RecyclerView.Adapter<TrustedDevicesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> list;
    OnTrustedDeviceClickedListener callback;

    public TrustedDevicesAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View historyActivityView = inflater.inflate(R.layout.item_ble_device, parent, false);
        ViewHolder viewHolder = new ViewHolder(historyActivityView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tv_ble_device_name.setText(list.get(position));
        holder.item_ble_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundColor(COLOR_EVEN_POSITION);
                callback.onTrustedDeviceClickedListener(position);
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
    public void setOnTrustedDeviceClickedListener(OnTrustedDeviceClickedListener callback){
        this.callback = callback;
    }

    public interface OnTrustedDeviceClickedListener {
        void onTrustedDeviceClickedListener(int position);
    }
}

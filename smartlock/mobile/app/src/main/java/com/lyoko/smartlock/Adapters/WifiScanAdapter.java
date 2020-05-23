package com.lyoko.smartlock.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lyoko.smartlock.R;

import java.util.ArrayList;

import static com.lyoko.smartlock.Utils.LyokoString.COLOR_BLUE;

public class WifiScanAdapter extends RecyclerView.Adapter<WifiScanAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> list;
    private OnItemClickedListener callback;

    public WifiScanAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }
    public void setOnItemClickedListener(OnItemClickedListener callback){
        this.callback = callback;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View historyActivityView = inflater.inflate(R.layout.item_wifi_ssid, parent, false);
        ViewHolder viewHolder = new ViewHolder(historyActivityView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.tv_item_wifi_ssid.setText(list.get(position));
        holder.item_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onItemClick(list.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_item_wifi_ssid;
        LinearLayout item_wifi;
        public ViewHolder(View view) {
            super(view);
            item_wifi = view.findViewById(R.id.item_wifi);
            tv_item_wifi_ssid = view.findViewById(R.id.tv_item_wifi_ssid);
        }
    }

    public interface OnItemClickedListener {
        void onItemClick(String ssid);
    }

}

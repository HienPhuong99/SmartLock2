package com.lyoko.smartlock;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lyoko.smartlock.Activities.SplashActivity;

import java.util.ArrayList;


import static com.lyoko.smartlock.Utils.LyokoString.ALERT_CODE;
import static com.lyoko.smartlock.Utils.LyokoString.LYOKO_USERS;
import static com.lyoko.smartlock.Utils.LyokoString.OWN_DEVICES;
import static com.lyoko.smartlock.Utils.LyokoString.phone_login;

public class LyokoNotificationService extends Service {
    public static final String LYOKO_NOTIFY_CHANEL = "lyokoNotifyChanel";
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference user = db.getReference(LYOKO_USERS);
    NotificationManager manager;
    Notification notification;
    PendingIntent pendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(LYOKO_NOTIFY_CHANEL, "LYOKO_NOTIFY_SERVICE", NotificationManager.IMPORTANCE_MAX);
            manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        Intent notificationIntent = new Intent(this, SplashActivity.class);
        pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);


        Log.d("test", "runService");
        user.child(phone_login).child(OWN_DEVICES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<String> list = new ArrayList<>();
                for (DataSnapshot deviceSnapshot: dataSnapshot.getChildren()){
                    String macAddress = deviceSnapshot.getKey();
                    list.add(macAddress);
                }
                listen(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void listen(ArrayList<String> list) {
        for (final String e:list) {
            user.child(phone_login).child(OWN_DEVICES).child(e).child(ALERT_CODE).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String alertData =  dataSnapshot.getValue(String.class);
                    Log.d("test12", alertData);
                    if (alertData!=null){
                        notification = new NotificationCompat.Builder(getApplicationContext(),LYOKO_NOTIFY_CHANEL)
                                .setContentTitle("Lyoko Controller")
                                .setContentText("Có người cố gắng mở "+ alertData +" của bạn")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentIntent(pendingIntent)
                                .setSound(Uri.parse(Notification.CATEGORY_ALARM))
                                .build();
                        manager.notify(13,notification);

                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

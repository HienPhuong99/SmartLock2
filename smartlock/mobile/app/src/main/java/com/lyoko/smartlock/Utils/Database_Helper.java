package com.lyoko.smartlock.Utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lyoko.smartlock.Activities.LockSettingsActivity;
import com.lyoko.smartlock.Interface.iAuth;
import com.lyoko.smartlock.Interface.iCanRemote;
import com.lyoko.smartlock.Interface.iCheckPhoneNumber;
import com.lyoko.smartlock.Interface.iDeviceList;
import com.lyoko.smartlock.Interface.iHistory;
import com.lyoko.smartlock.Interface.iLogin;
import com.lyoko.smartlock.Interface.iQRCheck;
import com.lyoko.smartlock.Interface.iRegister;
import com.lyoko.smartlock.Interface.iSetting;
import com.lyoko.smartlock.Interface.iTrustedDevice;
import com.lyoko.smartlock.Models.CanRemotePersonInfo;
import com.lyoko.smartlock.Models.Device_info;
import com.lyoko.smartlock.Models.Device_settings;
import com.lyoko.smartlock.Models.History;
import com.lyoko.smartlock.Models.NewSMLock;
import com.lyoko.smartlock.Models.NewUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.lyoko.smartlock.Activities.AddDeviceActivity.device_mac_address;
import static com.lyoko.smartlock.Utils.LyokoString.ALERT_CODE;
import static com.lyoko.smartlock.Utils.LyokoString.AUTH_ID;
import static com.lyoko.smartlock.Utils.LyokoString.DELAY;
import static com.lyoko.smartlock.Utils.LyokoString.DEVICE_NAME;
import static com.lyoko.smartlock.Utils.LyokoString.DEVICE_TYPE;
import static com.lyoko.smartlock.Utils.LyokoString.HISTORY_UNLOCK_NAME;
import static com.lyoko.smartlock.Utils.LyokoString.HISTORY_UNLOCK_TIME;
import static com.lyoko.smartlock.Utils.LyokoString.HISTORY_UNLOCK_TYPE;
import static com.lyoko.smartlock.Utils.LyokoString.LOCK_OTP;
import static com.lyoko.smartlock.Utils.LyokoString.LOCK_STATE;
import static com.lyoko.smartlock.Utils.LyokoString.LYOKO_DEVICES;
import static com.lyoko.smartlock.Utils.LyokoString.HISTORIES;
import static com.lyoko.smartlock.Utils.LyokoString.LYOKO_USERS;
import static com.lyoko.smartlock.Utils.LyokoString.OPEN_DELAY;
import static com.lyoko.smartlock.Utils.LyokoString.OTP_LIMIT_ENTRY;
import static com.lyoko.smartlock.Utils.LyokoString.OTP_LIMIT_UPDATE;
import static com.lyoko.smartlock.Utils.LyokoString.OWNER_NAME;
import static com.lyoko.smartlock.Utils.LyokoString.OWN_DEVICES;
import static com.lyoko.smartlock.Utils.LyokoString.PASSWORD;
import static com.lyoko.smartlock.Utils.LyokoString.PHONE_NUMBER_REGISTERED;
import static com.lyoko.smartlock.Utils.LyokoString.REMOTE_BY;
import static com.lyoko.smartlock.Utils.LyokoString.REMOTE_DEVICES;
import static com.lyoko.smartlock.Utils.LyokoString.RESET_CODE;
import static com.lyoko.smartlock.Utils.LyokoString.SCAN_ADDRESS;
import static com.lyoko.smartlock.Utils.LyokoString.TRUSTED_DEVICES_ADDRESS;
import static com.lyoko.smartlock.Utils.LyokoString.TRUSTED_DEVICES_NAME;
import static com.lyoko.smartlock.Utils.LyokoString.TRUSTED_DEVICES_UPDATE;
import static com.lyoko.smartlock.Utils.LyokoString.UPDATE_CODE;
import static com.lyoko.smartlock.Utils.LyokoString.auth_id;
import static com.lyoko.smartlock.Utils.LyokoString.phone_login;
import static com.lyoko.smartlock.Utils.LyokoString.phone_login_password;

public class Database_Helper {
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference user = db.getReference(LYOKO_USERS);
    DatabaseReference devices = db.getReference(LYOKO_DEVICES);
    DatabaseReference phone_number =  db.getReference(PHONE_NUMBER_REGISTERED);

    public void getHistories(String owner_phone_number, String current_device_address, final iHistory iHistory) {
        user.child(owner_phone_number).child(OWN_DEVICES).child(current_device_address).child(HISTORIES)
                .addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         final ArrayList<History> list = new ArrayList<>();
                         for (DataSnapshot historySnapshot: dataSnapshot.getChildren()){
                             String unlock_name = historySnapshot.child(HISTORY_UNLOCK_NAME).getValue(String.class);
                             String unlock_type = historySnapshot.child(HISTORY_UNLOCK_TYPE).getValue(String.class);
                             String unlock_time = historySnapshot.child(HISTORY_UNLOCK_TIME).getValue(String.class) ;
                             list.add(new History(unlock_name, unlock_time, unlock_type));
                         }
                         if (list.size()==0){
                             return;
                         }
                         iHistory.show_history(list);

                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {

                     }
                });

    }

    public void checkPhoneNumber(final String phoneNum, final iCheckPhoneNumber iCheckPhoneNumber) {
        phone_number.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(phoneNum).exists()) {
                    iCheckPhoneNumber.phoneNumExist();
                } else iCheckPhoneNumber.phoneNumNotExist();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void checkPassword(final String password, final iLogin iLogin){
         user.child(phone_login).child(PASSWORD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String pass = dataSnapshot.getValue(String.class);
                if (pass.equalsIgnoreCase(password)){
                    phone_login_password = password;
                    iLogin.onPasswordMatched();
                } else iLogin.onPasswordNotMatch();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getOwnDevices(final Activity activity, final iDeviceList iDeviceList) {
        user.child(phone_login).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(OWN_DEVICES).exists()){
                    user.child(phone_login).child(OWN_DEVICES).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final ArrayList<Device_info> list = new ArrayList<>();
                            for (DataSnapshot deviceSnapshot: dataSnapshot.getChildren()){
                                String macAddress = deviceSnapshot.getKey();
                                String type = deviceSnapshot.child(DEVICE_TYPE).getValue(String.class);
                                list.add(new Device_info(phone_login,macAddress,type));
                            }
                            subGetOwnDevices(list, iDeviceList);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }

                    });

                } else iDeviceList.notThingToShow();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void subGetOwnDevices(final ArrayList<Device_info> list, final iDeviceList iDeviceList) {
        for (final Device_info e:list) {
            user.child(phone_login).child(OWN_DEVICES).child(e.getAddress()).child(e.getType()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String deviceName =  dataSnapshot.child(DEVICE_NAME).getValue(String.class);
                    int deviceState = dataSnapshot.child(LOCK_STATE).getValue(Integer.class);
                    e.setState(deviceState);
                    e.setName(deviceName);
                    iDeviceList.showOwnDevices(list);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    public void getRemoteDevice(final iDeviceList iDeviceList){
        user.child(phone_login).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(REMOTE_DEVICES).exists()){
                    user.child(phone_login).child(REMOTE_DEVICES).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final ArrayList<Device_info> list = new ArrayList<>();
                            for (DataSnapshot remoteDevice: dataSnapshot.getChildren()){
                                final String address = remoteDevice.getKey();
                                final String owner = remoteDevice.getValue(String.class);
                                list.add(new Device_info(owner,address));
                            }
                            subGetRemoteDevice(list,iDeviceList);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

                } else  iDeviceList.notThingToShow();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void subGetRemoteDevice(final ArrayList<Device_info> remove, final iDeviceList iDeviceList) {
        for (final Device_info device:remove){
        user.child(device.getOwner()).child(OWN_DEVICES).child(device.getAddress())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("out1", remove.size()+"");
                String type = dataSnapshot.child(DEVICE_TYPE).getValue(String.class);
                String name = dataSnapshot.child(type).child(DEVICE_NAME).getValue(String.class);
                int state = dataSnapshot.child(type).child(LOCK_STATE).getValue(Integer.class);
                device.setName(name);
                device.setType(type);
                device.setState(state);
                iDeviceList.showRemoteDevices(remove);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
     }
    }



    public void changed_update_code(String owner_phone_number, String current_device_address, String type, int update_code) {
        user.child(owner_phone_number).child(OWN_DEVICES).child(current_device_address).child(type).child(UPDATE_CODE).setValue(update_code);
    }

    public void saveHistory(String owner_phone_number, String current_device_address, String unlock_name){
        DatabaseReference history = user.child(owner_phone_number).child(OWN_DEVICES).child(current_device_address).child(HISTORIES);
        String hisID = history.push().getKey();
        String pattern= "EE, MMMM dd yyyy HH:mm:ss";
        Date date =new Date(new Date().getTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String unlock_time = simpleDateFormat.format(date);
        String unlock_type = "smartphone";
        History his = new History(unlock_name,unlock_time,unlock_type);
        history.child(hisID).setValue(his);
    }

    public void checkAuthenticDevice(final String address,final iQRCheck iqrCheck){
        devices.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean check = false;
                for (DataSnapshot authDeviceSnapshot: dataSnapshot.getChildren()){
                    if (authDeviceSnapshot.getKey().equals(address) && check == false){
                        check = true;
                        String owner = authDeviceSnapshot.getValue(String.class);
                        if (!owner.equals("")){
                            if (owner.equals(phone_login)){
                                iqrCheck.onAsOwner(address);
                            } else {
                                iqrCheck.onNotOwner(address);
                            }
                        } else  iqrCheck.onReadyToAddDevice(address);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void registerNewUser(iRegister iRegister, NewUser userInfo) {
        user.child(phone_login).setValue(userInfo);
        db.getReference(PHONE_NUMBER_REGISTERED).child(phone_login).setValue(userInfo.getOwner_name());
        iRegister.onRegisterSuccess();
    }

    public void addRemoteDevices(final String current_device_address, final String remote_phone_number, final String id){
        user.child(remote_phone_number).child(REMOTE_DEVICES).child(current_device_address).setValue(phone_login);
        user.child(phone_login).child(OWN_DEVICES).child(current_device_address).child(REMOTE_BY).child(id).setValue(remote_phone_number);

    }

    public void getAuthID(String phone_login){
        user.child(phone_login).child(AUTH_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                auth_id = name;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void changePassword(String pw) {
        user.child(phone_login).child(PASSWORD).setValue(pw);
    }


    public void setOTP(String address,  String type,String otp) {
       user.child(phone_login).child(OWN_DEVICES).child(address).child(type).child(LOCK_OTP).setValue(otp);
    }


    public void addNewDevice(String add_device_address, String device_name, String device_type, String device_mac_address) {
        NewSMLock newSMLock = new NewSMLock(device_name,device_type.toLowerCase(),"",0,10, -99, 3);
        user.child(phone_login).child(OWN_DEVICES).child(add_device_address).child(device_type.toLowerCase()).setValue(newSMLock);
        user.child(phone_login).child(OWN_DEVICES).child(add_device_address).child(DEVICE_TYPE).setValue(device_type.toLowerCase());
        user.child(phone_login).child(OWN_DEVICES).child(add_device_address).child(SCAN_ADDRESS).setValue(device_mac_address);
        devices.child(device_mac_address).setValue(phone_login);
    }

    public void addTrustedDevice(String current_device_address, String device_count, String ble_name, String ble_address, String device_type) {
        user.child(phone_login)
                .child(OWN_DEVICES)
                .child(current_device_address)
                .child(TRUSTED_DEVICES_ADDRESS).child(device_count).setValue(ble_address);
        user.child(phone_login)
                .child(OWN_DEVICES)
                .child(current_device_address)
                .child(TRUSTED_DEVICES_NAME).child(device_count).setValue(ble_name);
        changed_update_code(phone_login,current_device_address,device_type,TRUSTED_DEVICES_UPDATE);
    }

    public void updateDeviceName(String device_address, String type, String name ) {
        user.child(phone_login).child(OWN_DEVICES).child(device_address).child(type).child(DEVICE_NAME).setValue(name);

    }

    public void getDeviceSettings(final Activity activity, final String device_address, final String type) {
        user.child(phone_login).child(OWN_DEVICES).child(device_address).child(type)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child(DEVICE_NAME).getValue(String.class);
                        int delay = dataSnapshot.child(DELAY).getValue(Integer.class);
                        int otp_limit = dataSnapshot.child(OTP_LIMIT_ENTRY).getValue(Integer.class);
                        DialogShow.onGetDeviceSettings(activity,  new Device_settings(name,device_address,type,delay/1000,otp_limit));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    public void updateDelay(String device_address, String type,String delay) {
        user.child(phone_login).child(OWN_DEVICES).child(device_address).child(type).child(DELAY).setValue(Integer.parseInt(delay)*1000);
        changed_update_code(phone_login,device_address,type,OPEN_DELAY);
    }

    public void updateOTPLimitEntry(String device_address, String type, String limit) {
        user.child(phone_login).child(OWN_DEVICES).child(device_address).child(type).child(OTP_LIMIT_ENTRY).setValue(Integer.parseInt(limit));
       changed_update_code(phone_login,device_address, type, OTP_LIMIT_UPDATE);
    }

    public void getTrustedDevice(final String device_address, final iTrustedDevice iTrustedDevice) {
        user.child(phone_login).child(OWN_DEVICES).child(device_address).child(TRUSTED_DEVICES_NAME)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> list = new ArrayList<>();
                        for (DataSnapshot trustedDevice: dataSnapshot.getChildren()){
                            String trustedName = trustedDevice.getValue(String.class);
                            list.add(trustedName);
                        }
                        iTrustedDevice.showTrustedDevice(list);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        iTrustedDevice.noDeviceToShow();
                    }
                });
    }

    public void updateTrustedDevices(final int position, final String device_address, final String path) {
        user.child(phone_login).child(OWN_DEVICES).child(device_address).child(path)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> list = new ArrayList<>();
                        for (DataSnapshot trustedDevice: dataSnapshot.getChildren()){
                            String trustedName = trustedDevice.getValue(String.class);
                            list.add(trustedName);
                        }
                        list.remove(position);
                        user.child(phone_login).child(OWN_DEVICES).child(device_address).child(path).removeValue();
                        for (int i=0; i < list.size(); i++)
                            user.child(phone_login).child(OWN_DEVICES).child(device_address).child(path).child(String.valueOf(i)).setValue(list.get(i));

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    public void getCanRemoteDevice(String current_device_address, final iCanRemote iCanRemote) {
        user.child(phone_login).child(OWN_DEVICES).child(current_device_address).child(REMOTE_BY)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final  ArrayList<CanRemotePersonInfo> list = new ArrayList<>();
                        for (DataSnapshot data: dataSnapshot.getChildren()){
                            list.add(new CanRemotePersonInfo(data.getValue(String.class),data.getKey()));
                        }
                        Log.d("list", list.toString());
                        subGetCanRemoteDevice(list);
                    }

                    private void subGetCanRemoteDevice(final ArrayList<CanRemotePersonInfo> listKey) {
                        phone_number.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final ArrayList<CanRemotePersonInfo> info = new ArrayList<>();
                                for (final CanRemotePersonInfo _info: listKey) {
                                    info.add(new CanRemotePersonInfo(_info.getPhoneNumber(),dataSnapshot.child(_info.getPhoneNumber()).getValue(String.class),_info.getAuthid()));
                                }
                                iCanRemote.onGetCanRemoteDevices(info);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    public void removeCanRemoteDevice(String current_device_address, String phone, String authid) {
        user.child(phone).child(REMOTE_DEVICES).child(current_device_address).removeValue();
        user.child(phone_login).child(OWN_DEVICES).child(current_device_address).child(REMOTE_BY).child(authid).removeValue();
    }

    public void change_name(String name) {
        user.child(phone_login).child(OWNER_NAME).setValue(name);
        phone_number.child(phone_login).setValue(name);
    }

    public void getOwnerName(final iAuth iAuth) {
        user.child(phone_login).child(OWNER_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                iAuth.onGetName(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void resetDevice(final String device_address) {
        user.child(phone_login).child(OWN_DEVICES).child(device_address).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               String s= dataSnapshot.child(SCAN_ADDRESS).getValue(String.class);
                subReset(s);
            }

            private void subReset(String s) {
                devices.child(s).setValue("");
                user.child(phone_login).child(OWN_DEVICES).child(device_address).removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getAlertCode(String device_address, String device_type, final iSetting iSetting) {
        user.child(phone_login).child(OWN_DEVICES).child(device_address).child(device_type).child(UPDATE_CODE).setValue(143);
        user.child(phone_login).child(ALERT_CODE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String data = dataSnapshot.getValue(String.class);
                String address = data.substring(0,17);
                String code = data.substring(18);
                if (Integer.parseInt(code)== RESET_CODE);
                iSetting.onAlertCodeListen(address);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getScanAddress(String device_address, final iSetting iSetting) {
        user.child(phone_login).child(OWN_DEVICES).child(device_address).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String s= dataSnapshot.child(SCAN_ADDRESS).getValue(String.class);
                iSetting.onGetScanAddress(s);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
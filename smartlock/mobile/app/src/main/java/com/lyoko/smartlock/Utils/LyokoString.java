package com.lyoko.smartlock.Utils;

import android.graphics.Color;

import java.util.UUID;

public class LyokoString {
    // VERIFIED MODE
    public final static String VERIFIED_MODE = "verified_mode";
    public final static String LOGIN = "1";
    public final static String REGISTER = "0";
    public final static String FORGOT = "-1";
    public final static String RESET = "-99";

    public static final String ALERT_CODE = "alert_code";


    // SHARES PREFERENCE
    public final static String LOGGED_PREFERENCE = "loggedPref";
    public final static String LOGGED_PHONE = "loggedPhone";
    public final static String LOGGED_NAME = "loggedName";
    public final static String LOGIN_SAVED = "loginSaved";

    // Toast message
    public final static String PHONE_NUMBER_UNSUITABLE = "Số điện thoại không phù hợp";
    public final static String NOT_EMPTY = "Không được để trống";
    public final static String REGISTER_SUCCESSFULLY = "Đăng kí thành công";
    public final static String ACCESS_DENIED = "Bạn ko có quyền thực hiện chức năng này";
    // otp message
    public final static String OTP_SHARE_MESSAGE = "Mã OTP dùng để mở khóa của bạn là: ";
    public final static String OTP_SHARE_SUB_MESSAGE = ". Vui lòng không chia sẻ cho bất kì ai!";
    public final static String OTP_NOT_SAVE = "Vui lòng lưu OTP trước khi chia sẻ!";
    public final static String OTP_SAVED = "Đã lưu thay đổi";
    public final static String OTP_REMOVED = "ĐÃ XÓA OTP";
    public final static String OTP_UP_TO_DATE = "KHÔNG CÓ THAY ĐỔI GÌ";
    public final static String OTP_NULL = "Bạn chưa tạo mã otp nào";
    public final static String OTP_SHOW = "mã otp của bạn là: ";

    // LYOKO's USER
    public static String phone_login = null;
    public static String phone_name = null;
    public static String phone_login_password = null;
    public static String add_device_address = null;
    public static String add_device_name = null;
    public static String auth_id = null;

    public final static String OWNER_NAME = "owner_name";
    public final static String PASSWORD = "password";
    public final static String FCM = "fcm";

    // UPDATE CONTROLLER CODE
    public final static int CLOSE_LOCK= 0;
    public final static int OPEN_LOCK = 1;
    public final static int HOLD_LOCK = 2;
    public final static int OPEN_DELAY = 3;
    public final static int OTP_UPDATE = 4;
    public final static int TRUSTED_DEVICES_UPDATE = 5;
    public final static int OTP_LIMIT_UPDATE = 6;
    public final static int FCM_UPDATE = 7;
    public final static int RESET_CODE = 143;

    public final static int UP_TO_DATE = -99;
    // REMOTE DEVICE ATTRIBUTE


    // DEVICE OWNER ATTRIBUTE
    public final static String OWNER_PHONE_NUMBER = "owner_phone_number";
    public final static String OWN_DEVICES = "own_devices";
    public final static String REMOTE_DEVICES = "remote_devices";
    public final static String HISTORIES = "histories";
    public final static String DEVICE_NAME = "device_name";
    public final static String DEVICE_TYPE = "device_type";
    public final static String DEVICE_ADDRESS = "device_address";
    public final static String LOCK_STATE = "state";
    public final static String SCAN_ADDRESS = "scan_address";
    public final static String OTP_LIMIT_ENTRY = "otp_limit_entry";
    public final static String LOCK_OTP = "otp";
    public final static String TRUSTED_DEVICES_ADDRESS = "trusted_devices_address";
    public final static String TRUSTED_DEVICES_NAME = "trusted_devices_name";
    public final static String UPDATE_CODE = "update_code";
    public final static String REMOTE_BY = "remote_by";
    public final static String AUTH_ID = "auth_id";
    public static final String DELAY = "delay";

    // LYOKO DB PATH
    public final static String LYOKO_DEVICES = "lyoko_devices";
    public final static String LYOKO_USERS = "lyoko_users";
    public final static String PHONE_NUMBER_REGISTERED = "phone_number_registered";


    // WIFI SERVICE AND CHARACTERISTIC
    public final static UUID SERVICE_LYOKO_UUID = UUID.fromString("7060da7f-1ce6-43d1-b58c-2c595f8f9a56");
    public final static UUID CHARACTERISTIC_WIFI_CREDENTIAL_UUID = UUID.fromString("b82150b1-48e9-4a1b-a18c-3f2c140a8104");
    public final static UUID CHARACTERISTIC_RESPONSE_UUID = UUID.fromString("459d013b-061c-430b-a4fe-734cc22012cb");
    public final static UUID CHARACTERISTIC_OWNER_PHONE_NUMBER_UUID = UUID.fromString("e7608c36-76e7-4e9c-9263-1786f7a2f854");
    public final static UUID CHARACTERISTIC_CHIP_ID_UUID = UUID.fromString("95374a93-101c-4f2c-a590-2e8fcb3dfd37");
    public final static UUID CHARACTERISTIC_APPLICATION_COMMAND_UUID = UUID.fromString("c6014093-e175-41e8-a6bf-96964c7dfa71");
        public final static UUID CHARACTERISTIC_CLIENT_CONFIG_UUID = FormatData.convertFromInteger(0x2902);
    // COLOR
    public final static int COLOR_BLUE = Color.parseColor("#3498db");
    public final static int COLOR_UNLOCK = Color.parseColor("#2ecc71");
    public final static int COLOR_LOCK = Color.parseColor("#f95843");
    public final static int COLOR_GRAY = Color.parseColor("#8D8C8C");
    public final static int COLOR_EVEN_POSITION = Color.parseColor("#dff9fb");
    public final static int COLOR_ODD_POSITION = Color.parseColor("#c7ecee");

    // HISTORY ATTRIBUTE
    public final static String HISTORY_UNLOCK_TYPE = "unlock_type";
    public final static String HISTORY_UNLOCK_TIME = "unlock_time";
    public final static String HISTORY_UNLOCK_NAME = "unlock_name";

    // UNLOCK_TYPE
    public final static String UNLOCK_TYPE_SMARTPHONE = "smartphone";
    public final static String UNLOCK_TYPE_OTP = "otp";
    public final static String UNLOCK_TYPE_AUTO = "trusted_devices";


}

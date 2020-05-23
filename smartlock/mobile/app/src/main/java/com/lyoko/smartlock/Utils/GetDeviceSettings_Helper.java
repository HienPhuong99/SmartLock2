package com.lyoko.smartlock.Utils;

import java.util.Random;

public class GetDeviceSettings_Helper {
    public static String otp = "";
    public static boolean otp_save = false;


    public static String generateOTP(){
        String s = "";
        while (s.length()<6)
            s = String.valueOf(new Random().nextInt(999999 - 100000));
        otp = s;
        return s ;
    }

}

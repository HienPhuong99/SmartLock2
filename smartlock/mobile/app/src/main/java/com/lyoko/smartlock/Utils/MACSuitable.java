package com.lyoko.smartlock.Utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class  MACSuitable {
    private final static ArrayList<String> MAC_DEFAULT
            = new ArrayList<>(
                    Arrays.asList(
                            "10:52:1C", "18:FE:34", "24:0A:C4", "24:62:AB",
                            "24:6F:28", "24:B2:DE", "2C:3A:E8", "2C:F4:32",
                            "30:AE:A4", "3C:71:BF", "40:F5:20", "48:3F:DA",
                            "4C:11:AE", "50:02:91", "54:5A:A6", "5C:CF:7F",
                            "60:01:94", "68:C6:3A", "70:03:9F", "7C:9E:BD",
                            "7C:DF:A1", "80:7D:3A", "84:0D:8E", "84:CC:A8",
                            "84:F3:EB", "8C:AA:B5", "90:97:D5", "98:F4:AB",
                            "A0:20:A6", "A4:7B:9D", "A4:CF:12", "AC:67:B2",
                            "AC:D0:74", "B4:E6:2D", "B8:F0:09", "BC:DD:C2",
                            "C4:4F:33", "C8:2B:96", "CC:50:E3", "D8:A0:1D",
                            "D8:BF:C0", "D8:F1:5B", "DC:4F:22", "E0:98:06",
                            "EC:FA:BC", "F0:08:D1", "F4:CF:A2", "FC:F5:C4"
                            ));

    public static Boolean check(String address){
       for (String s: MAC_DEFAULT){
           if (address.toUpperCase().contains(s)){
               Log.d("match with",s);
               return true;
           }
       }
       return false;
    }
}

package com.lyoko.smartlock.Utils;

import java.util.UUID;

public class FormatData {

    public static String hexToString(byte[] data) {
        final StringBuilder sb = new StringBuilder(data.length);
        for(byte byteChar : data) {
            sb.append(String.format("%02X ", byteChar));
        }
        return sb.toString();
    }

    public static UUID convertFromInteger(int i) {
        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;
        return new UUID(MSB | (value << 32), LSB);
    }
}

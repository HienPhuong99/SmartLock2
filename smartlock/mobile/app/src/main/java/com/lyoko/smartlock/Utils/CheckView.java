package com.lyoko.smartlock.Utils;

import android.widget.EditText;

public class CheckView {

    public static boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}

package com.lyoko.smartlock.Interface;

import com.lyoko.smartlock.Models.CanRemotePersonInfo;

import java.util.ArrayList;

public interface iCanRemote {
    void onGetCanRemoteDevices(ArrayList<CanRemotePersonInfo> list);
    void nothingToShow();
}

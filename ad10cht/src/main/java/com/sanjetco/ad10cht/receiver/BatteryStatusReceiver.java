package com.sanjetco.ad10cht.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

import com.sanjetco.ad10cht.common.MainCommon;
import com.sanjetco.ad10cht.common.SharedData;

/**
 * Created by PaulLee on 2016/5/15.
 * BatteryStatusReceiver
 */
public class BatteryStatusReceiver extends BroadcastReceiver implements MainCommon {

    SharedData mSharedData = new SharedData();

    public BatteryStatusReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "BatteryStatusReceiver ENTER");
        int battery_level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 50);
        mSharedData.updateBatteryLevel(battery_level);
        Log.d(TAG, "BatteryStatusReceiver LEAVE");
    }
}

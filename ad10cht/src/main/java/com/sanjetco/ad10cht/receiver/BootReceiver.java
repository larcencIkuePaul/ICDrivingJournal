package com.sanjetco.ad10cht.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sanjetco.ad10cht.common.MainCommon;
import com.sanjetco.ad10cht.service.MainService;

/**
 * Created by PaulLee on 2016/4/22.
 * Boot receiver
 */
public class BootReceiver extends BroadcastReceiver implements MainCommon {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case Intent.ACTION_BOOT_COMPLETED:
                Log.d(TAG, "Receive BOOT COMPLETED");
                Intent it = new Intent(context, MainService.class);
                context.startService(it);
                break;
        }
    }
}

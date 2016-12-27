package com.sanjetco.ad10cht.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.sanjetco.ad10cht.R;
import com.sanjetco.ad10cht.common.MainCommon;
import com.sanjetco.ad10cht.service.MainService;
import com.sanjetco.ad10cht.ui.UpdateUI;

/**
 * Created by PaulLee on 2016/4/21.
 * MainActivity will launch MainService
 */
public class MainActivity extends Activity implements MainCommon {

    UpdateUI mUpdateUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "MainActivity CREATE");
        super.onCreate(savedInstanceState);

        if (ENABLE_UI) {
            setContentView(R.layout.main_activity);

            // Update UI handler
            mUpdateUI = new UpdateUI(this);
            mUpdateUI.initUI();

            // Sensor data broadcast receiver and intent filter
            BroadcastReceiver updateUIReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if ( !intent.getAction().equalsIgnoreCase(INTENT_ACTION_UPDATE_UI) ) {
                        return;
                    }
                    int type = intent.getIntExtra(EXTRA_DATA_TYPE, 0);
                    switch (type) {
                        case TYPE_ACCELEROMETER_DATA:
                            float[] data = intent.getFloatArrayExtra(EXTRA_ACCELEROMETER_DATA);
                            mUpdateUI.updateAccelValue(data);
                            break;
                        case TYPE_SYS_MSG:
                            String msg = intent.getStringExtra(EXTRA_SYS_MSG);
                            mUpdateUI.updateSystemMessage(msg);
                            break;
                        default:
                            break;
                    }
                }
            };
            IntentFilter itFilter = new IntentFilter(INTENT_ACTION_UPDATE_UI);
            registerReceiver(updateUIReceiver, itFilter);
        }

        // Launch MainService
        Intent intent = new Intent(this, MainService.class);
        startService(intent);

        if (!ENABLE_UI)
            finish();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "MainActivity START");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "MainActivity RESUME");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "MainActivity PAUSE");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "MainActivity STOP");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "MainActivity RESTART");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "MainActivity DESTROY");
        super.onDestroy();
    }
}

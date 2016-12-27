package com.sanjetco.ad10cht.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sanjetco.ad10cht.callback.GpsEventCallback;
import com.sanjetco.ad10cht.callback.SensorListenerCallback;
import com.sanjetco.ad10cht.callback.SystemEventCallback;
import com.sanjetco.ad10cht.callback.SystemLogCallback;
import com.sanjetco.ad10cht.common.ChtGpsDataPack;
import com.sanjetco.ad10cht.common.MainCommon;
import com.sanjetco.ad10cht.common.SystemEventCommon;
import com.sanjetco.ad10cht.database.DatabaseManager;
import com.sanjetco.ad10cht.database.DrivingJournalManager;
import com.sanjetco.ad10cht.event.DrivingEvent;
import com.sanjetco.ad10cht.event.GpsEvent;
import com.sanjetco.ad10cht.event.SystemEvent;
import com.sanjetco.ad10cht.listener.Obd2Listener;
import com.sanjetco.ad10cht.listener.SensorListener;
import com.sanjetco.ad10cht.receiver.BatteryStatusReceiver;
import com.sanjetco.ad10cht.watcher.DatabaseWatcher;
import com.sanjetco.ad10cht.watcher.HeartbeatWatcher;
import com.sanjetco.ad10cht.watcher.IgnitionStatusWatcher;

/**
 * Created by PaulLee on 2016/4/21.
 * Main service
 */
public class MainService extends Service implements
        MainCommon,
        SystemEventCommon,
        SensorListenerCallback,
        GpsEventCallback,
        SystemEventCallback,
        SystemLogCallback {

    DatabaseManager mDatabaseManager;
    DrivingJournalManager mDrivingJournalManager;
    DrivingEvent mDrivingEvent;
    GpsEvent mGpsEvent;
    SensorListener mSensorListener;
    SystemEvent mSystemEvent;

    DatabaseWatcher mDatabaseWatcher;
    IgnitionStatusWatcher mIgnitionStatusWatcher;
    HeartbeatWatcher mHeartbeatWatcher;

    Obd2Listener mObd2Listener;

    BatteryStatusReceiver mBatteryStatusReceiver = new BatteryStatusReceiver();

    float[] m3AxisAccelerationData = new float[] {0f, 0f, 0f};
    ChtGpsDataPack mChtGpsDataPack = new ChtGpsDataPack();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "MainService STARTED");
        if (ENABLE_UI) {
            updateSystemLog("MainService STARTED");
        }

        mDatabaseManager = new DatabaseManager(this);

        mDrivingJournalManager = new DrivingJournalManager(this);
        mDrivingJournalManager.initManager();

        /*
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis() / 1000;
                while (true) {
                    if (System.currentTimeMillis() / 1000 - startTime > 10) {
                        Log.d(TAG, "Ready to get journals");
                        mDrivingJournalManager.getJournals();
                        mDrivingJournalManager.addNewJournal();
                        mDrivingJournalManager.getMessages();
                        startTime = System.currentTimeMillis() / 1000;
                    }
                }
            }
        });
        thread.start();
        */

        mSensorListener = new SensorListener(this);
        mSensorListener.initSensorListener();
        mSensorListener.startListen();

        mDrivingEvent = new DrivingEvent(this, mDrivingJournalManager);
        mDrivingEvent.initAll(mDatabaseManager);

        mGpsEvent = new GpsEvent(this);
        mGpsEvent.startEvent();

        mSystemEvent = new SystemEvent(this);
        mSystemEvent.startEvent();

        mDatabaseWatcher = new DatabaseWatcher();
        mDatabaseWatcher.execute(this);

        mIgnitionStatusWatcher = new IgnitionStatusWatcher(mSystemEvent.getSystemEventListenerCallback(), mSystemEvent.getSystemEventListener());
        mIgnitionStatusWatcher.start();

        mHeartbeatWatcher = new HeartbeatWatcher();
        mHeartbeatWatcher.start();

        this.registerReceiver(mBatteryStatusReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHeartbeatWatcher.stop();
        mIgnitionStatusWatcher.stop();
        mSystemEvent.stopEvent();
        mGpsEvent.stopEvent();
        mSensorListener.stopListen();
        mDrivingJournalManager.deInitManager();

        this.unregisterReceiver(mBatteryStatusReceiver);
    }

    @Override
    public void throwSensorData(float[] data) {
        System.arraycopy(data, 0, m3AxisAccelerationData, 0, m3AxisAccelerationData.length);
        mDrivingEvent.recvSensorDataFromMainService(m3AxisAccelerationData);
        if (ENABLE_UI) {
            Intent intent = new Intent(INTENT_ACTION_UPDATE_UI);
            intent.putExtra(EXTRA_DATA_TYPE, TYPE_ACCELEROMETER_DATA);
            intent.putExtra(EXTRA_ACCELEROMETER_DATA, m3AxisAccelerationData);
            this.sendBroadcast(intent);
        }
    }

    @Override
    public void throwGpsData(ChtGpsDataPack pack) {
        mChtGpsDataPack = pack;
    }

    @Override
    public void notifySystemEvent(String action) {
        switch (action) {
            case IGNITION_OFF_OVER_MAX_DURATION:
                mDrivingEvent.wrapDrivingEventBeforeShutdown();
                mHeartbeatWatcher.stop();
                mIgnitionStatusWatcher.stop();
                mGpsEvent.stopEvent();
                mSensorListener.stopListen();
                mDrivingJournalManager.deInitManager();

                mSystemEvent.systemShutdown();

                mSystemEvent.stopEvent();
                break;
            default:
                break;
        }
    }

    @Override
    public void updateSystemLog(String message) {
        Log.d(TAG, "updateSystemLog | " + message);
        if (ENABLE_UI) {
            Intent intent = new Intent(INTENT_ACTION_UPDATE_UI);
            intent.putExtra(EXTRA_DATA_TYPE, TYPE_SYS_MSG);
            intent.putExtra(EXTRA_SYS_MSG, message);
            this.sendBroadcast(intent);
        }
    }
}

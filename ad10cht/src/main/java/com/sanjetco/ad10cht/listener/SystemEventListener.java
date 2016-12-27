package com.sanjetco.ad10cht.listener;

import android.content.Context;
import android.util.Log;

import com.intel.telematics.powermanager.HwState;
import com.intel.telematics.powermanager.ISystemEventListener;
import com.intel.telematics.powermanager.SystemPowerManager;
import com.intel.telematics.powermanager.SystemState;
import com.sanjetco.ad10cht.callback.SystemEventListenerCallback;
import com.sanjetco.ad10cht.callback.SystemLogCallback;
import com.sanjetco.ad10cht.common.MainCommon;

/**
 * Created by PaulLee on 2016/4/22.
 * Register and listen system event
 */
public class SystemEventListener implements
        ISystemEventListener,
        MainCommon
{

    static Context mContext;
    static SystemPowerManager mSysPowerMgr;
    static SystemEventListenerCallback mSystemEventListenerCallback;
    static SystemLogCallback mSystemLogCallback;
    long mStartTimestamp;


    public SystemEventListener(Context context, SystemEventListenerCallback systemEventListenerCallback, SystemLogCallback systemLogCallback) {
        mContext = context;
        mSystemEventListenerCallback = systemEventListenerCallback;
        mSystemLogCallback = systemLogCallback;
    }

    public void startListen() {
        mSysPowerMgr = new SystemPowerManager(mContext, this);
        mSysPowerMgr.initServiceConnection();

        /*
        mStartTimestamp = System.currentTimeMillis() / 1000;
        Thread threadAutoShutdown = new Thread(new Runnable() {
            @Override
            public void run() {
                while (System.currentTimeMillis() / 1000 - mStartTimestamp < 120) { }
                Log.d(TAG, "Ready to shutdown...");
                mSystemLogCallback.updateSystemLog("Ready to shutdown...");
                mSysPowerMgr.requestSysShutDown();
            }
        });
        threadAutoShutdown.start();
        */
        Log.d(TAG, "SystemEventListener STARTED");
    }

    public void stopListen() {
        mSysPowerMgr.deInitServiceConnection();
    }

    public boolean checkIgnitionStatus() {
        return mSysPowerMgr.isIgnitionOn();
    }

    @Override
    public void onPowerServiceConnected() {
        Log.d(TAG, "Power service CONNECTED");
        mSystemLogCallback.updateSystemLog("Power service CONNECTED");
    }

    @Override
    public void onPowerServiceDisconnected() {
        Log.d(TAG, "Power service DISCONNECTED");
        mSystemLogCallback.updateSystemLog("Power service DISCONNECTED");
    }

    @Override
    public void onSystemEventRaised(SystemState.HwType hwType, HwState hwState) {
        Log.d(TAG, SystemEventParser(hwType, hwState));
        mSystemLogCallback.updateSystemLog(SystemEventParser(hwType, hwState));
    }

    protected String SystemEventParser(SystemState.HwType type, HwState state) {
        return HwTypeParser(type) + " | " + HwStateParser(state);
    }

    protected String HwTypeParser(SystemState.HwType type) {
        String result;
        switch (type) {
            case SYS_HW_TYPE_BT:
                result = "BT";
                break;
            case SYS_HW_TYPE_GPS:
                result = "GPS";
                break;
            case SYS_HW_TYPE_WIFI:
                result = "WIFI";
                break;
            case SYS_HW_TYPE_HOTSPOT:
                result = "HotSpot";
                break;
            case HW_TYPE_SP_TELEMATICS:
                result = "Telematics";
                break;
            default:
                result = "Unrecognized";
                break;
        }
        return result;
    }

    protected String HwStateParser(HwState state) {
        String result;
        switch (state) {
            case ON:
                result = "ON";
                break;
            case OFF:
                result = "OFF";
                break;
            case WAKE:
                result = "WAKE";
                break;
            case SLEEP:
                result = "SLEEP";
                break;
            default:
                result = "Unrecognized";
                break;
        }
        return result;
    }

    @Override
    public void onIgnitionEventRaised(boolean isIgnitionOn) {
        Log.d(TAG, IgnitionEventParser(isIgnitionOn));
        mSystemEventListenerCallback.notifyIgnitionEventRaised(isIgnitionOn);
        mSystemLogCallback.updateSystemLog(IgnitionEventParser(isIgnitionOn));
    }

    protected String IgnitionEventParser(boolean isIgnitionOn) {
        return isIgnitionOn ? "Ignition ON" : "Ignition OFF";
    }

    @Override
    public void onMotionDetected() {
        Log.d(TAG, "Motion DETECTED");
        mSystemLogCallback.updateSystemLog("Motion DETECTED");
    }

    @Override
    public void onDongleConnected() {
        Log.d(TAG, "Dongle CONNECTED");
        mSystemEventListenerCallback.notifyDongleConnected();
        mSystemLogCallback.updateSystemLog("Dongle CONNECTED");
    }

    @Override
    public void onDongleDetached() {
        Log.d(TAG, "Dongle DETACHED");
        mSystemEventListenerCallback.notifyDongleDetached();
        mSystemLogCallback.updateSystemLog("Dongle DETACHED");
        //mSysPowerMgr.requestSysShutDown();
    }

    @Override
    public void onLowCarBattery() {
        Log.d(TAG, "Low car battery");
        mSystemEventListenerCallback.notifyLowCarBattery();
        mSystemLogCallback.updateSystemLog("Low car battery");
    }

    public void shutdown() {
        mSysPowerMgr.requestSysShutDown();
    }
}

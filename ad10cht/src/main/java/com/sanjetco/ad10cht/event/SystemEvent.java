package com.sanjetco.ad10cht.event;

import android.content.Context;
import android.util.Log;

import com.sanjetco.ad10cht.callback.SystemEventCallback;
import com.sanjetco.ad10cht.callback.SystemEventListenerCallback;
import com.sanjetco.ad10cht.callback.SystemLogCallback;
import com.sanjetco.ad10cht.common.ChtGpsDataPack;
import com.sanjetco.ad10cht.common.HttpCommon;
import com.sanjetco.ad10cht.common.MainCommon;
import com.sanjetco.ad10cht.common.SharedData;
import com.sanjetco.ad10cht.common.SystemEventCommon;
import com.sanjetco.ad10cht.httpclient.ChtHttpConnection;
import com.sanjetco.ad10cht.listener.SystemEventListener;
import com.sanjetco.ad10cht.log.LogFileWriter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by PaulLee on 2016/5/15.
 * System event
 */
public class SystemEvent implements
        MainCommon,
        SystemEventListenerCallback,
        HttpCommon,
        SystemEventCommon
{
    Context mContext;
    SystemEventListener mSystemEventListener;
    SystemEventCallback mSystemEventCallback;
    SystemLogCallback mSystemLogCallback;
    LogFileWriter mLogFileWriter = new LogFileWriter();
    static SharedData mSharedData = new SharedData();
    static ChtGpsDataPack mChtGpsDataPack = new ChtGpsDataPack();

    long mIgnitionOffStartTime;
    boolean mIgnitionOffRecording = false;

    public SystemEvent(Object object) {
        mContext = (Context) object;
        mSystemEventCallback = (SystemEventCallback) object;
        mSystemLogCallback = (SystemLogCallback) object;
        mSystemEventListener = new SystemEventListener(mContext, this, mSystemLogCallback);
    }

    public void startEvent() {
        mSystemEventListener.startListen();
        String message = "SystemEvent STARTED";
        Log.d(TAG, message);
        mLogFileWriter.appendLog(message);
    }

    public void stopEvent() {
        mSystemEventListener.stopListen();
    }

    public SystemEventListener getSystemEventListener() {
        return mSystemEventListener;
    }

    public SystemEventListenerCallback getSystemEventListenerCallback() {
        return this;
    }

    @Override
    public void notifyDongleConnected() {
        String message = "Dongle connected callback";
        Log.d(TAG, message);
        mLogFileWriter.appendLog(message);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                send(45);
            }
        });
        thread.start();
    }

    @Override
    public void notifyDongleDetached() {
        String message = "Dongle detached callback";
        Log.d(TAG, message);
        mLogFileWriter.appendLog(message);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                send(44);
            }
        });
        thread.start();
    }

    @Override
    public void notifyIgnitionEventRaised(boolean status) {
        String message = "Ignition event raised callback";
        Log.d(TAG, message);
        mLogFileWriter.appendLog(message);
        final int eventId = status ? 98 : 99;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                send(eventId);
            }
        });
        thread.start();
    }

    @Override
    public void notifyIgnitionStatusPolling(boolean isIgnitionOn) {
        Log.d(TAG, "Ignition status polling callback | " + String.valueOf(isIgnitionOn));
        if ( !isIgnitionOn ) {
            if (mIgnitionOffRecording) {
                long ignitionOffDuration = System.currentTimeMillis() / 1000 - mIgnitionOffStartTime;
                if (ignitionOffDuration >= IGNITION_OFF_MAX_DURATION_SEC) {
                    mSystemEventCallback.notifySystemEvent(IGNITION_OFF_OVER_MAX_DURATION);
                    mIgnitionOffRecording = false;
                } else {
                    String message = String.format("Detect ignition off after %d sec...", ignitionOffDuration);
                    Log.d(TAG, message);
                    mLogFileWriter.appendLog(message);
                }
            } else {
                mIgnitionOffStartTime = System.currentTimeMillis() / 1000;
                mIgnitionOffRecording = true;
            }
        } else {
            mIgnitionOffRecording = false;
        }
    }

    public void systemShutdown() {
        String message = "System shutdown due to ignition off over 60 sec...";
        Log.d(TAG, message);
        mLogFileWriter.appendLog(message);
        mSystemEventListener.shutdown();
    }

    @Override
    public void notifyLowCarBattery() {
        String message = "Low car battery callback";
        Log.d(TAG, message);
        mLogFileWriter.appendLog(message);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                send(41);
            }
        });
        thread.start();
    }

    protected byte[] generateEventInfo(int eventId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deviceId", mSharedData.getImeiId());
            jsonObject.put("time", String.valueOf(System.currentTimeMillis() / 1000));
            jsonObject.put("eventId", String.valueOf(eventId));
            jsonObject.put("gpsTrack", generateGpsTrack());
            Log.d(TAG, "Event info | " + jsonObject.toString());
        } catch (JSONException e) {
            String message = "Generate EventInfo error | " + e.getMessage();
            Log.d(TAG, message);
            mLogFileWriter.appendLog(message);
        }
        return jsonObject.toString().getBytes();
    }

    protected JSONObject generateGpsTrack() {
        mChtGpsDataPack = mSharedData.getChtGpsDataPack();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("altitude", String.valueOf(mChtGpsDataPack.altitude));
            jsonObject.put("bearing", String.valueOf(mChtGpsDataPack.bearing));
            jsonObject.put("lat", String.valueOf(mChtGpsDataPack.lat));
            jsonObject.put("lon", String.valueOf(mChtGpsDataPack.lon));
            jsonObject.put("speed", String.valueOf(mChtGpsDataPack.speed));
        } catch (JSONException e) {
            String message = "Generate GpsTrack error | " + e.getMessage();
            Log.d(TAG, message);
            mLogFileWriter.appendLog(message);
        }
        return jsonObject;
    }

    protected void send(int eventId) {
        int result;
        result = new ChtHttpConnection().sendDataToCht(generateEventInfo(eventId), UBI_CLOUD_URL + EVENT_URL, EVENT_URL);
        String message = String.format("Send event %d result | %d", eventId, result);
        Log.d(TAG, message);
        mLogFileWriter.appendLog(message);
    }
}

package com.sanjetco.ad10cht.watcher;

import android.util.Log;

import com.sanjetco.ad10cht.common.ChtGpsDataPack;
import com.sanjetco.ad10cht.common.HttpCommon;
import com.sanjetco.ad10cht.common.MainCommon;
import com.sanjetco.ad10cht.common.SharedData;
import com.sanjetco.ad10cht.httpclient.ChtHttpConnection;
import com.sanjetco.ad10cht.log.LogFileWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by PaulLee on 2016/5/13.
 * Heartbeat watcher
 */
public class HeartbeatWatcher implements
        MainCommon,
        HttpCommon
{
    static LogFileWriter mLogFileWriter = new LogFileWriter();
    static ChtGpsDataPack mChtGpsDataPack = new ChtGpsDataPack();
    static SharedData mSharedData = new SharedData();

    boolean mRunHeartbeatWatcher = false;
    long HEARTBEAT_REPORT_DURATION_MILLISEC = 60000;
    //long HEARTBEAT_REPORT_DURATION_MILLISEC = 20000;

    public HeartbeatWatcher() {

    }

    public void start() {
        mRunHeartbeatWatcher = true;
        mThread.start();
        String message = "Heartbeat watcher STARTED";
        Log.d(TAG, message);
        mLogFileWriter.appendLog(message);
    }

    public void stop() {
        mRunHeartbeatWatcher = false;
        String message = "Heartbeat watcher STOPPED";
        Log.d(TAG, message);
        mLogFileWriter.appendLog(message);
    }

    Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (mRunHeartbeatWatcher) {
                try {
                    Thread.sleep(HEARTBEAT_REPORT_DURATION_MILLISEC);
                    send();
                } catch (InterruptedException e) {
                    String message = "Heartbeat interrupt exception | " + e.getMessage();
                    Log.d(TAG, message);
                    mLogFileWriter.appendLog(message);
                }
            }
        }
    });

    protected byte[] generateHeartbeatInfo() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deviceId", mSharedData.getImeiId());
            jsonObject.put("time", String.valueOf(System.currentTimeMillis() / 1000));
            jsonObject.put("gpsTrack", generateGpsTrack());
            jsonObject.put("data", new JSONArray(generateData()));
            Log.d(TAG, "Heartbeat info | " + jsonObject.toString());
        } catch (JSONException e) {
            String message = "Generate HeartbeatInfo error | " + e.getMessage();
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

    protected String generateData() {
        String data;
        data = generateBatteryInfo().toString() + ",";
        data = data + generateGpsInfo().toString() + ",";
        data = data + generateMobileInfo().toString() + ",";
        data = "[" + data + generateGSensorInfo() + "]";
        return data;
    }

    protected JSONObject generateBatteryInfo() {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonCustomData = new JSONObject();
        try {
            jsonObject.put("detectItem", "Battery");
            jsonObject.put("detectValue", "OK");
            jsonCustomData.put("customKey", "BatteryLevel");
            jsonCustomData.put("customValue", String.valueOf(mSharedData.getBatteryLevel()));
            jsonObject.put("customData", new JSONArray("[" + jsonCustomData.toString() + "]"));
            Log.d(TAG, "Battery info | " + jsonObject.toString());
        } catch (JSONException e) {
            String message = "Generate BatteryInfo error | " + e.getMessage();
            Log.d(TAG, message);
            mLogFileWriter.appendLog(message);
        }
        return jsonObject;
    }

    protected JSONObject generateGpsInfo() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("detectItem", "GPS");
            jsonObject.put("detectValue", "OK");
            jsonObject.put("customData", new JSONArray(generateCustomData()));
        } catch (JSONException e) {
            String message = "Generate GpsInfo error | " + e.getMessage();
            Log.d(TAG, message);
            mLogFileWriter.appendLog(message);
        }
        return jsonObject;
    }

    protected JSONObject generateMobileInfo() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("detectItem", "Mobile");
            jsonObject.put("detectValue", "OK");
            jsonObject.put("customData", new JSONArray(generateCustomData()));
        } catch (JSONException e) {
            String message = "Generate MobileInfo error | " + e.getMessage();
            Log.d(TAG, message);
            mLogFileWriter.appendLog(message);
        }
        return jsonObject;
    }

    protected JSONObject generateGSensorInfo() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("detectItem", "GSensor");
            jsonObject.put("detectValue", "OK");
            jsonObject.put("customData", new JSONArray(generateCustomData()));
        } catch (JSONException e) {
            String message = "Generate GSensorInfo error | " + e.getMessage();
            Log.d(TAG, message);
            mLogFileWriter.appendLog(message);
        }
        return jsonObject;
    }

    protected String generateCustomData() {
        String result;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("customKey", "");
            jsonObject.put("customValue", "");
        } catch (JSONException e) {
            String message = "Generate CustomData error | " + e.getMessage();
            Log.d(TAG, message);
            mLogFileWriter.appendLog(message);
        }
        result = "[" + jsonObject.toString() + "]";
        return  result;
    }

    protected void send() {
        int result;
        result = new ChtHttpConnection().sendDataToCht(generateHeartbeatInfo(), UBI_CLOUD_URL + HEARTBEAT_URL, HEARTBEAT_URL);
        String message = "Send heartbeat result | " + String.valueOf(result);
        Log.d(TAG, message);
        mLogFileWriter.appendLog(message);
    }
}

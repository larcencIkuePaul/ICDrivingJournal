package com.sanjetco.ad10cht.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.util.Log;

import com.sanjetco.ad10cht.common.MainCommon;
import com.sanjetco.ad10cht.callback.SensorListenerCallback;
import com.sanjetco.ad10cht.callback.SystemLogCallback;
import com.sanjetco.ad10cht.common.SharedData;

/**
 * Created by PaulLee on 2016/4/26.
 * Sensor listener
 */
public class SensorListener implements
        SensorEventListener2,
        MainCommon {

    Context mContext;
    SensorManager mSensorManager;
    Sensor mAccelSensor;
    SensorListenerCallback mSensorListenerCallback;
    SystemLogCallback mSystemLogCallback;
    float[] mAccelValue = new float[] {0, 0, 0};
    static SharedData mSharedData = new SharedData();

    public SensorListener(Object object) {
        mContext = (Context) object;
        mSensorListenerCallback = (SensorListenerCallback) object;
        mSystemLogCallback = (SystemLogCallback) object;
    }

    public void initSensorListener() {
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mAccelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Log.d(TAG, "SensorListener STARTED");
        mSystemLogCallback.updateSystemLog("SensorListener STARTED");
    }

    public void startListen() {
        mSensorManager.registerListener(this, mAccelSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "Listen Accelerometer Sensor STARTED");
        mSystemLogCallback.updateSystemLog("Listen Accelerometer Sensor STARTED");
    }

    public void stopListen() {
        mSensorManager.unregisterListener(this, mAccelSensor);
        Log.d(TAG, "Listen Accelerometer Sensor STOPPED");
        mSystemLogCallback.updateSystemLog("Listen Accelerometer Sensor STOPPED");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        System.arraycopy(event.values, 0, mAccelValue, 0, event.values.length);
        mSensorListenerCallback.throwSensorData(mAccelValue);
        mSharedData.update3AxisAccelerationData(mAccelValue);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onFlushCompleted(Sensor sensor) {

    }
}

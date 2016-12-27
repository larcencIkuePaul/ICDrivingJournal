package com.sanjetco.ad10cht.listener;

import android.content.Context;
import android.util.Log;

import com.intel.telematics.comm.IHardwareStatusListener;
import com.intel.telematics.comm.IOBDDataChangeListener;
import com.intel.telematics.comm.OBDCommManager;
import com.intel.telematics.config.ObdConfig;
import com.sanjetco.ad10cht.common.MainCommon;
import com.sanjetco.ad10cht.callback.SystemLogCallback;

import java.util.Arrays;

/**
 * Created by PaulLee on 2016/4/22.
 * Register and listen Obd2 data
 */
public class Obd2Listener implements
        IOBDDataChangeListener,
        IHardwareStatusListener,
        MainCommon
{
    Context mContext;
    OBDCommManager mObdCommMgr;
    SystemLogCallback mSystemLogCallback;
    int mRegId = 0;

    public Obd2Listener(Object object) {
        mContext = (Context) object;
        mSystemLogCallback = (SystemLogCallback) object;
        mObdCommMgr = new OBDCommManager(mContext);
        mObdCommMgr.initServiceConnection(this);
        Log.d(TAG, "Obd2Listener STARTED");
    }

    @Override
    public void onHardwareOpenStatus(boolean status) {
        if (status) {
            int[] pids = {
                    ObdConfig.Mode1Mode2Pid.engineRPM,
                    ObdConfig.Mode1Mode2Pid.vehicleSpeed
            };
            mRegId = mObdCommMgr.registerObdPids(
                    (char)ObdConfig.MODE_REQUEST_LIVEDATA,
                    ObdConfig.SamplingRate5, pids, this);
            Log.d(TAG, "OBD2 Common Manager Registration ID | " + String.valueOf(mRegId));
        }
    }

    @Override
    public void onCyclicObdData(int[] data, int pid, char mode, int registrationId, long timestamp) {
        String result = Obd2DataParser(data, pid);
        Log.d(TAG, result);
        mSystemLogCallback.updateSystemLog(result);
    }

    @Override
    public void onObdDataChange(int[] data, int pid, char mode, int registrationId, long timestamp) {

    }

    protected String Obd2DataParser(int[] data, int pid) {
        String result;
        switch (pid) {
            case ObdConfig.Mode1Mode2Pid.engineRPM:
                result = String.valueOf(((float)data[0] * 256 + (float)data[1]) / 4) + " rpm";
                break;
            case ObdConfig.Mode1Mode2Pid.vehicleSpeed:
                result = String.valueOf(data[0]) + " km/h";
                break;
            default:
                result = Arrays.toString(data);
                break;
        }
        return result;
    }
}

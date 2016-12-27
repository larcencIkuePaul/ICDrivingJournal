package com.sanjetco.ad10cht.watcher;

import android.util.Log;

import com.sanjetco.ad10cht.callback.SystemEventListenerCallback;
import com.sanjetco.ad10cht.common.MainCommon;
import com.sanjetco.ad10cht.listener.SystemEventListener;
import com.sanjetco.ad10cht.log.LogFileWriter;

/**
 * Created by PaulLee on 2016/5/13.
 * Ignition status watcher
 */
public class IgnitionStatusWatcher implements MainCommon {

    SystemEventListener mSystemEventListener;
    SystemEventListenerCallback mSystemEventListenerCallback;
    LogFileWriter mLogFileWriter = new LogFileWriter();
    boolean mRunIgnitionStatusWatcher = false;
    long IGNITION_STATUS_CHECK_DURATION_MILLISEC = 10000;

    public IgnitionStatusWatcher(SystemEventListenerCallback systemEventListenerCallback, SystemEventListener systemEventListener) {
        mSystemEventListenerCallback = systemEventListenerCallback;
        mSystemEventListener = systemEventListener;
    }

    public void start() {
        mRunIgnitionStatusWatcher = true;
        mThread.start();
        String message = "Ignition status watcher STARTED";
        Log.d(TAG, message);
        mLogFileWriter.appendLog(message);
    }

    public void stop() {
        mRunIgnitionStatusWatcher = false;
        String message = "Ignition status watcher STOPPED";
        Log.d(TAG, message);
        mLogFileWriter.appendLog(message);
    }

    Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (mRunIgnitionStatusWatcher) {
                try {
                    Thread.sleep(IGNITION_STATUS_CHECK_DURATION_MILLISEC);
                    boolean status = mSystemEventListener.checkIgnitionStatus();
                    mSystemEventListenerCallback.notifyIgnitionStatusPolling(status);
                    Log.d(TAG, "Ignition status | " + String.valueOf(status));
                    if (!status) {
                        String message = "Ignition off detected";
                        Log.d(TAG, message);
                        mLogFileWriter.appendLog(message);
                    }
                } catch (InterruptedException e) {
                    String message = "Ignition status interrupt exception | " + e.getMessage();
                    Log.d(TAG, message);
                    mLogFileWriter.appendLog(message);
                }
            }
        }
    });
}

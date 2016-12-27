package com.sanjetco.ad10cht.listener;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;

import com.sanjetco.ad10cht.common.MainCommon;
import com.sanjetco.ad10cht.log.LogFileWriter;

/**
 * Created by PaulLee on 2016/5/10.
 */
public class GpsListener implements MainCommon {

    Context mContext;
    LocationManager mLocationManager;
    LocationListener mLocationListener;
    GpsStatus.NmeaListener mNmeaListener;
    LogFileWriter mLogFileWriter = new LogFileWriter();

    public GpsListener(Context context, LocationListener locationListener, GpsStatus.NmeaListener nmeaListener) {
        mContext = context;
        mLocationListener = locationListener;
        mNmeaListener = nmeaListener;
    }

    public void initGpsListener() {
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    public void startListen() {
        String message;
        if (Build.VERSION.SDK_INT >= 23 &&
                mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            message = "No location permission granted";
            Log.d(TAG, message);
            mLogFileWriter.appendLog(message);
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mLocationListener);
        message = "Listen GPS STARTED";
        Log.d(TAG, message);
        mLogFileWriter.appendLog(message);

        mLocationManager.addNmeaListener(mNmeaListener);
        message = "Listen NMEA STARTED";
        Log.d(TAG, message);
        mLogFileWriter.appendLog(message);

        message = "GpsListener STARTED";
        Log.d(TAG, message);
        mLogFileWriter.appendLog(message);
    }

    public void stopListen() {
        String message;
        if (Build.VERSION.SDK_INT >= 23 &&
                mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            message = "No location permission granted";
            Log.d(TAG, message);
            mLogFileWriter.appendLog(message);
            return;
        }
        mLocationManager.removeUpdates(mLocationListener);
        message = "Listen GPS STOPPED";
        Log.d(TAG, message);
        mLogFileWriter.appendLog(message);

        mLocationManager.removeNmeaListener(mNmeaListener);
        message = "Listen NMEA STOPPED";
        Log.d(TAG, message);
        mLogFileWriter.appendLog(message);
    }
}

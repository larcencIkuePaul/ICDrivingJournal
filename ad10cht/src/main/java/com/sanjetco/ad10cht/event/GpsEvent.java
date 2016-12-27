package com.sanjetco.ad10cht.event;

import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.sanjetco.ad10cht.common.ChtGpsDataPack;
import com.sanjetco.ad10cht.callback.GpsEventCallback;
import com.sanjetco.ad10cht.common.MainCommon;
import com.sanjetco.ad10cht.common.SharedData;
import com.sanjetco.ad10cht.listener.GpsListener;
import com.sanjetco.ad10cht.log.LogFileWriter;

/**
 * Created by PaulLee on 2016/5/10.
 * Gps event
 */
public class GpsEvent implements MainCommon {

    Context mContext;
    GpsEventCallback mGpsEventCallback;
    GpsListener mGpsListener;
    ChtGpsDataPack mChtGpsDataPack = new ChtGpsDataPack();
    EventFileWriter mEventFileWriter;
    static SharedData mSharedData = new SharedData();
    LogFileWriter mLogFileWriter = new LogFileWriter();

    static final String GPGSA_HEADER = "$GPGSA";

    public GpsEvent(Object object) {
        mContext = (Context) object;
        mGpsEventCallback = (GpsEventCallback) object;
        mEventFileWriter = new EventFileWriter();
    }

    public void startEvent() {
        mGpsListener = new GpsListener(mContext, mLocationListener, mNmeaListener);
        mGpsListener.initGpsListener();
        mGpsListener.startListen();
        String message = "GpsEvent STARTED";
        Log.d(TAG, message);
        mLogFileWriter.appendLog(message);
    }

    public void stopEvent() {
        mGpsListener.stopListen();
        String message = "GpsEvent STOPPED";
        Log.d(TAG, message);
        mLogFileWriter.appendLog(message);
    }

    LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            getGpsData(location);
            mGpsEventCallback.throwGpsData(mChtGpsDataPack);
            mEventFileWriter.writeGpsTrackInfo(DrivingEvent.mGpsTrackInfoName, mChtGpsDataPack);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        public void getGpsData(Location location) {
            mChtGpsDataPack.altitude = location.getAltitude();
            mChtGpsDataPack.bearing = location.getBearing();
            mChtGpsDataPack.lat = location.getLatitude();
            mChtGpsDataPack.lon = location.getLongitude();
            mChtGpsDataPack.speed = location.getSpeed();
            mChtGpsDataPack.time = location.getTime();
            mSharedData.updateChtGpsDataPack(mChtGpsDataPack);
        }
    };

    GpsStatus.NmeaListener mNmeaListener = new GpsStatus.NmeaListener() {
        @Override
        public void onNmeaReceived(long timestamp, String nmea) {
            getDopDataFromNmea(nmea);
        }

        /*************************************************************************
         *  Nmea example: $GPGSA,A,3,10,07,05,02,29,04,08,13,,,,,1.72,1.03,1.38*0A
         *  data[15]: pdop = 1.72
         *  data[16]: hdop = 1.03
         *  data[17]: vdop = 1.38
         *  data[2]: 1: Not positioned; 2: 2D positioned; 3: 3D positioned
         *************************************************************************/
        public void getDopDataFromNmea(String nmea) {
            if ( nmea.startsWith(GPGSA_HEADER) ) {
                Log.d(TAG, "$GPGSA string | " + nmea);
                String data[] = nmea.split(",");
                if ( data[2].equals("1") ) {
                    return;
                }
                mChtGpsDataPack.pdop = Float.valueOf(data[15]);
                mChtGpsDataPack.hdop = Float.valueOf(data[16]);
                mChtGpsDataPack.vdop = Float.valueOf(data[17].split("\\*")[0]);
            }
        }
    };
}

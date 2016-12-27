package com.sanjetco.ad10cht.event;

import com.sanjetco.ad10cht.callback.GpsEventCallback;
import com.sanjetco.ad10cht.common.ChtGpsDataPack;
import com.sanjetco.ad10cht.common.MainCommon;
import com.sanjetco.ad10cht.callback.SensorListenerCallback;
import com.sanjetco.ad10cht.callback.SystemEventListenerCallback;
import com.sanjetco.ad10cht.callback.SystemLogCallback;

/**
 * Created by PaulLee on 2016/5/12.
 */
public class EventCenter implements
        MainCommon,
        SensorListenerCallback,
        GpsEventCallback,
        SystemEventListenerCallback,
        SystemLogCallback {

    @Override
    public void throwGpsData(ChtGpsDataPack pack) {

    }

    @Override
    public void throwSensorData(float[] data) {

    }

    @Override
    public void notifyIgnitionStatusPolling(boolean status) {

    }

    @Override
    public void notifyIgnitionEventRaised(boolean status) {

    }

    @Override
    public void notifyDongleConnected() {

    }

    @Override
    public void notifyDongleDetached() {

    }

    @Override
    public void updateSystemLog(String message) {

    }

    @Override
    public void notifyLowCarBattery() {

    }
}

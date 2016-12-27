package com.sanjetco.ad10cht.callback;

import com.sanjetco.ad10cht.common.ChtGpsDataPack;

/**
 * Created by PaulLee on 2016/5/10.
 */
public interface GpsEventCallback {
    void throwGpsData(ChtGpsDataPack pack);
}

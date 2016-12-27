package com.sanjetco.ad10cht.common;

/**
 * Created by PaulLee on 2016/5/10.
 */
public interface DatabaseCommon {

    String GPS_PATH = "/sdcard/SJ/Driving/GPS";
    String GSENSOR_PATH = "/sdcard/SJ/Driving/GSensor";
    int TYPE_GPS = 0x1;
    int TYPE_GSENSOR = 0x2;

    class PendingFilePack {
        public int id;
        public long timestamp;
        public String name;
        public int type;
    }
}

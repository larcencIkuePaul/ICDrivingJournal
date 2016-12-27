package com.sanjetco.ad10cht.common;

/**
 * Created by PaulLee on 2016/5/10.
 */
public interface HttpCommon {

    //String UBI_CLOUD_URL = "http://202.39.164.38/ubiapi";
    String UBI_CLOUD_URL = "http://ubi.iot.cht.com.tw/ubiapi-test";
    String UBI_CLOUD_DONGLE_URL = "http://202.39.164.38/ubiapi-test";

    String GPS_URL = "/v1/tracks/gps";
    String GSENSOR_URL = "/v1/tracks/gsensor";
    String HEARTBEAT_URL = "/v1/dongle/heartbeat";
    String EVENT_URL = "/v1/dongle/event";
}

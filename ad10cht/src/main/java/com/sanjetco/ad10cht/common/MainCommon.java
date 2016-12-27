package com.sanjetco.ad10cht.common;

/**
 * Created by PaulLee on 2016/4/22.
 */
public interface MainCommon {

    // Definition of debug tag
    String TAG = "AD10CHT";

    // Definition of UI enable
    boolean ENABLE_UI = false;

    // Definition of intent action
    String INTENT_ACTION_BROADCAST_ACCELROMETER_DATA =
            "com.sanjetco.ad10cht.common.INTENT_ACTION_BROADCAST_ACCELROMETER_DATA";
    String INTENT_ACTION_UPDATE_UI =
            "com.sanjetco.ad10cht.common.INTENT_ACTION_UPDATE_UI";
    String INTENT_ACTION_SYSTEM_IGNITION_STATUS =
            "com.sanjetco.ad10cht.common.INTENT_ACTION_SYSTEM_IGNITION_STATUS";

    // Definition of extra string
    String EXTRA_ACCELEROMETER_DATA = "com.sanjetco.ad10cht.common.EXTRA_ACCELEROMETER_DATA";
    String EXTRA_SYS_MSG = "com.sanjetco.ad10cht.common.EXTRA_SYS_MSG";
    String EXTRA_DATA_TYPE = "com.sanjetco.ad10cht.common.EXTRA_DATA_TYPE";
    String EXTRA_SYSTEM_IGNITION_STATUS = "com.sanjetco.ad10cht.common.EXTRA_SYSTEM_IGNITION_STATUS";

    // Definition of message type
    int TYPE_ACCELEROMETER_DATA = 0;
    int TYPE_SYS_MSG = 1;
}

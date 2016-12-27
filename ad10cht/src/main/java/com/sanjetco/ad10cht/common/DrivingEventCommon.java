package com.sanjetco.ad10cht.common;

/**
 * Created by PaulLee on 2016/5/11.
 */
public interface DrivingEventCommon {

    int TYPE_ACCELERATION_EVENT = 0x1;
    int TYPE_BRAKING_EVENT = 0x2;
    int TYPE_REVERSE_EVENT = 0x3;
    int TYPE_SWERVE_EVENT = 0x4;

    // Default wrap driving event duration
    //long DEFAULT_WRAP_DRIVING_EVENT_DURATION_SEC = 10;
    long DEFAULT_WRAP_DRIVING_EVENT_DURATION_SEC = 1800;

    boolean mGsensorRun = true;
    boolean mGpsRun = true;
}

package com.sanjetco.ad10cht.common;

/**
 * Created by PaulLee on 2016/5/13.
 */
public class SharedData {

    protected static ChtGpsDataPack mChtGpsDataPack = new ChtGpsDataPack();
    protected static float[] m3AxisAccelerationData = new float[] {0f, 0f, 0f};
    protected static String mImeiId = "";
    protected static int mBatteryLevel = 100;

    public SharedData() {

    }

    public void updateChtGpsDataPack(ChtGpsDataPack chtGpsDataPack) {
        mChtGpsDataPack = chtGpsDataPack;
    }
    public ChtGpsDataPack getChtGpsDataPack() {
        return mChtGpsDataPack;
    }

    public void update3AxisAccelerationData(float[] data) {
        System.arraycopy(data, 0, m3AxisAccelerationData, 0, m3AxisAccelerationData.length);
    }
    public float[] get3AxisAccelerationData() {
        return m3AxisAccelerationData;
    }

    public void updateImeiId(String id) {
        mImeiId = id;
    }
    public String getImeiId() {
        return mImeiId;
    }

    public void updateBatteryLevel(int level) {
        mBatteryLevel = level;
    }
    public int getBatteryLevel() {
        return mBatteryLevel;
    }
}

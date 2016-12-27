package com.sanjetco.ad10cht.event;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.sanjetco.ad10cht.common.DatabaseCommon;
import com.sanjetco.ad10cht.common.DrivingEventCommon;
import com.sanjetco.ad10cht.common.MainCommon;
import com.sanjetco.ad10cht.common.SharedData;
import com.sanjetco.ad10cht.database.DatabaseManager;
import com.sanjetco.ad10cht.database.DrivingJournalManager;
import com.sanjetco.ad10cht.log.LogFileWriter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by PaulLee on 2016/5/6.
 * Driving event
 */
public class DrivingEvent implements
        MainCommon,
        DatabaseCommon,
        DrivingEventCommon
{
    Context mContext;
    DatabaseManager mDatabaseManager;
    DrivingJournalManager mDrivingJournalManager;
    EventFileWriter mEventFileWriter;
    LogFileWriter mLogFileWriter = new LogFileWriter();
    static SharedData mSharedData = new SharedData();

    // 3-Axis acceleration data
    static float[] m3AxisAccelerationData = new float[] {0f, 0f, 0f};

    // ID variables
    static String mAndroidId = "";
    public static String mImeiId = "";

    // Global time variables
    static long mCurrentTime;
    static long mStartTime;
    static long mTripStartTime;

    // Global date variables
    public static String mTripStartDate = "";

    // Event trip ID varaibles
    static String mTripId = "";

    // Event time variables
    static long mAccelerationEventStartTime;
    static long mBrakingEventStartTime;
    static long mReverseEventStartTime;
    static long mSwerveEventStartTime;
    static long mAccelerationEventEndTime;
    static long mBrakingEventEndTime;
    static long mReverseEventEndTime;
    static long mSwerveEventEndTime;

    // Event count variables
    static int mAccelerationEventCount = 0;
    static int mBrakingEventCount = 0;
    static int mReverseEventCount = 0;
    static int mSwerveEventCount = 0;

    // Event monitor status variables
    static boolean mAccelerationEventMonitoring = false;
    static boolean mBrakingEventMonitoring = false;
    static boolean mReverseEventMonitoring = false;
    static boolean mSwerveEventMonitoring = false;

    // Event record status variables
    static boolean mAccelerationEventRecording = false;
    static boolean mBrakingEventRecording = false;
    static boolean mReverseEventRecording = false;
    static boolean mSwerveEventRecording = false;

    // Event velocity variables
    static float mMaxAccelerationEventVelocity = 0;
    static float mMaxBrakingEventVelocity = 0;
    static float mMinReverseEventVelocity = SensorManager.GRAVITY_EARTH;
    static float mMaxMinSwerveEventVelocity = 0;

    // Journal name variables
    static String mGpsJournalName = "";
    static String mGsensorJournalName = "";
    static String mGpsTrackInfoName = "";
    static String mGsensorTrackInfoName = "";
    static String mAccelerationEventName = "";
    static String mBrakingEventName = "";
    static String mReverseEventName = "";
    static String mSwerveEventName = "";

    // Event threshold constants
    static final float ACCELERATION_EVENT_Y_AXIS_VELOCITY_THRESHOLD = 3.5f;
    static final float BRAKING_EVENT_Y_AXIS_VELOCITY_THRESHOLD = -3.5F;
    static final float SWERVE_EVENT_X_AXIS_VELOCITY_THRESHOLD = 4.9f;
    static final float REVERSE_EVENT_X_AXIS_VELOCITY_THRESHOLD = 4.9f;
    static final float REVERSE_EVENT_Y_AXIS_VELOCITY_THRESHOLD = 3.5f;
    static final float REVERSE_EVENT_Z_AXIS_VELOCITY_THRESHOLD = 4.9f;

    static final long ACCELERATION_EVENT_DURATION_THRESHOLD_SEC = 1;
    static final long BRAKING_EVENT_DURATION_THRESHOLD_SEC = 1;
    static final long SWERVE_EVENT_DURATION_THRESHOLD_SEC = 1;
    static final long REVERSE_EVENT_DURATION_THRESHOLD_SEC = 1;


    public DrivingEvent(Context context, DrivingJournalManager manager) {
        mContext = context;
        mDrivingJournalManager = manager;
        mEventFileWriter = new EventFileWriter();
    }

    public void initAll(DatabaseManager databaseManager) {
        initDeviceId();
        initCurrentTimeVariable();
        initTripStartDate();
        initJournalName();
        initEventVariable();
        initDatabase(databaseManager);
    }

    public void initDeviceId() {
        String message;
        if (Build.VERSION.SDK_INT < 23) {
            mAndroidId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            mImeiId = telephonyManager.getDeviceId();
        }
        else if (Build.VERSION.SDK_INT >= 23 &&
                mContext.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            mAndroidId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            mImeiId = telephonyManager.getDeviceId();
        } else {
            mAndroidId = "empty_android_id";
            mImeiId = "empty_imei_id";
        }
        mSharedData.updateImeiId(mImeiId);

        message = "Android ID | " + mAndroidId;
        Log.d(TAG, message);
        mLogFileWriter.appendLog(message);

        message = "IMEI ID | " + mImeiId;
        Log.d(TAG, message);
        mLogFileWriter.appendLog(message);
    }

    public void initCurrentTimeVariable() {
        String message;
        mCurrentTime = System.currentTimeMillis() / 1000;
        mStartTime = mCurrentTime;

        message = "Start time | " + mStartTime;
        Log.d(TAG, message);
        mLogFileWriter.appendLog(message);
    }

    public void initTripStartDate() {
        String message;
        mTripStartTime = mStartTime;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmssZZZ", Locale.TAIWAN);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date(mTripStartTime * 1000);
        mTripStartDate = sdf.format(date);
        message = "Trip start date | " + mTripStartDate;
        Log.d(TAG, message);
        mLogFileWriter.appendLog(message);
    }

    public void initJournalName() {
        mGpsJournalName = mImeiId + "-" + mStartTime + "-" + "GPS";
        mGsensorJournalName = mImeiId + "-" + mStartTime + "-" + "GSensor";
        mGpsTrackInfoName = mImeiId + "-" + String.valueOf(mStartTime) + "-" + "track";
        mGsensorTrackInfoName = mImeiId + "-" + String.valueOf(mStartTime) + "-" + "gstrack";
        mAccelerationEventName = mImeiId + "-" + String.valueOf(mStartTime) + "-" + "acceleration";
        mBrakingEventName = mImeiId + "-" + String.valueOf(mStartTime) + "-" + "braking";
        mReverseEventName = mImeiId + "-" + String.valueOf(mStartTime) + "-" + "reverse";
        mSwerveEventName = mImeiId + "-" + String.valueOf(mStartTime) + "-" + "swerve";
    }

    public void initEventVariable() {
        String message;
        mAccelerationEventStartTime = 0;
        mBrakingEventStartTime = 0;
        mReverseEventStartTime = 0;
        mSwerveEventStartTime = 0;
        mAccelerationEventEndTime = 0;
        mBrakingEventEndTime = 0;
        mReverseEventEndTime = 0;
        mSwerveEventEndTime = 0;

        mAccelerationEventCount = 0;
        mBrakingEventCount = 0;
        mReverseEventCount = 0;
        mSwerveEventCount = 0;

        mAccelerationEventMonitoring = false;
        mBrakingEventMonitoring = false;
        mReverseEventMonitoring = false;
        mSwerveEventMonitoring = false;

        mAccelerationEventRecording = false;
        mBrakingEventRecording = false;
        mReverseEventRecording = false;
        mSwerveEventRecording = false;

        mMaxAccelerationEventVelocity = 0;
        mMaxBrakingEventVelocity = 0;
        mMinReverseEventVelocity = SensorManager.GRAVITY_EARTH;
        mMaxMinSwerveEventVelocity = 0;

        mTripId = mImeiId + String.valueOf(mStartTime);
        message = "Trip Id | " + mTripId;
        Log.d(TAG, "Trip Id | " + mTripId);
        mLogFileWriter.appendLog(message);

        message = "Init. event variables done";
        Log.d(TAG, message);
        mLogFileWriter.appendLog(message);
    }

    public void initDatabase(DatabaseManager databaseManager) {
        String message;
        mDatabaseManager = databaseManager;
        message = "Init. database done";
        Log.d(TAG, message);
        mLogFileWriter.appendLog(message);
    }

    public void recvSensorDataFromMainService(float[] data) {
        System.arraycopy(data, 0, m3AxisAccelerationData, 0, m3AxisAccelerationData.length);
        //checkAllDrivingEvent(m3AxisAccelerationData);
        checkCollectDataTimeout();
    }

    protected void checkAllDrivingEvent(float[] data) {
        checkAccelerationEvent(data);
        checkBrakingEvent(data);
        checkReverseEvent(data);
        checkSwerveEvent(data);
    }

    protected void checkCollectDataTimeout() {
        if ((System.currentTimeMillis() / 1000 - mStartTime) >= DEFAULT_WRAP_DRIVING_EVENT_DURATION_SEC) {
            wrapAllDrivingEvent();
            wrapAllGpsEvent();
            initCurrentTimeVariable();
            initJournalName();
            initEventVariable();
            initTripStartDate();
        }
    }

    public void wrapDrivingEventBeforeShutdown() {
        wrapAllDrivingEvent();
        wrapAllGpsEvent();
        initCurrentTimeVariable();
        initJournalName();
        initEventVariable();
        initTripStartDate();
    }

    protected void checkAccelerationEvent(float[] data) {
        if (data[1] >= ACCELERATION_EVENT_Y_AXIS_VELOCITY_THRESHOLD) {
            if ( !mAccelerationEventMonitoring ) {
                mAccelerationEventStartTime = System.currentTimeMillis() / 1000;
                mMaxAccelerationEventVelocity = data[1];
                mAccelerationEventMonitoring = true;
            } else {
                if ( !mAccelerationEventRecording ) {
                    if ( (System.currentTimeMillis() / 1000 - mAccelerationEventStartTime) > ACCELERATION_EVENT_DURATION_THRESHOLD_SEC ) {
                        mAccelerationEventRecording = true;
                        mAccelerationEventCount++;
                        if (data[1] > mMaxAccelerationEventVelocity) mMaxAccelerationEventVelocity = data[1];
                    } else {
                        // Sudden acceleration is monitoring
                        if (data[1] > mMaxAccelerationEventVelocity) mMaxAccelerationEventVelocity = data[1];
                    }
                } else {
                    // Sudden acceleration is recording
                    if (data[1] > mMaxAccelerationEventVelocity) mMaxAccelerationEventVelocity = data[1];
                }
            }
        } else if (data[1] < ACCELERATION_EVENT_Y_AXIS_VELOCITY_THRESHOLD && mAccelerationEventMonitoring) {
            if (mAccelerationEventRecording) {
                mAccelerationEventEndTime = System.currentTimeMillis() / 1000;

                // Write event information to file
                mEventFileWriter.writeAccelerationEvent(mAccelerationEventName, mAccelerationEventStartTime, mAccelerationEventEndTime, mMaxAccelerationEventVelocity);
                mEventFileWriter.writeGsensorTrackInfo(mGsensorTrackInfoName, m3AxisAccelerationData, mAccelerationEventStartTime);

                mAccelerationEventRecording = false;
            } else {
                // Sudden acceleration's duration < ACCELERATION_DURATION
            }
            mAccelerationEventStartTime = 0;
            mMaxAccelerationEventVelocity = 0;
            mAccelerationEventMonitoring = false;
        }
    }

    protected void checkBrakingEvent(float[] data) {
        if (data[1] <= BRAKING_EVENT_Y_AXIS_VELOCITY_THRESHOLD) {
            if ( !mBrakingEventMonitoring ) {
                mBrakingEventStartTime = System.currentTimeMillis() / 1000;
                mMaxBrakingEventVelocity = data[1];
                mBrakingEventMonitoring = true;
            } else {
                if ( !mBrakingEventRecording ) {
                    if ( (System.currentTimeMillis() / 1000 - mBrakingEventStartTime) > BRAKING_EVENT_DURATION_THRESHOLD_SEC ) {
                        mBrakingEventRecording = true;
                        mBrakingEventCount++;
                        if (data[1] < mMaxBrakingEventVelocity) mMaxBrakingEventVelocity = data[1];
                    } else {
                        // Sudden braking is monitoring
                        if (data[1] < mMaxBrakingEventVelocity) mMaxBrakingEventVelocity = data[1];
                    }
                } else {
                    // Sudden braking is recording
                    if (data[1] < mMaxBrakingEventVelocity) mMaxBrakingEventVelocity = data[1];
                }
            }
        } else if (data[1] > BRAKING_EVENT_Y_AXIS_VELOCITY_THRESHOLD && mBrakingEventMonitoring) {
            if (mBrakingEventRecording) {
                mBrakingEventEndTime = System.currentTimeMillis() / 1000;

                // Write event information to file
                mEventFileWriter.writeBrakingEvent(mBrakingEventName, mBrakingEventStartTime, mBrakingEventEndTime, mMaxBrakingEventVelocity);
                mEventFileWriter.writeGsensorTrackInfo(mGsensorTrackInfoName, m3AxisAccelerationData, mBrakingEventStartTime);

                mBrakingEventRecording = false;
            } else {
                // Sudden braking's duration < BRAKING_DURATION
            }
            mBrakingEventStartTime = 0;
            mMaxBrakingEventVelocity = 0;
            mBrakingEventMonitoring = false;
        }
    }

    protected void checkReverseEvent(float[] data) {
        if (data[2] < REVERSE_EVENT_Z_AXIS_VELOCITY_THRESHOLD) {
            if ( Math.abs(data[0]) > REVERSE_EVENT_X_AXIS_VELOCITY_THRESHOLD || Math.abs(data[1]) > REVERSE_EVENT_Y_AXIS_VELOCITY_THRESHOLD) {
                if ( !mReverseEventMonitoring ) {
                    mReverseEventStartTime = System.currentTimeMillis() / 1000;
                    mMinReverseEventVelocity = data[2];
                    mReverseEventMonitoring = true;
                } else {
                    if ( !mReverseEventRecording) {
                        if ( (System.currentTimeMillis() / 1000 - mReverseEventStartTime) > REVERSE_EVENT_DURATION_THRESHOLD_SEC ) {
                            mReverseEventRecording = true;
                            mReverseEventCount++;
                            if (data[2] < mMinReverseEventVelocity) mMinReverseEventVelocity = data[2];
                        } else {
                            // Sudden reverse is monitoring
                            if (data[2] < mMinReverseEventVelocity) mMinReverseEventVelocity = data[2];
                        }
                    } else {
                        // Sudden reverse is recording
                        if (data[2] < mMinReverseEventVelocity) mMinReverseEventVelocity = data[2];
                    }
                }
            }
        } else if (data[2] > REVERSE_EVENT_Z_AXIS_VELOCITY_THRESHOLD && mReverseEventMonitoring) {
            if (mReverseEventRecording) {
                mReverseEventEndTime = System.currentTimeMillis() / 1000;

                // Write event information to file
                mEventFileWriter.writeReverseEvent(mReverseEventName, mReverseEventStartTime, mReverseEventEndTime, mMinReverseEventVelocity);
                mEventFileWriter.writeGsensorTrackInfo(mGsensorTrackInfoName, m3AxisAccelerationData, mReverseEventStartTime);

                mReverseEventRecording = false;
            } else {
                // Sudden reverse's duration < REVERSE_DURATION
            }
            mReverseEventStartTime = 0;
            mMinReverseEventVelocity = (float)9.8;
            mReverseEventMonitoring = false;
        }
    }

    protected void checkSwerveEvent(float[] data) {
        if ( Math.abs(data[0]) >= SWERVE_EVENT_X_AXIS_VELOCITY_THRESHOLD ) {
            if ( !mSwerveEventMonitoring ) {
                mSwerveEventStartTime = System.currentTimeMillis() / 1000;
                mMaxMinSwerveEventVelocity = data[0];
                mSwerveEventMonitoring = true;
            } else {
                if ( !mSwerveEventRecording ) {
                    if ( (System.currentTimeMillis() / 1000 - mSwerveEventStartTime) > SWERVE_EVENT_DURATION_THRESHOLD_SEC ) {
                        mSwerveEventRecording = true;
                        mSwerveEventCount++;
                        if ( Math.abs(data[0]) > Math.abs(mMaxMinSwerveEventVelocity) ) mMaxMinSwerveEventVelocity = data[0];
                    } else {
                        // Sudden swerve is monitoring
                        if ( Math.abs(data[0]) > Math.abs(mMaxMinSwerveEventVelocity) )mMaxMinSwerveEventVelocity = data[0];
                    }
                } else {
                    // Sudden swerve is recording
                    if ( Math.abs(data[0]) > Math.abs(mMaxMinSwerveEventVelocity) ) mMaxMinSwerveEventVelocity = data[0];
                }
            }
        } else if ( Math.abs(data[0]) < SWERVE_EVENT_X_AXIS_VELOCITY_THRESHOLD && mSwerveEventMonitoring) {
            if (mSwerveEventRecording) {
                mSwerveEventEndTime = System.currentTimeMillis() / 1000;

                // Write event information to file
                mEventFileWriter.writeSwerveEvent(mSwerveEventName, mSwerveEventStartTime, mSwerveEventEndTime, mMaxMinSwerveEventVelocity);
                mEventFileWriter.writeGsensorTrackInfo(mGsensorTrackInfoName, m3AxisAccelerationData, mSwerveEventStartTime);

                mSwerveEventRecording = false;
            } else {
                // Sudden braking's duration < SWERVE_DURATION
            }
            mSwerveEventStartTime = 0;
            mMaxMinSwerveEventVelocity = 0;
            mSwerveEventMonitoring = false;
        }
    }

    protected void collectDataImmediately() {
        if (mAccelerationEventRecording) {
            mAccelerationEventEndTime = System.currentTimeMillis() / 1000;
            mEventFileWriter.writeAccelerationEvent(mAccelerationEventName, mAccelerationEventStartTime, mAccelerationEventEndTime, mMaxAccelerationEventVelocity);
            mEventFileWriter.writeGsensorTrackInfo(mGsensorTrackInfoName, m3AxisAccelerationData, mAccelerationEventStartTime);
            mAccelerationEventRecording = false;
        }
        if (mBrakingEventRecording) {
            mBrakingEventEndTime = System.currentTimeMillis() / 1000;
            mEventFileWriter.writeBrakingEvent(mBrakingEventName, mBrakingEventStartTime, mBrakingEventEndTime, mMaxBrakingEventVelocity);
            mEventFileWriter.writeGsensorTrackInfo(mGsensorTrackInfoName, m3AxisAccelerationData, mBrakingEventStartTime);
            mBrakingEventRecording = false;
        }
        if (mReverseEventRecording) {
            mSwerveEventEndTime = System.currentTimeMillis() / 1000;
            mEventFileWriter.writeReverseEvent(mReverseEventName, mReverseEventStartTime, mReverseEventEndTime, mMinReverseEventVelocity);
            mEventFileWriter.writeGsensorTrackInfo(mGsensorTrackInfoName, m3AxisAccelerationData, mReverseEventStartTime);
        }
        if (mSwerveEventRecording) {
            mSwerveEventEndTime = System.currentTimeMillis() / 1000;
            mEventFileWriter.writeSwerveEvent(mSwerveEventName, mSwerveEventStartTime, mSwerveEventEndTime, mMaxMinSwerveEventVelocity);
            mEventFileWriter.writeGsensorTrackInfo(mGsensorTrackInfoName, m3AxisAccelerationData, mSwerveEventStartTime);
        }
    }

    public void wrapAllDrivingEvent() {
        if (mGsensorRun) {
            if (mAccelerationEventCount > 0 || mBrakingEventCount > 0 || mReverseEventCount > 0 || mSwerveEventCount > 0) {
                Log.d(TAG, "Acceleration event count | " + String.valueOf(mAccelerationEventCount));
                Log.d(TAG, "Braking event count | " + String.valueOf(mBrakingEventCount));
                Log.d(TAG, "Reverse event count | " + String.valueOf(mReverseEventCount));
                Log.d(TAG, "Swerve event count | " + String.valueOf(mSwerveEventCount));

                collectDataImmediately();
                /*****************************************************************************************************
                *   eventName[0]: Acceleration; eventName[1]: Braking; eventName[2]: Reverse; eventName[3]: Swerve
                *   eventCount[0]: Acceleration; eventCount[1]: Braking; eventCount[2]: Reverse; eventCount[3]: Swerve
                *****************************************************************************************************/
                String[] eventName = new String[] {mAccelerationEventName, mBrakingEventName, mReverseEventName, mSwerveEventName};
                int[] eventCount = new int[] {mAccelerationEventCount, mBrakingEventCount, mReverseEventCount, mSwerveEventCount};
                mEventFileWriter.writeGsensorJournal(mGsensorJournalName, mImeiId, mTripId, mStartTime, mGsensorTrackInfoName, eventName, eventCount);
                long result = mDatabaseManager.insert(mStartTime, mGsensorJournalName, TYPE_GSENSOR);
                Log.d(TAG, "DrivingEvent insert result | " + String.valueOf(result));
                mLogFileWriter.appendLog("DrivingEvent insert result | " + String.valueOf(result));
            } else {
                Log.d(TAG, "No DrivingEvent need to be collected");
                mLogFileWriter.appendLog("No DrivingEvent need to be collected");
            }
        }
    }

    public void wrapAllGpsEvent() {
        if (mGpsRun) {
            boolean bIsGpsFileExist = mEventFileWriter.writeGpsJournal(mGpsJournalName, mGpsTrackInfoName, mImeiId, mTripId, mStartTime);
            if (bIsGpsFileExist) {
                long result = mDatabaseManager.insert(mStartTime, mGpsJournalName, TYPE_GPS);
                Log.d(TAG, "GpsEvent insert result | " + String.valueOf(result));
                mLogFileWriter.appendLog("GpsEvent insert result | " + String.valueOf(result));
            } else {
                Log.d(TAG, "No GpsEvent need to be collected");
                mLogFileWriter.appendLog("No GpsEvent need to be collected");
            }
        }
    }
}

package com.sanjetco.ad10cht.log;

import android.os.Environment;
import android.util.Log;

import com.sanjetco.ad10cht.common.MainCommon;
import com.sanjetco.ad10cht.event.DrivingEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by PaulLee on 2016/5/10.
 * Log file writer
 */
public class LogFileWriter implements MainCommon {

    static final String LOG_PATH_PREFIX = Environment.getExternalStorageDirectory().getPath();
    static final String LOG_PATH = LOG_PATH_PREFIX + "/SJ/Driving/Log";
    static final String LOG_NAME_POSTFIX = "-log.txt";
    static String mLogName = "";

    public void appendLog(String message) {

        mLogName = DrivingEvent.mTripStartDate + LOG_NAME_POSTFIX;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmssZZZ", Locale.TAIWAN);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date currentDate = new Date(System.currentTimeMillis());
        String timeStamp = sdf.format(currentDate);

        String messageToFile = timeStamp + "  " + message;

        try {

            File dir = new File(LOG_PATH);
            if ( !dir.exists() ) {
                dir.mkdirs();
                Log.d(TAG, "Create log directory | " + LOG_PATH);
            }
            File file = new File(LOG_PATH, mLogName);
            FileOutputStream fos = new FileOutputStream(file, true);

            fos.write(messageToFile.getBytes());
            fos.write("\r\n".getBytes());
            fos.close();

        } catch (FileNotFoundException e) {
            Log.d(TAG, "Creating log file error: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Log file I/O error: " + e.getMessage());
        }
    }
}

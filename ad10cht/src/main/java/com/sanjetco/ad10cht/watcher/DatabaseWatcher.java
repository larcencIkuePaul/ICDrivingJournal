package com.sanjetco.ad10cht.watcher;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.sanjetco.ad10cht.common.DatabaseCommon;
import com.sanjetco.ad10cht.common.HttpCommon;
import com.sanjetco.ad10cht.common.MainCommon;
import com.sanjetco.ad10cht.database.DatabaseManager;
import com.sanjetco.ad10cht.httpclient.ChtHttpConnection;
import com.sanjetco.ad10cht.log.LogFileWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by PaulLee on 2016/5/10.
 */
public class DatabaseWatcher extends AsyncTask<Context, Object, Object> implements 
        MainCommon, 
        DatabaseCommon, 
        HttpCommon
{

    static final int RECHECK_DATABASE_DURATION_MSEC = 5000;
    static final int MAX_RECHECK_DATABASE_DURATION_MSEC = 60000;
    static final int MAX_CONNECTION_RETRY_COUNT = 3;

    DatabaseManager mDatabaseManager;
    Context mContext;
    LogFileWriter mLogFileWriter = new LogFileWriter();

    boolean mRunFlag = true;

    int mFileCount = 0;

    Lock mLock = new ReentrantLock();
    Condition mHasTasks = mLock.newCondition();

    List<PendingFilePack> mPendingFileList;

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "Worker thread DatabaseWatcher start");
    }

    @Override
    protected Object doInBackground(Context... contexts) {

        int waitTime = RECHECK_DATABASE_DURATION_MSEC;
        this.mContext = contexts[0];
        mDatabaseManager = new DatabaseManager(mContext);

        while (mRunFlag) {
            if (execDatabase() == 0) {
                execConnection();
                waitTime = RECHECK_DATABASE_DURATION_MSEC;
            } else {
                if (waitTime < MAX_RECHECK_DATABASE_DURATION_MSEC) {
                    waitTime += ( (waitTime / 5000) * 1000 );
                }
            }

            try {
                Log.d(TAG, "Ready to Wait " + waitTime + " msec");
                mLock.lock();
                mHasTasks.await(waitTime, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                mRunFlag = false;
                Log.d(TAG, "mHasTasks interrupted | " + e.getMessage());
            } finally {
                mLock.unlock();
            }
        }
        return null;
    }

    // This function will be called automatically after publishProgress is invoked.
    @Override
    protected void onProgressUpdate(Object... progress) {

    }

    @Override
    protected void onPostExecute(Object result) {
        Log.d(TAG, "Worker thread DatabaseWatcher end");
    }

    protected int execDatabase() {

        int result = -1;

        if ( (mFileCount = mDatabaseManager.getCount()) == 0) {
            Log.d(TAG, "No record in database");
            return result;
        }

        Log.d(TAG, mFileCount + " file(s) in database");
        //if ( !mPendingFileList.isEmpty() )
        //  mPendingFileList.clear();

        mPendingFileList = mDatabaseManager.getAll();
        Log.d(TAG, "Get pending file list done");
        result = 0;

        return result;
    }

    protected int execConnection() {

        int result = 0;
        int count = 0;
        Iterator<PendingFilePack> it = mPendingFileList.iterator();

        while (it.hasNext()) {
            final PendingFilePack pfp = it.next();
            do {
                Log.d(TAG, "name: " + pfp.name);
                Log.d(TAG, "type: " + pfp.type);
                Log.d(TAG, "timestamp: " + pfp.timestamp);
                switch (pfp.type) {
                    case TYPE_GPS:
                        result = new ChtHttpConnection().sendDataToCht(convertJournalToByteArray(GPS_PATH, pfp.name), UBI_CLOUD_URL + GPS_URL, GPS_URL);
                        count++;
                        Log.d(TAG, "type: " + pfp.type + " | result: " + result + " | count: " + count);
                        break;
                    case TYPE_GSENSOR:
                        result = new ChtHttpConnection().sendDataToCht(convertJournalToByteArray(GSENSOR_PATH, pfp.name), UBI_CLOUD_URL + GSENSOR_URL, GSENSOR_URL);
                        count++;
                        Log.d(TAG, "type: " + pfp.type + " | result: " + result + " | count: " + count);
                        break;
                    default:
                        break;
                }
                if (result != 201 && result != 200) {
                    try {
                        mLock.lock();
                        mHasTasks.await(1000, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        Log.d(TAG, "execConnection interrupted | " + e.getMessage());
                    } finally {
                        mLock.unlock();
                    }
                }
            } while ( (result != 201 && result != 200) && count < MAX_CONNECTION_RETRY_COUNT );

            if (result == 201 || result == 200) {
                Log.d(TAG, "Invoke database delete");
                if ( mDatabaseManager.delete(pfp.id) == 1 ) {
                    Log.d(TAG, "Delete " + pfp.name + " from database done");
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            deleteFile(pfp.name, pfp.type);
                        }
                    });
                    thread.start();
                }
                else {
                    Log.d(TAG, "Delete " + pfp.name + " from database failed");
                    mLogFileWriter.appendLog("Delete " + pfp.name + " from database failed");
                }
            } else {
                Log.d(TAG, "Reach max retry count, exit connection & sleep");
                break;
            }
            count = 0;
        }
        return result;
    }

    protected byte[] convertJournalToByteArray(String dirPath, String fileName) {

        byte[] buffer = new byte[1];

        try {
            File ifile = new File(dirPath, fileName);
            FileInputStream fis = new FileInputStream(ifile);

            buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();

        } catch (FileNotFoundException e) {
            Log.d(TAG, e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
        return buffer;
    }

    protected boolean deleteFile(final String fileName, int type) {

        boolean result = false;
        String dirPath = "";
        String[] arrayString = {};
        String filterName = "";

        switch (type) {
            case TYPE_GPS:
                dirPath = GPS_PATH;
                filterName = fileName.substring(0, fileName.length() - "-GPS".length());
                break;
            case TYPE_GSENSOR:
                dirPath = GSENSOR_PATH;
                filterName = fileName.substring(0, fileName.length() - "-GSensor".length());
                break;
        }

        final String filter = filterName;

        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.indexOf(filter) != -1) return true;
                return false;
            }
        };
        File fileList = new File(dirPath);
        if ( fileList.exists() ) {
            arrayString = fileList.list(filenameFilter);
            if (arrayString != null) {
                Log.d(TAG, "Find similar file(s)");
            }
            else {
                Log.d(TAG, "Find no similar file");
            }
        }

        for (int i = 0; i < arrayString.length; i++) {
            File file = new File(dirPath, arrayString[i]);
            if (file.exists()) {
                result = file.delete();
                if (result) {
                    Log.d(TAG, "Delete file " + arrayString[i] + " done");
                } else {
                    Log.d(TAG, "Delete file " + arrayString[i] + " failed");
                }
            } else {
                Log.d(TAG, "File " + arrayString[i] + " doesn't exist");
            }
        }
        return result;
    }
    
}

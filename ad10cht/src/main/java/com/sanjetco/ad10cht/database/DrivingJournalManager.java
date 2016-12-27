package com.sanjetco.ad10cht.database;

import android.content.Context;
import android.util.Log;

import com.intel.ita.snf.doc.JMessage;
import com.intel.ita.snf.doc.Journal;
import com.intel.ita.snf.interfaces.StoreAndForwardServiceConnectionCallback;
import com.intel.ita.snf.lib.StoreAndForwardServiceManager;
import com.sanjetco.ad10cht.common.MainCommon;

/**
 * Created by PaulLee on 2016/5/4.
 */
public class DrivingJournalManager implements
        StoreAndForwardServiceConnectionCallback,
        MainCommon {

    Context mContext;
    StoreAndForwardServiceManager mSAFSManager;
    Journal mApple;


    boolean mIsServiceConnected = false;

    public DrivingJournalManager(Object object) {
        mContext = (Context) object;
        mSAFSManager = new StoreAndForwardServiceManager(mContext);
    }

    public void initManager() {
        mSAFSManager.initServiceConnection(this);
    }

    public void deInitManager() {
        mSAFSManager.deInitServiceConnection();
    }

    public void addNewJournal() {
        JMessage message = new JMessage(
                mApple.getJournalID(),
                System.currentTimeMillis(),
                0,
                "Chiba",
                "",
                0,
                "");
        mSAFSManager.addMessage(mApple.getJournalID(), message);
    }

    public void getMessages() {
        Log.d(TAG, "apple message count: " + mSAFSManager.getMessageCount(mApple.getJournalID(), ""));
    }

    public void getJournals() {
        Log.d(TAG, "apple: " + mSAFSManager.getJournalCount("apple"));
        Log.d(TAG, "bee: " + mSAFSManager.getJournalCount("bee"));
        Log.d(TAG, "cat: " + mSAFSManager.getJournalCount("cat"));
        Log.d(TAG, "dog: " + mSAFSManager.getJournalCount("dog"));
        Log.d(TAG, "egg: " + mSAFSManager.getJournalCount("egg"));
    }

    @Override
    public void onServiceConnectionStateChanged(boolean status) {
        Log.d(TAG, "SAFS status: " + String.valueOf(status));
        if (status) {
            mIsServiceConnected = true;
            mApple = mSAFSManager.createJournal("apple");
            Log.d(TAG, "apple Journal ID: " + mApple.getJournalID());
            Log.d(TAG, "bee Journal ID: " + mSAFSManager.createJournal("bee").getJournalID());
            Log.d(TAG, "cat Journal ID: " + mSAFSManager.createJournal("cat").getJournalID());
            Log.d(TAG, "dog Journal ID: " + mSAFSManager.createJournal("dog").getJournalID());
            Log.d(TAG, "egg Journal ID: " + mSAFSManager.createJournal("egg").getJournalID());
            Log.d(TAG, "Create journals done");
        } else {
            mIsServiceConnected = false;
        }
    }
}

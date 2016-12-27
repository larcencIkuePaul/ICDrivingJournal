package com.sanjetco.ad10cht.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sanjetco.ad10cht.common.DatabaseCommon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PaulLee on 2016/5/10.
 * Database manager
 */
public class DatabaseManager implements DatabaseCommon {

    protected static SQLiteDatabase mSqlDb;

    static final String TABLE_NAME = "pending_file";
    static final String KEY_ID = "_id";
    static final String TIMESTAMP_COLUMN = "timestamp";
    static final String NAME_COLUMN = "name";
    static final String TYPE_COLUMN = "type";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TIMESTAMP_COLUMN + " INTEGER NOT NULL, " +
                    NAME_COLUMN + " TEXT NOT NULL, " +
                    TYPE_COLUMN + " INTEGER NOT NULL)";



    // DatabaseMgr Constructor
    public DatabaseManager(Context context) {
        mSqlDb = DatabaseHelper.getDatabase(context);
    }

    public void close() {
        mSqlDb.close();
    }

    public long insert(long timestamp, String name, int type) {
        ContentValues cv = new ContentValues();
        cv.put(TIMESTAMP_COLUMN, timestamp);
        cv.put(NAME_COLUMN, name);
        cv.put(TYPE_COLUMN, type);
        return mSqlDb.insert(TABLE_NAME, null, cv);
    }

    public int delete(int id) {
        String where = KEY_ID + " = " + id;
        return mSqlDb.delete(TABLE_NAME, where, null);
    }

    public int getCount() {
        int result = 0;
        Cursor cursor = mSqlDb.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        if(cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        cursor.close();
        return result;
    }

    public List<PendingFilePack> getAll() {
        List<PendingFilePack> result = new ArrayList<>();
        Cursor cursor = mSqlDb.query(TABLE_NAME, null, null, null, null, null, TIMESTAMP_COLUMN + " ASC");
        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }
        return result;
    }

    protected PendingFilePack getRecord(Cursor cursor) {
        PendingFilePack pendingFilePack = new PendingFilePack();
        pendingFilePack.id = cursor.getInt(0);
        pendingFilePack.timestamp = cursor.getLong(1);
        pendingFilePack.name = cursor.getString(2);
        pendingFilePack.type = cursor.getInt(3);
        return pendingFilePack;
    }
}

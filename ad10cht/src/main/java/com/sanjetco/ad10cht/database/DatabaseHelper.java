package com.sanjetco.ad10cht.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by PaulLee on 2016/5/10.
 * Database helper
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    protected static final String DATABASE_NAME = "dj.db";
    protected static final int VERSION = 1;

    protected static SQLiteDatabase mSqlDb;

    public DatabaseHelper(Context context, String name,  SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static SQLiteDatabase getDatabase(Context context) {
        if ( mSqlDb == null || !mSqlDb.isOpen() ) {
            mSqlDb = new DatabaseHelper(context, DATABASE_NAME, null, VERSION).getWritableDatabase();
        }
        return mSqlDb;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseManager.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseManager.TABLE_NAME);
        onCreate(db);
    }
}

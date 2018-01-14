package com.lzx.nicemusic.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by xian on 2018/1/14.
 */

public class DbHelper extends SQLiteOpenHelper {
    private static final String name = "nickmusic";
    private static final int version = 7;
    private static volatile DbHelper instance;

    public DbHelper(Context context) {
        super(context, name, null, version);
    }

    private final String TABLE_CACHE = "create table "
            + CacheManager.niceMusicCache + " ( "
            + CacheManager.niceMusicKey + " text not null primary key, "
            + CacheManager.niceMusicValue + " text);";

    private final String TABLE_SEARCH_HISTORY = "create table "
            + SearchManager.searchHistory + " ( "
            + SearchManager.searchTitle + " text);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CACHE);
        db.execSQL(TABLE_SEARCH_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static DbHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DbHelper.class) {
                if (instance == null) {
                    instance = new DbHelper(context.getApplicationContext());
                }
            }
        }
        return instance;
    }
}
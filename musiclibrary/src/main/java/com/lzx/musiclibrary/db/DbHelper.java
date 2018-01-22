package com.lzx.musiclibrary.db;

import android.content.ContentProvider;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by xian on 2018/1/22.
 */

public class DbHelper extends SQLiteOpenHelper implements DbConstants {
    private static final String name = "music_db";
    private static final int version = 7;
    private static volatile DbHelper instance;

    public DbHelper(Context context) {
        super(context, name, null, version);
    }

    private final String CREATE_TABLE_PLAYING_MUSIC_LIST = "create table "
            + TABLE_PLAYING_MUSIC_LIST + " ( "
            + MUSIC_ID + " text, "
            + MUSIC_TITLE + " text, "
            + ARTIST + " text);";

    private final String CREATE_TABLE_FAVORITE_MUSIC = "create table "
            + TABLE_FAVORITE_MUSIC + " ( "
            + MUSIC_ID + " text, "
            + MUSIC_TITLE + " text, "
            + ALBUM_TITLE + " text, "
            + ARTIST + " text);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PLAYING_MUSIC_LIST);
        db.execSQL(CREATE_TABLE_FAVORITE_MUSIC);
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
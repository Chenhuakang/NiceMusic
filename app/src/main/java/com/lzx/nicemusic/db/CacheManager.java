package com.lzx.nicemusic.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lzx.nicemusic.NiceMusicApplication;

import java.util.ArrayList;

/**
 * Created by xian on 2018/1/14.
 */

public class CacheManager {
    public static final String niceMusicCache = "nicemusic_cache";
    public static final String niceMusicKey = "nicemusic_key";
    public static final String niceMusicValue = "nicemusic_value";
    public static final String KEY_HOME_LIST_DATA = "key_home_list_data";

    private DbHelper helper;

    public CacheManager(Context context) {
        helper = DbHelper.getInstance(context);
    }

    private final static class HolderClass {
        private final static CacheManager INSTANCE = new CacheManager(NiceMusicApplication.getContext());
    }

    public static CacheManager getImpl() {
        return HolderClass.INSTANCE;
    }

    public void saveCache(String key, String value) {
        if (hasCache(key)) {
            updateCache(key, value);
        } else {
            insertCache(key, value);
        }
    }

    public void insertCache(String key, String value) {
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(niceMusicKey, key);
            contentValues.put(niceMusicValue, value);
            db.insert(niceMusicCache, null, contentValues);
        }
    }

    public void updateCache(String key, String value) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(niceMusicValue, value);
            db.update(niceMusicCache, contentValues, niceMusicKey + " = ?", new String[]{key});
        }
    }

    public boolean hasCache(String key) {
        SQLiteDatabase db = helper.getReadableDatabase();
        boolean result = false;
        if (db.isOpen()) {
            try {
                Cursor cursor = db.query(niceMusicCache, null,
                        niceMusicKey + " = ?", new String[]{key},
                        null, null, null, null);
                if (cursor.moveToNext()) {
                    result = true;
                }
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
                result = false;
            }
        }
        return result;
    }

    public String findCache(String key) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String result = "{}";
        if (db.isOpen()) {
            try {
                Cursor cursor = db.query(niceMusicCache, null,
                        niceMusicKey + " = ?", new String[]{key},
                        null, null, null, null);
                while (cursor.moveToNext()) {
                    result = cursor.getString(cursor.getColumnIndex(niceMusicValue));
                }
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
                result = "{}";
            }
        }
        return result;
    }

}

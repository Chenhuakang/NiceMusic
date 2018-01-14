package com.lzx.nicemusic.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lzx.nicemusic.NiceMusicApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xian on 2018/1/14.
 */

public class SearchManager {

    public static final String searchHistory = "searchHistory";
    public static final String searchTitle = "searchHistory";


    private DbHelper helper;

    public SearchManager(Context context) {
        helper = DbHelper.getInstance(context);
    }

    private final static class HolderClass {
        private final static SearchManager INSTANCE = new SearchManager(NiceMusicApplication.getContext());
    }

    public static SearchManager getImpl() {
        return SearchManager.HolderClass.INSTANCE;
    }

    public boolean hasHistory(String title) {
        SQLiteDatabase db = helper.getReadableDatabase();
        boolean result = false;
        if (db.isOpen()) {
            try {
                Cursor cursor = db.query(searchHistory, null,
                        searchTitle + " = ?", new String[]{title},
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

    public void saveHistory(String title) {
        if (!hasHistory(title)) {
            SQLiteDatabase db = helper.getReadableDatabase();
            if (db.isOpen()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(searchTitle, title);
                db.insert(searchHistory, null, contentValues);
            }
        }
    }

    public void deleteHistory(String title) {
        if (hasHistory(title)) {
            SQLiteDatabase db = helper.getReadableDatabase();
            if (db.isOpen()) {
                db.delete(searchHistory, searchTitle + " = ?", new String[]{title});
            }
        }
    }

    public List<String> findHistorys() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            try {
                Cursor cursor = db.query(searchHistory, null,
                        null, null,
                        null, null, null, null);
                while (cursor.moveToNext()) {
                    String history = cursor.getString(cursor.getColumnIndex(searchTitle));
                    list.add(history);
                }
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}

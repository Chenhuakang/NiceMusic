package com.lzx.musiclibrary.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.lzx.musiclibrary.db.DbConstants;
import com.lzx.musiclibrary.db.MusicContentProvider;

/**
 * Created by xian on 2018/1/22.
 */

public class ProviderManager implements DbConstants {
    private Context mContext;

    public ProviderManager(Context context) {
        mContext = context;
    }

    public void query() {
        Uri uri = MusicContentProvider.PLAYING_MUSIC_LIST_URI;
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        while (cursor.moveToNext()){

        }
        cursor.close();
    }

    public void insert() {
        Uri uri = MusicContentProvider.PLAYING_MUSIC_LIST_URI;
        ContentValues values = new ContentValues();
        values.put(MUSIC_ID, "");
        mContext.getContentResolver().insert(uri, values);

    }
}

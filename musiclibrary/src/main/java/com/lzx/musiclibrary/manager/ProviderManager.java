package com.lzx.musiclibrary.manager;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.lzx.musiclibrary.aidl.model.MusicInfo;
import com.lzx.musiclibrary.db.DbConstants;
import com.lzx.musiclibrary.db.MusicContentProvider;
import com.lzx.musiclibrary.playback.QueueManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xian on 2018/1/22.
 */

public class ProviderManager implements DbConstants {
    private Context mContext;
    private ContentResolver mResolver;
    private QueueManager mQueueManager;

    public ProviderManager(Context context, QueueManager queueManager) {
        mContext = context;
        mResolver = mContext.getContentResolver();
        mQueueManager = queueManager;
    }

    public List<MusicInfo> queryPlayList() {
        Uri uri = MusicContentProvider.PLAYING_MUSIC_LIST_URI;
        Cursor cursor = mResolver.query(uri, null, null, null, null);
        if (cursor == null) {
            return new ArrayList<>();
        }
        List<MusicInfo> musicInfos = new ArrayList<>();
        while (cursor.moveToNext()) {
            String musicId = cursor.getString(cursor.getColumnIndex(MUSIC_ID));
            String artist = cursor.getString(cursor.getColumnIndex(ARTIST));
            String musicTitle = cursor.getString(cursor.getColumnIndex(MUSIC_TITLE));
            MusicInfo info = new MusicInfo();
            info.musicId = musicId;
            info.musicArtist = artist;
            info.musicTitle = musicTitle;
            musicInfos.add(info);
        }
        cursor.close();
        return musicInfos;
    }

    public List<MusicInfo> queryFavoriteList() {
        Uri uri = MusicContentProvider.FAVORITE_MUSIC_LIST_URI;
        Cursor cursor = mResolver.query(uri, null, null, null, null);
        if (cursor == null) {
            return new ArrayList<>();
        }
        List<MusicInfo> musicInfos = new ArrayList<>();
        while (cursor.moveToNext()) {
            String musicId = cursor.getString(cursor.getColumnIndex(MUSIC_ID));
            String artist = cursor.getString(cursor.getColumnIndex(ARTIST));
            String musicTitle = cursor.getString(cursor.getColumnIndex(MUSIC_TITLE));
            String albumTitle = cursor.getString(cursor.getColumnIndex(ALBUM_TITLE));
            MusicInfo info = new MusicInfo();
            info.musicId = musicId;
            info.musicArtist = artist;
            info.musicTitle = musicTitle;
            info.albumTitle = albumTitle;
            musicInfos.add(info);
        }
        cursor.close();
        return musicInfos;
    }

    public void insert() {
        Uri uri = MusicContentProvider.PLAYING_MUSIC_LIST_URI;
        ContentValues values = new ContentValues();
        values.put(MUSIC_ID, "");
        mContext.getContentResolver().insert(uri, values);

    }
}

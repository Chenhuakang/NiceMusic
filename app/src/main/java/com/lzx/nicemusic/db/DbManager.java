package com.lzx.nicemusic.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.lzx.musiclibrary.aidl.model.MusicInfo;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author lzx
 * @date 2018/2/6
 */

public class DbManager {

    private Context mContext;
    private ContentResolver mResolver;


    public DbManager(Context context) {
        mContext = context;
        mResolver = mContext.getContentResolver();
    }

    /**
     * 获取播放列表
     *
     * @return
     */
    public List<MusicInfo> queryPlayList() {
        Uri uri = MusicContentProvider.SONG_LIST_URI;
        Cursor cursor = mResolver.query(uri, null, null, null, null);
        if (cursor == null) {
            return new ArrayList<>();
        }
        List<MusicInfo> musicInfos = new ArrayList<>();
        while (cursor.moveToNext()) {
            String musicId = cursor.getString(cursor.getColumnIndex(DbConstants.MUSIC_ID));
            String artist = cursor.getString(cursor.getColumnIndex(DbConstants.ARTIST));
            String musicTitle = cursor.getString(cursor.getColumnIndex(DbConstants.MUSIC_TITLE));
            String albumTitle = cursor.getString(cursor.getColumnIndex(DbConstants.ALBUM_TITLE));
            long duration = cursor.getLong(cursor.getColumnIndex(DbConstants.DURATION));
            MusicInfo info = new MusicInfo();
            info.musicId = musicId;
            info.musicArtist = artist;
            info.musicTitle = musicTitle;
            info.albumTitle = albumTitle;
            info.musicDuration = duration;
            musicInfos.add(info);
        }
        cursor.close();
        return musicInfos;
    }

    /**
     * 保存播放列表
     *
     * @param list
     */
    public void savePlayList(List<MusicInfo> list) {
        clearPlayList();
        Uri uri = MusicContentProvider.SONG_LIST_URI;
        for (MusicInfo info : list) {
            ContentValues values = new ContentValues();
            values.put(DbConstants.MUSIC_ID, info.musicId);
            values.put(DbConstants.ARTIST, info.musicArtist);
            values.put(DbConstants.MUSIC_TITLE, info.musicTitle);
            values.put(DbConstants.ALBUM_TITLE, info.albumTitle);
            values.put(DbConstants.DURATION, info.musicDuration);
            mResolver.insert(uri, values);
        }
    }

    /**
     * 删除一条播放列表
     *
     * @return The number of rows deleted.
     */
    public int deleteInfoInPlayList(MusicInfo info) {
        Uri uri = MusicContentProvider.SONG_LIST_URI;
        return mResolver.delete(uri, DbConstants.MUSIC_ID + " = ?", new String[]{info.musicId});
    }

    /**
     * 清空播放列表
     *
     * @return The number of rows deleted.
     */
    public int clearPlayList() {
        Uri uri = MusicContentProvider.SONG_LIST_URI;
        return mResolver.delete(uri, null, null);
    }

    /**
     * 获取我的歌单
     *
     * @return
     */
    public List<MusicInfo> queryFavoriteList() {
        Uri uri = MusicContentProvider.FAVORITES_URI;
        Cursor cursor = mResolver.query(uri, null, null, null, null);
        if (cursor == null) {
            return new ArrayList<>();
        }
        List<MusicInfo> musicInfos = new ArrayList<>();
        while (cursor.moveToNext()) {
            String musicId = cursor.getString(cursor.getColumnIndex(DbConstants.MUSIC_ID));
            String artist = cursor.getString(cursor.getColumnIndex(DbConstants.ARTIST));
            String musicTitle = cursor.getString(cursor.getColumnIndex(DbConstants.MUSIC_TITLE));
            String albumTitle = cursor.getString(cursor.getColumnIndex(DbConstants.ALBUM_TITLE));
            long duration = cursor.getLong(cursor.getColumnIndex(DbConstants.DURATION));
            MusicInfo info = new MusicInfo();
            info.musicId = musicId;
            info.musicArtist = artist;
            info.musicTitle = musicTitle;
            info.albumTitle = albumTitle;
            info.musicDuration = duration;
            musicInfos.add(info);
        }
        cursor.close();
        return musicInfos;
    }

    /**
     * 删除一条我的歌单
     *
     * @param info
     */
    public int deleteInfoInFavoriteList(MusicInfo info) {
        Uri uri = MusicContentProvider.FAVORITES_URI;
        return mResolver.delete(uri, DbConstants.MUSIC_ID + " = ?", new String[]{info.musicId});
    }

    /**
     * 添加一条我的歌单
     *
     * @param info
     */
    public void addInfoInFavoriteList(MusicInfo info) {
        Uri uri = MusicContentProvider.FAVORITES_URI;
        ContentValues values = new ContentValues();
        values.put(DbConstants.MUSIC_ID, info.musicId);
        values.put(DbConstants.ARTIST, info.musicArtist);
        values.put(DbConstants.MUSIC_TITLE, info.musicTitle);
        values.put(DbConstants.ALBUM_TITLE, info.albumTitle);
        values.put(DbConstants.DURATION, info.musicDuration);
        mResolver.insert(uri, values);
    }

    /**
     * 清空我的歌单
     */
    public int clearFavoriteList() {
        Uri uri = MusicContentProvider.FAVORITES_URI;
        return mResolver.delete(uri, null, null);
    }

    /**
     * 下面是异步方法
     */

    public Observable<List<MusicInfo>> AsyQueryPlayList() {
        return Observable.create((ObservableOnSubscribe<List<MusicInfo>>) emitter -> {
            List<MusicInfo> list = queryPlayList();
            emitter.onNext(list);
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Boolean> AsySavePlayList(List<MusicInfo> list) {
        return Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            savePlayList(list);
            emitter.onNext(true);
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Boolean> AsyDeleteInfoInPlayList(MusicInfo info) {
        return Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            int rows = deleteInfoInPlayList(info);
            emitter.onNext(rows > 0);
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<MusicInfo>> AsyQueryFavoriteList() {
        return Observable.create((ObservableOnSubscribe<List<MusicInfo>>) emitter -> {
            List<MusicInfo> list = queryFavoriteList();
            emitter.onNext(list);
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Boolean> AsyDeleteInfoInFavoriteList(MusicInfo info) {
        return Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            int rows = deleteInfoInFavoriteList(info);
            emitter.onNext(rows > 0);
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Boolean> AsyAddInfoInFavoriteList(MusicInfo info) {
        return Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            addInfoInFavoriteList(info);
            emitter.onNext(true);
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Boolean> AsyClearFavoriteList() {
        return Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            int rows = clearFavoriteList();
            emitter.onNext(rows > 0);
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

}

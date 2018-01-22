package com.lzx.musiclibrary.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by xian on 2018/1/22.
 */

public class MusicContentProvider extends ContentProvider implements DbConstants {

    public static final String AUTHORITY = "com.lzx.musiclibrary.db.provider";
    public static final Uri PLAYING_MUSIC_LIST_URI = Uri.parse("content://" + AUTHORITY + "/playing_music_list");
    public static final Uri FAVORITE_MUSIC_LIST_URI = Uri.parse("content://" + AUTHORITY + "/favorite_music_list");
    public static final int PLAYING_MUSIC_LIST_CODE = 0;
    public static final int FAVORITE_MUSIC_LIST_CODE = 1;

    private DbHelper mDbHelper;
    private SQLiteDatabase mDb;
    private UriMatcher mUriMatcher;
    private Context mContext;

    @Override
    public boolean onCreate() {
        mContext = getContext();
        mDbHelper = new DbHelper(mContext);
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(AUTHORITY, "playing_music_list", PLAYING_MUSIC_LIST_CODE);
        mUriMatcher.addURI(AUTHORITY, "favorite_music_list", FAVORITE_MUSIC_LIST_CODE);
        mDb = mDbHelper.getWritableDatabase();
        return true;
    }

    private String getTableName(Uri uri) {
        String tableName = null;
        switch (mUriMatcher.match(uri)) {
            case PLAYING_MUSIC_LIST_CODE:
                tableName = TABLE_PLAYING_MUSIC_LIST;
                break;
            case FAVORITE_MUSIC_LIST_CODE:
                tableName = TABLE_FAVORITE_MUSIC;
                break;
            default:
                break;
        }
        return tableName;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        String tableName = getTableName(uri);
        return mDb.query(tableName, projection, selection, selectionArgs, null, null, sortOrder, null);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        String tableName = getTableName(uri);
        mDb.insert(tableName, null, contentValues);
        mContext.getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String tableName = getTableName(uri);
        int count = mDb.delete(tableName, selection, selectionArgs);
        if (count > 0) {
            mContext.getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        String tableName = getTableName(uri);
        int row = mDb.update(tableName, contentValues, selection, selectionArgs);
        if (row > 0) {
            mContext.getContentResolver().notifyChange(uri, null);
        }
        return row;
    }
}

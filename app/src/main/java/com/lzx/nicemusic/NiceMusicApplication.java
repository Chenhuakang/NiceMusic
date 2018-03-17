package com.lzx.nicemusic;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.lzx.musiclibrary.manager.MusicManager;
import com.lzx.musiclibrary.notification.NotificationCreater;
import com.lzx.musiclibrary.utils.BaseUtil;
import com.lzx.nicemusic.receiver.MyPlayerReceiver;
import com.lzx.nicemusic.utils.CrashHandler;
import com.lzx.nicemusic.utils.SpUtil;

/**
 * Created by xian on 2018/1/13.
 */

public class NiceMusicApplication extends Application {

    private static Context sContext;

    public static final String closeActionName = "com.lzx.nicemusic.android.Action_CLOSE";
    public static final String favoriteActionName = "com.lzx.nicemusic.android.Action_FAVORITE";
    public static final String lyricsActionName = "com.lzx.nicemusic.android.Action_Lyrics";
    public static boolean isFavorite = false;
    public static boolean isChecked = false;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        SpUtil.getInstance().init(this);
        CrashHandler.getInstance().init(this);

        if (!BaseUtil.getCurProcessName(this).contains(":musicLibrary")) {
            NotificationCreater creater = new NotificationCreater.Builder()
                    .setTargetClass("com.lzx.nicemusic.module.main.HomeActivity")
                    .setFavoriteIntent(getPendingIntent(favoriteActionName))
                    .setLyricsIntent(getPendingIntent(lyricsActionName))
                    .setCreateSystemNotification(true)
                    .build();
            MusicManager.get()
                    .setContext(this)
                    .setNotificationCreater(creater)
                    .init();
        }
    }

    private PendingIntent getPendingIntent(String action) {
        Intent intent = new Intent(action);
        intent.setClass(this, MyPlayerReceiver.class);
        return PendingIntent.getBroadcast(this, 0, intent, 0);
    }

    public static Context getContext() {
        return sContext;
    }


}

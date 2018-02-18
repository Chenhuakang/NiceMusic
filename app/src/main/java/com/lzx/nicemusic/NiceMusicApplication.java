package com.lzx.nicemusic;

import android.app.Application;
import android.app.Notification;
import android.content.Context;

import com.lzx.musiclibrary.manager.MusicManager;
import com.lzx.musiclibrary.notification.NotificationCreater;
import com.lzx.nicemusic.module.main.HomeActivity;
import com.lzx.nicemusic.utils.CrashHandler;
import com.lzx.nicemusic.utils.SpUtil;

/**
 * Created by xian on 2018/1/13.
 */

public class NiceMusicApplication extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        SpUtil.getInstance().init(this);
        CrashHandler.getInstance().init(this);


        MusicManager.get()
                .setContext(this)
                .setCreateNotification(true)
                .bindService();


    }

    public static Context getContext() {
        return sContext;
    }
}

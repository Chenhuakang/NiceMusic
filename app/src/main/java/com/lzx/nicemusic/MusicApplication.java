package com.lzx.nicemusic;

import android.app.Application;

import com.lzx.starrysky.manager.MusicManager;
import com.lzx.starrysky.notification.NotificationConstructor;

public class MusicApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MusicManager.initMusicManager(this);
        //配置通知栏
        NotificationConstructor constructor = new NotificationConstructor.Builder()
                .setCreateSystemNotification(false)
                .bulid();
        MusicManager.getInstance().setNotificationConstructor(constructor);
    }
}

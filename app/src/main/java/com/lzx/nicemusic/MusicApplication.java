package com.lzx.nicemusic;

import android.app.Application;

import com.lzx.starrysky.manager.MusicManager;
import com.lzx.starrysky.notification.NotificationConstructor;

public class MusicApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MusicManager.initMusicManager(this);
        NotificationConstructor constructor = new NotificationConstructor.Builder()
                .bulid();
        MusicManager.getInstance().setNotificationConstructor(constructor);
    }
}

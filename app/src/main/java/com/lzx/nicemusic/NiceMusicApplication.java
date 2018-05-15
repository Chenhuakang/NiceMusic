package com.lzx.nicemusic;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;

import com.danikula.videocache.ProxyCacheUtils;
import com.lzx.musiclibrary.cache.CacheConfig;
import com.lzx.musiclibrary.cache.CacheUtils;
import com.lzx.musiclibrary.manager.MusicLibrary;
import com.lzx.musiclibrary.manager.MusicManager;
import com.lzx.musiclibrary.notification.NotificationCreater;
import com.lzx.musiclibrary.utils.BaseUtil;
import com.lzx.nicemusic.receiver.MyPlayerReceiver;
import com.lzx.nicemusic.utils.CrashHandler;
import com.lzx.nicemusic.utils.SpUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;

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
            //直播链接在喜马拉雅获取，需要集成他们的sdk
            CommonRequest mXimalaya = CommonRequest.getInstanse();
            String mAppSecret = "8646d66d6abe2efd14f2891f9fd1c8af";
            mXimalaya.setAppkey("9f9ef8f10bebeaa83e71e62f935bede8");
            mXimalaya.setPackid("com.app.test.android");
            mXimalaya.init(this, mAppSecret);

            //通知栏配置
            NotificationCreater creater = new NotificationCreater.Builder()
                    .setTargetClass("com.lzx.nicemusic.module.main.HomeActivity")
                    .build();

            //边播边存配置
            CacheConfig cacheConfig = new CacheConfig.Builder()
                    .setOpenCacheWhenPlaying(true)
                    .setCachePath(CacheUtils.getStorageDirectoryPath() + "/NiceMusic/Cache/")
                    .build();

//            MusicManager.get()
//                    .setContext(this)
//                    .setNotificationCreater(creater)
//                    .setCacheConfig(cacheConfig)
//                    .init();
            MusicLibrary musicLibrary = new MusicLibrary.Builder(this)
                    .setNotificationCreater(creater)
                    .setCacheConfig(cacheConfig)
                    .build();
            musicLibrary.init();
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

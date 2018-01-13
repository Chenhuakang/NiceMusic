package com.lzx.nicemusic;

import android.app.Application;
import android.content.Context;

/**
 * Created by xian on 2018/1/13.
 */

public class NiceMusicApplication extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
    }

    public static Context getContext() {
        return sContext;
    }
}

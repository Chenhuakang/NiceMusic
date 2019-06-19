package com.lzx.nicemusic

import android.app.Application
import com.lzx.nicemusic.imageloader.GlideLoader

import com.lzx.starrysky.manager.MusicManager
import com.lzx.starrysky.notification.NotificationConstructor

class MusicApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MusicManager.initMusicManager(this)
        //设置图片加载器
        MusicManager.setImageLoader(GlideLoader())
        //配置通知栏
        val constructor = NotificationConstructor.Builder()
            .setCreateSystemNotification(false)
            .bulid()
        MusicManager.getInstance().setNotificationConstructor(constructor)
    }
}

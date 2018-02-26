package com.lzx.nicemusic.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.lzx.musiclibrary.manager.MusicManager;
import com.lzx.nicemusic.NiceMusicApplication;

/**
 * @author lzx
 * @date 2018/2/23
 */

public class MyPlayerReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        switch (action) {
            case NiceMusicApplication.closeActionName:
                //自定义实现关闭按钮功能
                Toast.makeText(context, "开始定时", Toast.LENGTH_SHORT).show();
                MusicManager.get().pausePlayInMillis(10 * 1000);
                break;
            case NiceMusicApplication.favoriteActionName:
                //模拟收藏功能
                if (NiceMusicApplication.isFavorite) {
                    NiceMusicApplication.isFavorite = false;
                    Toast.makeText(context, "取消收藏", Toast.LENGTH_SHORT).show();
                } else {
                    NiceMusicApplication.isFavorite = true;
                    Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show();
                }
                //通知更新UI
                MusicManager.get().updateNotificationFavorite(NiceMusicApplication.isFavorite);
                break;
            case NiceMusicApplication.lyricsActionName:
                //模拟歌词显示功能
                if (NiceMusicApplication.isChecked) {
                    NiceMusicApplication.isChecked = false;
                    Toast.makeText(context, "隐藏桌面歌词", Toast.LENGTH_SHORT).show();
                } else {
                    NiceMusicApplication.isChecked = true;
                    Toast.makeText(context, "显示桌面歌词", Toast.LENGTH_SHORT).show();
                }
                //通知更新UI
                MusicManager.get().updateNotificationLyrics(NiceMusicApplication.isChecked);
                break;
            default:
                break;
        }
    }
}

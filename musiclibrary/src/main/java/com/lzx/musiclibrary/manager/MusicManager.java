package com.lzx.musiclibrary.manager;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.lzx.musiclibrary.MusicConstants;
import com.lzx.musiclibrary.MusicService;
import com.lzx.musiclibrary.OnPlayerEventListener;
import com.lzx.musiclibrary.bean.MusicInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @author lzx
 * @date 2018/1/22
 */

public class MusicManager {

    private Context mContext;
    private boolean isUseMediaPlayer = false;
    private Messenger mMessenger;
    private Messenger mGetReplyMessenger = new Messenger(new MessengerHandler());
    private static List<OnPlayerEventListener> mPlayerEventListeners = new ArrayList<>();

    public static MusicManager get() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        @SuppressLint("StaticFieldLeak")
        private static final MusicManager sInstance = new MusicManager();
    }

    public void init(Context context) {
        init(context, false);
    }

    public void init(Context context, boolean isUseMediaPlayer) {
        mContext = context;
        this.isUseMediaPlayer = isUseMediaPlayer;
        Intent intent = new Intent(mContext, MusicService.class);
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mMessenger = new Messenger(iBinder);
            Message message = Message.obtain(null, MusicConstants.MSG_INIT);
            Bundle bundle = new Bundle();
            bundle.putBoolean(MusicConstants.KEY_IS_USE_MEDIAPLAYER, isUseMediaPlayer);
            message.setData(bundle);
            sendMessage(message);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    public void unbindService() {
        mContext.unbindService(mServiceConnection);
    }

    private MusicManager() {

    }

    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MusicConstants.MSG_MUSIC_START:
                    for (OnPlayerEventListener listener : mPlayerEventListeners) {
                        listener.onPlayerStart();
                    }
                    break;
                case MusicConstants.MSG_MUSIC_PAUSE:
                    for (OnPlayerEventListener listener : mPlayerEventListeners) {
                        listener.onPlayerPause();
                    }
                    break;
                case MusicConstants.MSG_MUSIC_STOP:
                    for (OnPlayerEventListener listener : mPlayerEventListeners) {
                        listener.onPlayCompletion();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * 添加监听
     *
     * @param onPlayerEventListener
     */
    public void addOnPlayerEventListener(OnPlayerEventListener onPlayerEventListener) {
        if (onPlayerEventListener != null) {
            mPlayerEventListeners.add(onPlayerEventListener);
        }
    }

    /**
     * 移除监听
     *
     * @param onPlayerEventListener
     */
    public void removePlayerEventListener(OnPlayerEventListener onPlayerEventListener) {
        if (onPlayerEventListener != null) {
            if (mPlayerEventListeners.contains(onPlayerEventListener)) {
                Iterator<OnPlayerEventListener> iterator = mPlayerEventListeners.iterator();
                while (iterator.hasNext()) {
                    OnPlayerEventListener listener = iterator.next();
                    if (listener == onPlayerEventListener) {
                        iterator.remove();
                        break;
                    }
                }
            }
        }
    }

    /**
     * 清除监听
     */
    public void clearPlayerEventListener() {
        Iterator<OnPlayerEventListener> iterator = mPlayerEventListeners.iterator();
        while (iterator.hasNext()) {
            iterator.remove();
        }
    }

    /**
     * 播放/暂停/切歌
     * 会在音乐列表最后添加一个音乐信息并且播放
     *
     * @param musicInfo 音乐信息
     */
    public void playMusic(MusicInfo musicInfo) {
        if (mMessenger != null) {
            Message message = Message.obtain(null, MusicConstants.MSG_PLAY_BY_MUSIC_INFO);
            Bundle bundle = new Bundle();
            bundle.putParcelable(MusicConstants.KEY_MUSIC_INFO, musicInfo);
            message.setData(bundle);
            message.replyTo = mGetReplyMessenger;
            sendMessage(message);
        }
    }

    /**
     * 发送消息
     */
    private void sendMessage(Message message) {
        try {
            mMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}

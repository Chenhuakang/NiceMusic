package com.lzx.musiclibrary.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.lzx.musiclibrary.MusicConstants;
import com.lzx.musiclibrary.MusicService;
import com.lzx.musiclibrary.bean.MusicInfo;


/**
 * @author lzx
 * @date 2018/1/22
 */

public class MusicManager {

    private Context mContext;
    private MusicService mMusicService;
    private boolean isUseMediaPlayer = false;
    private Messenger mMessenger;

    public static MusicManager get() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
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

    public void playMusic(MusicInfo musicInfo) {
        if (mMessenger != null) {
            Message message = Message.obtain(null, MusicConstants.MSG_PLAY_BY_MUSIC_INFO);
            Bundle bundle = new Bundle();
            bundle.putParcelable(MusicConstants.KEY_MUSIC_INFO, musicInfo);
            message.setData(bundle);
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

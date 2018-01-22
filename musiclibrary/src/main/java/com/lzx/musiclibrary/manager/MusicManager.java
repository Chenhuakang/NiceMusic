package com.lzx.musiclibrary.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.lzx.musiclibrary.MusicService;
import com.lzx.musiclibrary.bean.MusicInfo;
import com.lzx.nicemusic.utils.LogUtil;

/**
 * @author lzx
 * @date 2018/1/22
 */

public class MusicManager {

    private Context mContext;
    private MusicService mMusicService;
    private Messenger mMessenger;

    public static MusicManager get() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static final MusicManager sInstance = new MusicManager();
    }

    public void init(Context context) {
        mContext = context;

        Intent intent = new Intent(mContext, MusicService.class);
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mMessenger = new Messenger(iBinder);
            LogUtil.i("服务链接成功...");
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

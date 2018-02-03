package com.lzx.musiclibrary.manager;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.lzx.musiclibrary.MusicService;
import com.lzx.musiclibrary.aidl.listener.IOnPlayerEventListener;
import com.lzx.musiclibrary.aidl.listener.IPlayControl;
import com.lzx.musiclibrary.aidl.listener.OnPlayerEventListener;
import com.lzx.musiclibrary.aidl.model.MusicInfo;

import java.util.List;


/**
 * @author lzx
 * @date 2018/1/22
 */

public class MusicManager implements IPlayControl {

    private Context mContext;
    private boolean isUseMediaPlayer;
    private IPlayControl control;

    public static MusicManager get() {
        return SingletonHolder.sInstance;
    }


    private static class SingletonHolder {
        @SuppressLint("StaticFieldLeak")
        private static final MusicManager sInstance = new MusicManager();
    }

    public MusicManager setContext(Context context) {
        mContext = context;
        return this;
    }

    public MusicManager setUseMediaPlayer(boolean isUseMediaPlayer) {
        this.isUseMediaPlayer = isUseMediaPlayer;
        return this;
    }

    public void build() {
        Intent intent = new Intent(mContext, MusicService.class);
        intent.putExtra("isUseMediaPlayer", isUseMediaPlayer);
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            control = IPlayControl.Stub.asInterface(iBinder);
            try {
                control.registerPlayerEventListener(mOnPlayerEventListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    public void unbindService() {
        mContext.unbindService(mServiceConnection);
    }

    private OnPlayerEventListener mOnPlayerEventListener = new OnPlayerEventListener() {
        @Override
        public void onMusicChange(MusicInfo music) {

        }

        @Override
        public void onPlayerStart() {

        }

        @Override
        public void onPlayerPause() {

        }

        @Override
        public void onPlayerStop() {

        }

        @Override
        public void onPlayCompletion() {

        }

        @Override
        public void onError(String errorMsg) {

        }

        @Override
        public void onBuffering(boolean isFinishBuffer) throws RemoteException {

        }
    };

    private MusicManager() {

    }

    @Override
    public void playMusic(List<MusicInfo> list, int index) {
        if (control != null) {
            try {
                control.playMusic(list, index);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void playMusicByInfo(MusicInfo info) throws RemoteException {

    }

    @Override
    public void playMusicByIndex(int index) throws RemoteException {

    }

    @Override
    public void playMusicAutoStopWhen(List<MusicInfo> list, int index, int time) throws RemoteException {

    }

    @Override
    public void playMusicByInfoAutoStopWhen(MusicInfo info, int time) throws RemoteException {

    }

    @Override
    public void playMusicByIndexAutoStopWhen(int index, int time) throws RemoteException {

    }

    @Override
    public void setAutoStopTime(int time) throws RemoteException {

    }

    @Override
    public int getCurrPlayingIndex() throws RemoteException {
        return 0;
    }

    @Override
    public void pauseMusic() throws RemoteException {

    }

    @Override
    public void resumeMusic() throws RemoteException {

    }

    @Override
    public void stopMusic() throws RemoteException {

    }

    @Override
    public void setPlayList(List<MusicInfo> list) throws RemoteException {

    }

    @Override
    public void setPlayListWithIndex(List<MusicInfo> list, int index) throws RemoteException {

    }

    @Override
    public List<MusicInfo> getPlayList() throws RemoteException {
        return null;
    }

    @Override
    public int getStatus() throws RemoteException {
        return 0;
    }

    @Override
    public void playNext() throws RemoteException {

    }

    @Override
    public void playPre() throws RemoteException {

    }

    @Override
    public boolean hasPre() throws RemoteException {
        return false;
    }

    @Override
    public boolean hasNext() throws RemoteException {
        return false;
    }

    @Override
    public MusicInfo getPreMusic() throws RemoteException {
        return null;
    }

    @Override
    public MusicInfo getNextMusic() throws RemoteException {
        return null;
    }

    @Override
    public MusicInfo getCurrPlayingMusic() throws RemoteException {
        return null;
    }

    @Override
    public void setCurrMusic(int index) throws RemoteException {

    }

    @Override
    public void setPlayMode(int mode) throws RemoteException {

    }

    @Override
    public int getPlayMode() throws RemoteException {
        return 0;
    }

    @Override
    public long getProgress() throws RemoteException {
        return 0;
    }

    @Override
    public void seekTo(int position) throws RemoteException {

    }

    @Override
    public void reset() throws RemoteException {

    }

    @Override
    public void registerPlayerEventListener(IOnPlayerEventListener listener) throws RemoteException {

    }

    @Override
    public void unregisterPlayerEventListener(IOnPlayerEventListener listener) throws RemoteException {

    }

    @Override
    public IBinder asBinder() {
        return null;
    }

}

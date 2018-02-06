package com.lzx.musiclibrary.manager;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

import com.lzx.musiclibrary.MusicService;
import com.lzx.musiclibrary.aidl.listener.IOnPlayerEventListener;
import com.lzx.musiclibrary.aidl.listener.IPlayControl;
import com.lzx.musiclibrary.aidl.listener.OnPlayerEventListener;
import com.lzx.musiclibrary.aidl.model.MusicInfo;
import com.lzx.musiclibrary.playback.State;
import com.lzx.musiclibrary.utils.LogUtil;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * @author lzx
 * @date 2018/1/22
 */

public class MusicManager implements IPlayControl {

    private static final int MSG_MUSIC_CHANGE = 0;
    private static final int MSG_PLAYER_START = 1;
    private static final int MSG_PLAYER_PAUSE = 2;

    private static final int MSG_PLAY_COMPLETION = 4;
    private static final int MSG_PLAYER_ERROR = 5;
    private static final int MSG_BUFFERING = 6;

    private Context mContext;
    private boolean isUseMediaPlayer;
    private boolean isAutoPlayNext = true;
    private IPlayControl control;
    private ClientHandler mClientHandler;
    private CopyOnWriteArrayList<OnPlayerEventListener> mPlayerEventListeners = new CopyOnWriteArrayList<>();

    public static MusicManager get() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        @SuppressLint("StaticFieldLeak")
        private static final MusicManager sInstance = new MusicManager();
    }

    MusicManager() {
        mClientHandler = new ClientHandler(this);
    }

    public MusicManager setContext(Context context) {
        mContext = context;
        return this;
    }

    public MusicManager setUseMediaPlayer(boolean isUseMediaPlayer) {
        this.isUseMediaPlayer = isUseMediaPlayer;
        return this;
    }

    public MusicManager setAutoPlayNext(boolean autoPlayNext) {
        isAutoPlayNext = autoPlayNext;
        return this;
    }

    public void bindService() {
        Intent intent = new Intent(mContext, MusicService.class);
        intent.putExtra("isUseMediaPlayer", isUseMediaPlayer);
        intent.putExtra("isAutoPlayNext", isAutoPlayNext);
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            control = IPlayControl.Stub.asInterface(iBinder);
            try {
                control.registerPlayerEventListener(mOnPlayerEventListener);
                LogUtil.i("--onServiceConnected--");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            LogUtil.i("--onServiceDisconnected--");
        }
    };

    public void unbindService() {
        try {
            if (control != null && control.asBinder().isBinderAlive()) {
                control.unregisterPlayerEventListener(mOnPlayerEventListener);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mContext.unbindService(mServiceConnection);
    }

    private IOnPlayerEventListener mOnPlayerEventListener = new IOnPlayerEventListener.Stub() {
        @Override
        public void onMusicSwitch(MusicInfo music) {
            mClientHandler.obtainMessage(MSG_MUSIC_CHANGE, music).sendToTarget();
        }

        @Override
        public void onPlayerStart() {
            mClientHandler.obtainMessage(MSG_PLAYER_START).sendToTarget();
        }

        @Override
        public void onPlayerPause() {
            mClientHandler.obtainMessage(MSG_PLAYER_PAUSE).sendToTarget();
        }

        @Override
        public void onPlayCompletion() {
            mClientHandler.obtainMessage(MSG_PLAY_COMPLETION).sendToTarget();
        }

        @Override
        public void onError(String errorMsg) {
            mClientHandler.obtainMessage(MSG_PLAYER_ERROR, errorMsg).sendToTarget();
        }

        @Override
        public void onBuffering(boolean isFinishBuffer) {
            mClientHandler.obtainMessage(MSG_BUFFERING, isFinishBuffer).sendToTarget();
        }
    };

    private static class ClientHandler extends Handler {

        private final WeakReference<MusicManager> mWeakReference;

        ClientHandler(MusicManager manager) {
            super(Looper.getMainLooper());
            mWeakReference = new WeakReference<>(manager);
        }

        @Override
        public void handleMessage(Message msg) {
            MusicManager manager = mWeakReference.get();
            switch (msg.what) {
                case MSG_MUSIC_CHANGE:
                    MusicInfo musicInfo = (MusicInfo) msg.obj;
                    manager.notifyPlayerEventChange(MSG_MUSIC_CHANGE, musicInfo, "", false);
                    break;
                case MSG_PLAYER_START:
                    manager.notifyPlayerEventChange(MSG_PLAYER_START, null, "", false);
                    break;
                case MSG_PLAYER_PAUSE:
                    manager.notifyPlayerEventChange(MSG_PLAYER_PAUSE, null, "", false);
                    break;

                case MSG_PLAY_COMPLETION:
                    manager.notifyPlayerEventChange(MSG_PLAY_COMPLETION, null, "", false);
                    break;
                case MSG_PLAYER_ERROR:
                    String errMsg = (String) msg.obj;
                    manager.notifyPlayerEventChange(MSG_PLAYER_ERROR, null, errMsg, false);
                    break;
                case MSG_BUFFERING:
                    boolean isFinishBuffer = (boolean) msg.obj;
                    manager.notifyPlayerEventChange(MSG_BUFFERING, null, "", isFinishBuffer);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    public void addPlayerEventListener(OnPlayerEventListener listener) {
        if (listener != null) {
            if (!mPlayerEventListeners.contains(listener)) {
                mPlayerEventListeners.add(listener);
            }
        }
    }

    public void removePlayerEventListener(OnPlayerEventListener listener) {
        if (listener != null) {
            if (mPlayerEventListeners.contains(listener)) {
                mPlayerEventListeners.remove(listener);
            }
        }
    }

    public void clearPlayerEventListener() {
        mPlayerEventListeners.clear();
    }

    private void notifyPlayerEventChange(int msg, MusicInfo info, String errorMsg, boolean isFinishBuffer) {
        for (OnPlayerEventListener listener : mPlayerEventListeners) {
            switch (msg) {
                case MSG_MUSIC_CHANGE:
                    listener.onMusicSwitch(info);
                    break;
                case MSG_PLAYER_START:
                    listener.onPlayerStart();
                    break;
                case MSG_PLAYER_PAUSE:
                    listener.onPlayerPause();
                    break;
                case MSG_PLAY_COMPLETION:
                    listener.onPlayCompletion();
                    break;
                case MSG_PLAYER_ERROR:
                    listener.onError(errorMsg);
                    break;
                case MSG_BUFFERING:
                    listener.onBuffering(isFinishBuffer);
                    break;
            }
        }
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
    public void playMusicByInfo(MusicInfo info) {
        if (control != null) {
            try {
                control.playMusicByInfo(info);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void playMusicByIndex(int index) {
        if (control != null) {
            try {
                control.playMusicByIndex(index);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void playMusicAutoStopWhen(List<MusicInfo> list, int index, int time) {
        if (control != null) {
            try {
                control.playMusicAutoStopWhen(list, index, time);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void playMusicByInfoAutoStopWhen(MusicInfo info, int time) {
        if (control != null) {
            try {
                control.playMusicByInfoAutoStopWhen(info, time);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void playMusicByIndexAutoStopWhen(int index, int time) {
        if (control != null) {
            try {
                control.playMusicByIndexAutoStopWhen(index, time);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setAutoStopTime(int time) {
        if (control != null) {
            try {
                control.setAutoStopTime(time);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getCurrPlayingIndex() {
        if (control != null) {
            try {
                return control.getCurrPlayingIndex();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public void pauseMusic() {
        if (control != null) {
            try {
                control.pauseMusic();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void resumeMusic() {
        if (control != null) {
            try {
                control.resumeMusic();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stopMusic() {
        if (control != null) {
            try {
                control.stopMusic();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setPlayList(List<MusicInfo> list) {
        if (control != null) {
            try {
                control.setPlayList(list);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setPlayListWithIndex(List<MusicInfo> list, int index) {
        if (control != null) {
            try {
                control.setPlayListWithIndex(list, index);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<MusicInfo> getPlayList() {
        if (control != null) {
            try {
                return control.getPlayList();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public int getStatus() {
        if (control != null) {
            try {
                return control.getStatus();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public void playNext() {
        if (control != null) {
            try {
                control.playNext();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void playPre() {
        if (control != null) {
            try {
                control.playPre();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean hasPre() {
        if (control != null) {
            try {
                return control.hasPre();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean hasNext() {
        if (control != null) {
            try {
                return control.hasNext();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public MusicInfo getPreMusic() {
        if (control != null) {
            try {
                return control.getPreMusic();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public MusicInfo getNextMusic() {
        if (control != null) {
            try {
                return control.getNextMusic();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public MusicInfo getCurrPlayingMusic() {
        if (control != null) {
            try {
                return control.getCurrPlayingMusic();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void setCurrMusic(int index) {
        if (control != null) {
            try {
                control.setCurrMusic(index);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setPlayMode(int mode) {
        if (control != null) {
            try {
                control.setPlayMode(mode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getPlayMode() {
        if (control != null) {
            try {
                return control.getPlayMode();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public long getProgress() {
        if (control != null) {
            try {
                return control.getProgress();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public void seekTo(int position) {
        if (control != null) {
            try {
                control.seekTo(position);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void reset() {
        if (control != null) {
            try {
                control.reset();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断当前的音乐是不是正在播放的音乐
     */
    public static boolean isCurrMusicIsPlayingMusic(MusicInfo currMusic) {
        MusicInfo playingMusic = MusicManager.get().getCurrPlayingMusic();
        return playingMusic != null && currMusic.musicId.equals(playingMusic.musicId);
    }

    /**
     * 是否在暂停
     */
    public static boolean isPaused() {
        return MusicManager.get().getStatus() == State.STATE_PAUSED;
    }

    /**
     * 是否正在播放
     */
    public static boolean isPlaying() {
        return MusicManager.get().getStatus() == State.STATE_PLAYING;
    }

    /**
     * 当前的音乐是否在播放
     */
    public static boolean isCurrMusicIsPlaying(MusicInfo currMusic) {
        return isCurrMusicIsPlayingMusic(currMusic) && isPlaying();
    }

    /**
     * 当前音乐是否在暂停
     */
    public static boolean isCurrMusicIsPaused(MusicInfo currMusic) {
        return isCurrMusicIsPlayingMusic(currMusic) && isPaused();
    }

    @Override
    public void registerPlayerEventListener(IOnPlayerEventListener listener) {

    }

    @Override
    public void unregisterPlayerEventListener(IOnPlayerEventListener listener) {

    }

    @Override
    public IBinder asBinder() {
        return null;
    }

}

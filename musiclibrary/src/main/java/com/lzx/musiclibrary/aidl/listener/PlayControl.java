package com.lzx.musiclibrary.aidl.listener;

import android.content.Context;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.v4.media.session.PlaybackStateCompat;

import com.lzx.musiclibrary.PlayMode;
import com.lzx.musiclibrary.aidl.model.MusicInfo;
import com.lzx.musiclibrary.helper.QueueHelper;
import com.lzx.musiclibrary.playback.PlaybackManager;
import com.lzx.musiclibrary.playback.QueueManager;
import com.lzx.musiclibrary.playback.State;

import java.util.List;

/**
 * Created by xian on 2018/1/28.
 */

public class PlayControl extends IPlayControl.Stub implements PlaybackManager.PlaybackServiceCallback {

    private QueueManager mQueueManager;
    private PlaybackManager mPlaybackManager;
    private PlayMode mPlayMode;
    private Context mContext;

    private RemoteCallbackList<IOnPlayerEventListener> mRemoteCallbackList;
    private NotifyContract.NotifyStatusChanged mNotifyStatusChanged;

    public PlayControl(Context context, QueueManager queueManager, PlaybackManager playbackManager) {
        mQueueManager = queueManager;
        mPlaybackManager = playbackManager;
        mContext = context;
        mPlaybackManager.setServiceCallback(this);

        mPlayMode = new PlayMode();
        mNotifyStatusChanged = new NotifyStatusChange();
        mRemoteCallbackList = new RemoteCallbackList<>();
    }

    private class NotifyStatusChange implements NotifyContract.NotifyStatusChanged {

        @Override
        public void notify(MusicInfo info, int index, int status, String errorMsg) {
            synchronized (NotifyStatusChange.class) {
                final int N = mRemoteCallbackList.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    IOnPlayerEventListener listener = mRemoteCallbackList.getBroadcastItem(i);
                    if (listener != null) {
                        try {
                            switch (status) {
                                case State.STATE_IDLE:
                                    listener.onPlayerStop();
                                    break;
                                case State.STATE_BUFFERING:
                                    listener.onBuffering(true);
                                    break;
                                case State.STATE_PLAYING:
                                    listener.onBuffering(false);
                                    listener.onPlayerStart();
                                    break;
                                case State.STATE_PAUSED:
                                    listener.onPlayerPause();
                                    break;
                                case State.STATE_ENDED:
                                    listener.onPlayCompletion();
                                    break;
                                case State.STATE_STOPPED:
                                    listener.onPlayerStop();
                                    break;
                                case State.STATE_ERROR:
                                    listener.onError("");
                                    break;
                                default:
                                    listener.onError("");
                                    break;
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
                mRemoteCallbackList.finishBroadcast();
            }
        }
    }


    @Override
    public void onPlaybackError(String errorMsg) {
        try {
            mNotifyStatusChanged.notify(getCurrPlayingMusic(), getCurrPlayingIndex(), State.STATE_ERROR, errorMsg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPlaybackCompletion() {

    }

    @Override
    public void onNotificationRequired() {

    }

    @Override
    public void onPlaybackStateUpdated(int state, PlaybackStateCompat newState) {
        try {
            mNotifyStatusChanged.notify(getCurrPlayingMusic(), getCurrPlayingIndex(), state, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void playMusic(List<MusicInfo> list, int index) throws RemoteException {
        if (!QueueHelper.isIndexPlayable(index, list)) {
            return;
        }
        mQueueManager.setCurrentQueue(list, index);
        mQueueManager.setCurrentQueueItem(list.get(index).musicId, QueueHelper.isNeedToSwitchMusic(mQueueManager, list, index));
    }

    @Override
    public void playMusicByInfo(MusicInfo info) throws RemoteException {
        if (info == null) {
            return;
        }
        mQueueManager.addQueueItem(info);
        mQueueManager.setCurrentQueueItem(info.musicId, QueueHelper.isNeedToSwitchMusic(mQueueManager, info));
    }

    @Override
    public void playMusicByIndex(int index) throws RemoteException {
        if (mQueueManager.getPlayingQueue().size() == 0) {
            return;
        }
        if (!QueueHelper.isIndexPlayable(index, mQueueManager.getPlayingQueue())) {
            return;
        }
        MusicInfo playInfo = mQueueManager.getPlayingQueue().get(index);
        mQueueManager.setCurrentQueueItem(playInfo.musicId, QueueHelper.isNeedToSwitchMusic(mQueueManager, playInfo));
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
        return mQueueManager.getCurrentIndex();
    }

    @Override
    public void pauseMusic() throws RemoteException {
        mPlaybackManager.handlePauseRequest();
    }

    @Override
    public void resumeMusic() throws RemoteException {
        mPlaybackManager.handlePlayRequest();
    }

    @Override
    public void stopMusic() throws RemoteException {
        mPlaybackManager.handleStopRequest("");
    }

    @Override
    public void setPlayList(List<MusicInfo> list) throws RemoteException {
        mQueueManager.setCurrentQueue(list);
    }

    @Override
    public void setPlayListWithIndex(List<MusicInfo> list, int index) throws RemoteException {
        mQueueManager.setCurrentQueue(list, index);
    }

    @Override
    public List<MusicInfo> getPlayList() throws RemoteException {
        return mQueueManager.getPlayingQueue();
    }

    @Override
    public int getStatus() throws RemoteException {
        return mPlaybackManager.getPlayback().getState();
    }

    @Override
    public void playNext() throws RemoteException {
        mPlaybackManager.playNextOrPre(1);
    }

    @Override
    public void playPre() throws RemoteException {
        mPlaybackManager.playNextOrPre(-1);
    }

    @Override
    public boolean hasPre() throws RemoteException {
        return mPlaybackManager.hasNextOrPre();
    }

    @Override
    public boolean hasNext() throws RemoteException {
        return mPlaybackManager.hasNextOrPre();
    }

    @Override
    public MusicInfo getPreMusic() throws RemoteException {
        return mQueueManager.getPreMusicInfo();
    }

    @Override
    public MusicInfo getNextMusic() throws RemoteException {
        return mQueueManager.getNextMusicInfo();
    }

    @Override
    public MusicInfo getCurrPlayingMusic() throws RemoteException {
        return mQueueManager.getCurrentMusic();
    }

    @Override
    public void setCurrMusic(int index) throws RemoteException {
        mQueueManager.setCurrentMusic(index);
    }

    @Override
    public void setPlayMode(int mode) throws RemoteException {
        mPlayMode.setCurrPlayMode(mode);
    }

    @Override
    public int getPlayMode() throws RemoteException {
        return mPlayMode.getCurrPlayMode();
    }

    @Override
    public long getProgress() throws RemoteException {
        return mPlaybackManager.getPlayback().getCurrentStreamPosition();
    }

    @Override
    public void seekTo(int position) throws RemoteException {
        mPlaybackManager.getPlayback().seekTo(position);
    }

    @Override
    public void reset() throws RemoteException {

    }

    @Override
    public void registerPlayerEventListener(IOnPlayerEventListener listener) throws RemoteException {
        mRemoteCallbackList.register(listener);
    }

    @Override
    public void unregisterPlayerEventListener(IOnPlayerEventListener listener) throws RemoteException {
        mRemoteCallbackList.unregister(listener);
    }
}

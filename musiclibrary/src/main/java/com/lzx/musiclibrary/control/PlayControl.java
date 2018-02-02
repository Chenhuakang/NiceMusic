package com.lzx.musiclibrary.control;

import android.content.Context;
import android.support.v4.media.session.PlaybackStateCompat;

import com.lzx.musiclibrary.OnPlayerEventListener;
import com.lzx.musiclibrary.PlayMode;
import com.lzx.musiclibrary.bean.MusicInfo;
import com.lzx.musiclibrary.helper.QueueHelper;
import com.lzx.musiclibrary.playback.PlaybackManager;
import com.lzx.musiclibrary.playback.QueueManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xian on 2018/1/28.
 */

public class PlayControl implements IPlayControl, PlaybackManager.PlaybackServiceCallback {

    private QueueManager mQueueManager;
    private PlaybackManager mPlaybackManager;
    private PlayMode mPlayMode;
    private Context mContext;

    private List<OnPlayerEventListener> mOnPlayerEventListeners;

    public PlayControl(Context context, QueueManager queueManager, PlaybackManager playbackManager) {
        mQueueManager = queueManager;
        mPlaybackManager = playbackManager;
        mContext = context;
        mPlayMode = new PlayMode();
        mPlaybackManager.setServiceCallback(this);
        mOnPlayerEventListeners = new ArrayList<>();
    }

    @Override
    public void playMusic(List<MusicInfo> list, int index) {
        if (!QueueHelper.isIndexPlayable(index, list)) {
            return;
        }
        mQueueManager.setCurrentQueue(list, index);
        mQueueManager.setCurrentQueueItem(list.get(index).musicId, QueueHelper.isNeedToSwitchMusic(mQueueManager, list, index));
    }

    @Override
    public void playMusic(MusicInfo info) {
        if (info == null) {
            return;
        }
        mQueueManager.addQueueItem(info);
        mQueueManager.setCurrentQueueItem(info.musicId, QueueHelper.isNeedToSwitchMusic(mQueueManager, info));
    }

    @Override
    public void playMusic(int index) {
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
    public void playMusicAutoStopWhen(List<MusicInfo> list, int index, int time) {

    }

    @Override
    public void playMusicAutoStopWhen(MusicInfo info, int time) {

    }

    @Override
    public void playMusicAutoStopWhen(int index, int time) {

    }

    @Override
    public void setAutoStopTime(int time) {

    }

    @Override
    public MusicInfo getCurrPlayingMusic() {
        return mQueueManager.getCurrentMusic();
    }

    @Override
    public void setCurrMusic(int index) {
        mQueueManager.setCurrentMusic(index);
    }

    @Override
    public int getCurrPlayingIndex() {
        return mQueueManager.getCurrentIndex();
    }

    @Override
    public void pauseMusic() {
        mPlaybackManager.handlePauseRequest();
    }

    @Override
    public void resumeMusic() {
        mPlaybackManager.handlePlayRequest();
    }

    @Override
    public void stopMusic() {
        mPlaybackManager.handleStopRequest("");
    }

    @Override
    public void setPlayList(List<MusicInfo> list) {
        mQueueManager.setCurrentQueue(list);
    }

    @Override
    public void setPlayList(List<MusicInfo> list, int index) {
        mQueueManager.setCurrentQueue(list, index);
    }

    @Override
    public List<MusicInfo> getPlayList() {
        return mQueueManager.getPlayingQueue();
    }

    @Override
    public int getStatus() {
        return mPlaybackManager.getPlayback().getState();
    }

    @Override
    public void playNext() {
        MusicInfo playInfo = mQueueManager.getNextMusicInfo();
        mQueueManager.setCurrentQueueItem(playInfo.musicId, QueueHelper.isNeedToSwitchMusic(mQueueManager, playInfo));
    }

    @Override
    public void playPre() {
        MusicInfo playInfo = mQueueManager.getPreMusicInfo();
        mQueueManager.setCurrentQueueItem(playInfo.musicId, QueueHelper.isNeedToSwitchMusic(mQueueManager, playInfo));
    }

    @Override
    public boolean hasPre() {
        return false;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public MusicInfo getPreMusic() {
        return mQueueManager.getPreMusicInfo();
    }

    @Override
    public MusicInfo getNextMusic() {
        return mQueueManager.getNextMusicInfo();
    }

    @Override
    public void setPlayMode(int mode) {
        mPlayMode.setCurrPlayMode(mContext, mode);
    }

    @Override
    public int getPlayMode() {
        return mPlayMode.getCurrPlayMode(mContext);
    }

    @Override
    public long getProgress() {
        return mPlaybackManager.getPlayback().getCurrentStreamPosition();
    }

    @Override
    public void seekTo(int position) {
        mPlaybackManager.getPlayback().seekTo(position);
    }

    @Override
    public void reset() {

    }

    @Override
    public void addPlayerEventListener(OnPlayerEventListener listener) {
        if (listener != null) {
            if (!mOnPlayerEventListeners.contains(listener)) {
                mOnPlayerEventListeners.add(listener);
            }
        }
    }

    @Override
    public void onPlaybackStart() {
        for (OnPlayerEventListener listener : mOnPlayerEventListeners) {
            listener.onPlayerStart();
        }
    }

    @Override
    public void onPlaybackPause() {
        for (OnPlayerEventListener listener : mOnPlayerEventListeners) {
            listener.onPlayerPause();
        }
    }

    @Override
    public void onPlaybackStop() {
        for (OnPlayerEventListener listener : mOnPlayerEventListeners) {
            listener.onPlayerStop();
        }
    }

    @Override
    public void onNotificationRequired() {

    }

    @Override
    public void onPlaybackStateUpdated(PlaybackStateCompat newState) {

    }
}

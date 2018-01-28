package com.lzx.musiclibrary.control;

import android.content.Context;

import com.lzx.musiclibrary.PlayMode;
import com.lzx.musiclibrary.bean.MusicInfo;
import com.lzx.musiclibrary.helper.QueueHelper;
import com.lzx.musiclibrary.playback.PlaybackManager;
import com.lzx.musiclibrary.playback.QueueManager;

import java.util.List;

/**
 * Created by xian on 2018/1/28.
 */

public class PlayControl implements IPlayControl {

    private QueueManager mQueueManager;
    private PlaybackManager mPlaybackManager;
    private PlayMode mPlayMode;
    private Context mContext;

    public PlayControl(Context context, QueueManager queueManager, PlaybackManager playbackManager) {
        mQueueManager = queueManager;
        mPlaybackManager = playbackManager;
        mContext = context;
        mPlayMode = new PlayMode();
    }

    @Override
    public void playMusic(List<MusicInfo> list, int index) {
        if (!QueueHelper.isIndexPlayable(index, list)) {
            return;
        }
        mQueueManager.setCurrentQueue(list, index);
        String currMusicId = mQueueManager.getCurrentMusic().musicId;
        mQueueManager.setCurrentQueueItem(list.get(index).musicId, !currMusicId.equals(list.get(index).musicId));
    }

    @Override
    public void playMusic(MusicInfo info) {
        if (info == null) {
            return;
        }
        mQueueManager.addQueueItem(info);
        String currMusicId = mQueueManager.getCurrentMusic().musicId;
        mQueueManager.setCurrentQueueItem(info.musicId, !currMusicId.equals(info.musicId));
    }

    @Override
    public void playMusic(int index) {
        if (mQueueManager.getPlayingQueue().size() == 0) {
            return;
        }
        if (!QueueHelper.isIndexPlayable(index, mQueueManager.getPlayingQueue())) {
            return;
        }
        String currMusicId = mQueueManager.getCurrentMusic().musicId;
        MusicInfo playInfo = mQueueManager.getPlayingQueue().get(index);
        mQueueManager.setCurrentQueueItem(playInfo.musicId, !currMusicId.equals(playInfo.musicId));
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
    public List<MusicInfo> getPlayList() {
        return mQueueManager.getPlayingQueue();
    }

    @Override
    public int getStatus() {
        return mPlaybackManager.getPlayback().getState();
    }

    @Override
    public void playNext() {
        String currMusicId = mQueueManager.getCurrentMusic().musicId;
        MusicInfo playInfo = mQueueManager.getNextMusicInfo();
        mQueueManager.setCurrentQueueItem(playInfo.musicId, !currMusicId.equals(playInfo.musicId));
    }

    @Override
    public void playPre() {
        String currMusicId = mQueueManager.getCurrentMusic().musicId;
        MusicInfo playInfo = mQueueManager.getPreMusicInfo();
        mQueueManager.setCurrentQueueItem(playInfo.musicId, !currMusicId.equals(playInfo.musicId));
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
}

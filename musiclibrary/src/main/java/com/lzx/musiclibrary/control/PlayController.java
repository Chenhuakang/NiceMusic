package com.lzx.musiclibrary.control;

import android.content.Context;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.lzx.musiclibrary.aidl.listener.NotifyContract;
import com.lzx.musiclibrary.aidl.model.MusicInfo;
import com.lzx.musiclibrary.constans.PlayMode;
import com.lzx.musiclibrary.constans.State;
import com.lzx.musiclibrary.helper.QueueHelper;
import com.lzx.musiclibrary.manager.QueueManager;
import com.lzx.musiclibrary.playback.PlaybackManager;
import com.lzx.musiclibrary.playback.player.Playback;

import java.util.List;

/**
 * @author lzx
 * @date 2018/2/8
 */

public class PlayController implements QueueManager.MetadataUpdateListener, PlaybackManager.PlaybackServiceCallback {

    private Context mContext;
    private QueueManager mQueueManager;
    private PlaybackManager mPlaybackManager;
    private MediaSessionCompat mSession;
    private PlayMode mPlayMode;
    private NotifyContract.NotifyStatusChanged mNotifyStatusChanged;
    private NotifyContract.NotifyMusicSwitch mNotifyMusicSwitch;
    private Playback mPlayback;

    PlayController(
            Context context,
            PlayMode playMode,
            Playback playback,
            NotifyContract.NotifyStatusChanged notifyStatusChanged,
            NotifyContract.NotifyMusicSwitch notifyMusicSwitch,
            boolean isAutoPlayNext) {

        mContext = context;
        mPlayMode = playMode;
        mPlayback = playback;
        mNotifyStatusChanged = notifyStatusChanged;
        mNotifyMusicSwitch = notifyMusicSwitch;

        mQueueManager = new QueueManager(this, mPlayMode);
        mPlaybackManager = new PlaybackManager(mPlayback, mQueueManager, mPlayMode, isAutoPlayNext);
        mPlaybackManager.setServiceCallback(this);

        mSession = new MediaSessionCompat(mContext, "MusicService");
        mSession.setCallback(mPlaybackManager.getMediaSessionCallback());
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mPlaybackManager.updatePlaybackState(null);
    }

    void playMusic(List<MusicInfo> list, int index, boolean isJustPlay) {
        mQueueManager.setCurrentQueue(list, index);
        setCurrentQueueItem(list.get(index), isJustPlay);
    }

    void playMusicByInfo(MusicInfo info, boolean isJustPlay) {
        mQueueManager.addQueueItem(info);
        setCurrentQueueItem(info, isJustPlay);
    }

    void playMusicByIndex(int index, boolean isJustPlay) {
        if (mQueueManager.getPlayingQueue().size() == 0) {
            return;
        }
        if (!QueueHelper.isIndexPlayable(index, mQueueManager.getPlayingQueue())) {
            return;
        }
        MusicInfo playInfo = mQueueManager.getPlayingQueue().get(index);
        setCurrentQueueItem(playInfo, isJustPlay);
    }

    int getCurrPlayingIndex() {
        return mQueueManager.getCurrentIndex();
    }

    void pauseMusic() {
        mPlaybackManager.handlePauseRequest();
    }

    void resumeMusic() {
        mPlaybackManager.handlePlayRequest();
    }

    void stopMusic() {
        mPlaybackManager.handleStopRequest("");
    }

    void setPlayList(List<MusicInfo> list) {
        mQueueManager.setCurrentQueue(list);
    }

    void setPlayListWithIndex(List<MusicInfo> list, int index) {
        mQueueManager.setCurrentQueue(list, index);
    }

    List<MusicInfo> getPlayList() {
        return mQueueManager.getPlayingQueue();
    }

    void deleteMusicInfoOnPlayList(MusicInfo info) {
        mQueueManager.deleteQueueItem(info);
    }

    int getState() {
        return mPlaybackManager.getPlayback().getState();
    }

    void playNext() {
        mPlaybackManager.playNextOrPre(1);
    }

    void playPre() {
        mPlaybackManager.playNextOrPre(-1);
    }

    boolean hasPre() {
        return mPlaybackManager.hasNextOrPre();
    }

    boolean hasNext() {
        return mPlaybackManager.hasNextOrPre();
    }

    MusicInfo getPreMusic() {
        return mQueueManager.getPreMusicInfo();
    }

    MusicInfo getNextMusic() {
        return mQueueManager.getNextMusicInfo();
    }

    MusicInfo getCurrPlayingMusic() {
        return mQueueManager.getCurrentMusic();
    }

    void setCurrMusic(int index) {
        mQueueManager.setCurrentMusic(index);
    }

    long getProgress() {
        return mPlaybackManager.getCurrentPosition();
    }

    void seekTo(int position) {
        mPlaybackManager.getPlayback().seekTo(position);
    }

    private void setCurrentQueueItem(MusicInfo info, boolean isJustPlay) {
        mQueueManager.setCurrentQueueItem(info.musicId, isJustPlay, QueueHelper.isNeedToSwitchMusic(mQueueManager, info));
    }

    @Override
    public void onMetadataChanged(MusicInfo metadata) {
        mSession.setMetadata(metadata.metadataCompat);
    }

    @Override
    public void onMetadataRetrieveError() {
        mPlaybackManager.updatePlaybackState("Unable to retrieve metadata.");
    }

    @Override
    public void onCurrentQueueIndexUpdated(int queueIndex, boolean isJustPlay, boolean isSwitchMusic) {
        //播放
        mPlaybackManager.handlePlayPauseRequest(isJustPlay, isSwitchMusic);
    }

    @Override
    public void onQueueUpdated(List<MediaSessionCompat.QueueItem> newQueue, List<MusicInfo> playingQueue) {
        mSession.setQueue(newQueue);
    }

    @Override
    public void onPlaybackSwitch(MusicInfo info) {
        mNotifyMusicSwitch.notify(info);
    }

    @Override
    public void onPlaybackError(String errorMsg) {
        mNotifyStatusChanged.notify(mQueueManager.getCurrentMusic(), mQueueManager.getCurrentIndex(), State.STATE_ERROR, errorMsg);
    }

    @Override
    public void onPlaybackCompletion() {
        mNotifyStatusChanged.notify(mQueueManager.getCurrentMusic(), mQueueManager.getCurrentIndex(), State.STATE_ENDED, null);
    }

    @Override
    public void onNotificationRequired() {

    }

    @Override
    public void onPlaybackStateUpdated(int state, PlaybackStateCompat newState) {
        //状态改变
        mNotifyStatusChanged.notify(mQueueManager.getCurrentMusic(), mQueueManager.getCurrentIndex(), state, null);

        mSession.setPlaybackState(newState);
        if (state == State.STATE_PLAYING) {
            mSession.setActive(true);
        }
    }

    void releaseMediaSession() {
        mSession.release();
    }
}

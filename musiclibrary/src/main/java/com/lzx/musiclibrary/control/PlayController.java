package com.lzx.musiclibrary.control;

import android.content.Context;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.lzx.musiclibrary.MusicService;
import com.lzx.musiclibrary.aidl.listener.NotifyContract;
import com.lzx.musiclibrary.aidl.model.SongInfo;
import com.lzx.musiclibrary.constans.PlayMode;
import com.lzx.musiclibrary.constans.State;
import com.lzx.musiclibrary.helper.QueueHelper;
import com.lzx.musiclibrary.manager.MediaSessionManager;
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
    private MediaSessionManager mMediaSessionManager;
    private PlayMode mPlayMode;
    private NotifyContract.NotifyStatusChanged mNotifyStatusChanged;
    private NotifyContract.NotifyMusicSwitch mNotifyMusicSwitch;
    private Playback mPlayback;


    PlayController(
            Context context,
            MusicService musicService,
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

        mMediaSessionManager = new MediaSessionManager(mContext, mPlaybackManager);
        mPlaybackManager.updatePlaybackState(null);
    }

    void playMusic(List<SongInfo> list, int index, boolean isJustPlay) {
        mQueueManager.setCurrentQueue(list, index);
        setCurrentQueueItem(list.get(index), isJustPlay);
    }

    void playMusicByInfo(SongInfo info, boolean isJustPlay) {
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
        SongInfo playInfo = mQueueManager.getPlayingQueue().get(index);
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

    void setPlayList(List<SongInfo> list) {
        mQueueManager.setCurrentQueue(list);
    }

    void setPlayListWithIndex(List<SongInfo> list, int index) {
        mQueueManager.setCurrentQueue(list, index);
    }

    List<SongInfo> getPlayList() {
        return mQueueManager.getPlayingQueue();
    }

    void deleteMusicInfoOnPlayList(SongInfo info, boolean isNeedToPlayNext) {
        mQueueManager.deleteQueueItem(info, isNeedToPlayNext);
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

    SongInfo getPreMusic() {
        return mQueueManager.getPreMusicInfo();
    }

    SongInfo getNextMusic() {
        return mQueueManager.getNextMusicInfo();
    }

    SongInfo getCurrPlayingMusic() {
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

    private void setCurrentQueueItem(SongInfo info, boolean isJustPlay) {
        mQueueManager.setCurrentQueueItem(info.getSongId(), isJustPlay, QueueHelper.isNeedToSwitchMusic(mQueueManager, info));
    }

    @Override
    public void onMetadataChanged(SongInfo metadata) {
        //   mSession.setMetadata(metadata.getMetadataCompat());
        mMediaSessionManager.updateMetaData(metadata.getMetadataCompat());
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
    public void onQueueUpdated(List<MediaSessionCompat.QueueItem> newQueue, List<SongInfo> playingQueue) {
       // mSession.setQueue(newQueue);
        mMediaSessionManager.setQueue(newQueue);
    }

    @Override
    public void onPlaybackSwitch(SongInfo info) {
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
        mMediaSessionManager.setPlaybackState(newState);
    }

    void releaseMediaSession() {
        mMediaSessionManager.release();
    }
}

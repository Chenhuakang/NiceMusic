package com.lzx.musiclibrary.playback;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.lzx.musiclibrary.PlayMode;
import com.lzx.musiclibrary.bean.MusicInfo;
import com.lzx.musiclibrary.helper.QueueHelper;


/**
 * Created by xian on 2018/1/20.
 */

public class PlaybackManager implements Playback.Callback {

    private Playback mPlayback;
    private QueueManager mQueueManager;
    private PlaybackServiceCallback mServiceCallback;
    private MediaSessionCallback mMediaSessionCallback;
    private PlayMode mPlayMode;

    public PlaybackManager(Playback playback, QueueManager queueManager) {
        mPlayback = playback;
        mPlayback.setCallback(this);
        mQueueManager = queueManager;
        mMediaSessionCallback = new MediaSessionCallback();
        mPlayMode = new PlayMode();
    }

    public void setServiceCallback(PlaybackServiceCallback serviceCallback) {
        mServiceCallback = serviceCallback;
    }

    public Playback getPlayback() {
        return mPlayback;
    }

    public MediaSessionCompat.Callback getMediaSessionCallback() {
        return mMediaSessionCallback;
    }

    /**
     * 播放
     */
    public void handlePlayRequest() {
        MusicInfo currentMusic = mQueueManager.getCurrentMusic();
        if (currentMusic != null) {
            mPlayback.play(currentMusic);
            if (mServiceCallback != null) {
                mServiceCallback.onPlaybackStart();
            }
        }
    }

    /**
     * 暂停
     */
    public void handlePauseRequest() {
        if (mPlayback.isPlaying()) {
            mPlayback.pause();
            if (mServiceCallback != null) {
                mServiceCallback.onPlaybackPause();
            }
        }
    }

    /**
     * 停止
     *
     * @param withError
     */
    public void handleStopRequest(String withError) {
        mPlayback.stop(true);
        if (mServiceCallback != null) {
            mServiceCallback.onPlaybackStop();
        }
        updatePlaybackState(withError);
    }

    /**
     * 播放/暂停
     */
    public void handlePlayPauseRequest(boolean isSwitchMusic) {
        int state = mPlayback.getState();
        if (state == PlaybackStateCompat.STATE_STOPPED || state == PlaybackStateCompat.STATE_NONE) {
            handlePlayRequest();
        } else if (state == PlaybackStateCompat.STATE_BUFFERING) {
            handleStopRequest(null);
        } else if (state == PlaybackStateCompat.STATE_PLAYING) {
            if (!isSwitchMusic) {
                handlePauseRequest();
            } else {
                handlePlayRequest();
            }
        } else if (state == PlaybackStateCompat.STATE_PAUSED) {
            handlePlayRequest();
        }
    }

    /**
     * 获取当前进度
     *
     * @return
     */
    public long getCurrentPosition() {
        long position = 0;
        if (mPlayback != null && mPlayback.isConnected()) {
            position = mPlayback.getCurrentStreamPosition();
        }
        return position;
    }

    /**
     * 播放完成
     */
    @Override
    public void onPlayCompletion() {
        if (mServiceCallback != null) {
            mServiceCallback.onPlaybackCompletion();
        }
        playNextOrPre(1);
    }

    /**
     * 播放上一首和下一首
     *
     * @param amount 负数为上一首，正数为下一首
     */
    public void playNextOrPre(int amount) {
        switch (mPlayMode.getCurrPlayMode()) {
            //顺序播放
            case PlayMode.PLAY_IN_ORDER:
                if (QueueHelper.isIndexPlayable(mQueueManager.getCurrentIndex(), mQueueManager.getPlayingQueue())) {
                    if (mQueueManager.skipQueuePosition(amount)) {
                        handlePlayRequest();
                        mQueueManager.updateMetadata();
                    }
                } else {
                    handleStopRequest(null);
                }
                break;
            //单曲循环
            case PlayMode.PLAY_IN_SINGLE_LOOP:
                if (mQueueManager.skipQueuePosition(0)) {
                    handlePlayRequest();
                    mQueueManager.updateMetadata();
                } else {
                    handleStopRequest(null);
                }
                break;
            //随机播放
            case PlayMode.PLAY_IN_RANDOM:
                //0到size-1的随机数
                int random = (int) (Math.random() * mQueueManager.getCurrentQueueSize() - 1);
                if (mQueueManager.skipQueuePosition(random)) {
                    handlePlayRequest();
                    mQueueManager.updateMetadata();
                } else {
                    handleStopRequest(null);
                }
                break;
            //列表循环
            case PlayMode.PLAY_IN_LIST_LOOP:
                if (mQueueManager.skipQueuePosition(amount)) {
                    handlePlayRequest();
                    mQueueManager.updateMetadata();
                }
                break;
            default:
                handleStopRequest(null);
                break;
        }
    }

    public boolean hasNextOrPre() {
        switch (mPlayMode.getCurrPlayMode()) {
            //顺序播放
            case PlayMode.PLAY_IN_ORDER:
                return QueueHelper.isIndexPlayable(mQueueManager.getCurrentIndex(), mQueueManager.getPlayingQueue());
            case PlayMode.PLAY_IN_SINGLE_LOOP:  //单曲循环
            case PlayMode.PLAY_IN_RANDOM:  //随机播放
            case PlayMode.PLAY_IN_LIST_LOOP:   //列表循环
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onPlaybackStatusChanged(int state) {
        updatePlaybackState(null);
    }

    @Override
    public void onError(String error) {
        updatePlaybackState(error);
    }

    /**
     * 设置正在播放的id
     *
     * @param mediaId being currently played
     */
    @Override
    public void setCurrentMediaId(String mediaId) {

    }

    public void updatePlaybackState(String error) {
        long position = PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN;
        if (mPlayback != null && mPlayback.isConnected()) {
            position = mPlayback.getCurrentStreamPosition();
        }
        //noinspection ResourceType
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(getAvailableActions());

        int state = mPlayback.getState();

        // If there is an error message, send it to the playback state:
        if (error != null) {
            // Error states are really only supposed to be used for errors that cause playback to
            // stop unexpectedly and persist until the user takes action to fix it.
            stateBuilder.setErrorMessage(error);
            state = PlaybackStateCompat.STATE_ERROR;
        }
        //noinspection ResourceType
        stateBuilder.setState(state, position, 1.0f, SystemClock.elapsedRealtime());
        // Set the activeQueueItemId if the current index is valid.
        MusicInfo currentMusic = mQueueManager.getCurrentMusic();
        if (currentMusic != null) {
            stateBuilder.setActiveQueueItemId(currentMusic.trackNumber);
        }
        if (mServiceCallback != null) {
            mServiceCallback.onPlaybackStateUpdated(stateBuilder.build());
            mServiceCallback.onPlaybackError(error);
        }
        //播放/暂停状态就通知通知栏更新
        if (state == PlaybackStateCompat.STATE_PLAYING || state == PlaybackStateCompat.STATE_PAUSED) {
            if (mServiceCallback != null) {
                mServiceCallback.onNotificationRequired();
            }
        }
    }

    private long getAvailableActions() {
        long actions =
                PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID |
                        PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT;
        if (mPlayback.isPlaying()) {
            actions |= PlaybackStateCompat.ACTION_PAUSE;
        } else {
            actions |= PlaybackStateCompat.ACTION_PLAY;
        }
        return actions;
    }

    public void switchToPlayback(Playback playback, boolean resumePlaying) {
        if (playback == null) {
            throw new IllegalArgumentException("Playback cannot be null");
        }
        // Suspends current state.
        int oldState = mPlayback.getState();
        long pos = mPlayback.getCurrentStreamPosition();
        String currentMediaId = mPlayback.getCurrentMediaId();
        mPlayback.stop(false);
        playback.setCallback(this);
        playback.setCurrentMediaId(currentMediaId);
        playback.seekTo(pos < 0 ? 0 : pos);
        playback.start();
        // Swaps instance.
        mPlayback = playback;
        switch (oldState) {
            case PlaybackStateCompat.STATE_BUFFERING:
            case PlaybackStateCompat.STATE_CONNECTING:
            case PlaybackStateCompat.STATE_PAUSED:
                mPlayback.pause();
                break;
            case PlaybackStateCompat.STATE_PLAYING:
                MusicInfo currentMusic = mQueueManager.getCurrentMusic();
                if (resumePlaying && currentMusic != null) {
                    mPlayback.play(currentMusic);
                } else if (!resumePlaying) {
                    mPlayback.pause();
                } else {
                    mPlayback.stop(true);
                }
                break;
            case PlaybackStateCompat.STATE_NONE:
                break;
            default:
                break;
        }
    }

    /**
     * 媒体操作
     */
    private class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {

            handlePlayRequest();
        }

        @Override
        public void onSkipToQueueItem(long queueId) {
            mQueueManager.setCurrentQueueItem(String.valueOf(queueId), true);
            mQueueManager.updateMetadata();
        }

        @Override
        public void onSeekTo(long position) {
            mPlayback.seekTo((int) position);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
//            mQueueManager.setQueueFromMusic(mediaId);
//            handlePlayRequest();
        }

        @Override
        public void onPause() {
            handlePauseRequest();
        }

        @Override
        public void onStop() {
            handleStopRequest(null);
        }

        @Override
        public void onSkipToNext() {
            if (mQueueManager.skipQueuePosition(1)) {
                handlePlayRequest();
            } else {
                handleStopRequest("Cannot skip");
            }
            mQueueManager.updateMetadata();
        }

        @Override
        public void onSkipToPrevious() {
            if (mQueueManager.skipQueuePosition(-1)) {
                handlePlayRequest();
            } else {
                handleStopRequest("Cannot skip");
            }
            mQueueManager.updateMetadata();
        }

        @Override
        public void onCustomAction(@NonNull String action, Bundle extras) {
            //媒体库点击
        }

        /**
         * 搜索
         * Handle free and contextual searches.
         * <p/>
         * All voice searches on Android Auto are sent to this method through a connected
         * {@link android.support.v4.media.session.MediaControllerCompat}.
         * <p/>
         * Threads and async handling:
         * Search, as a potentially slow operation, should run in another thread.
         * <p/>
         * Since this method runs on the main thread, most apps with non-trivial metadata
         * should defer the actual search to another thread (for example, by using
         * an {@link AsyncTask} as we do here).
         **/
        @Override
        public void onPlayFromSearch(final String query, final Bundle extras) {
            mPlayback.setState(PlaybackStateCompat.STATE_CONNECTING);
//            mMusicProvider.retrieveMediaAsync(new MusicProvider.Callback() {
//                @Override
//                public void onMusicCatalogReady(boolean success) {
//                    if (!success) {
//                        updatePlaybackState("Could not load catalog");
//                    }
//                    boolean successSearch = mQueueManager.setQueueFromSearch(query, extras);
//                    if (successSearch) {
//                        handlePlayRequest();
//                        mQueueManager.updateMetadata();
//                    } else {
//                        updatePlaybackState("Could not find music");
//                    }
//                }
//            });
        }
    }


    public interface PlaybackServiceCallback {
        void onPlaybackStart();

        void onPlaybackPause();

        void onPlaybackStop();

        void onPlaybackError(String errorMsg);

        void onPlaybackCompletion();

        void onNotificationRequired();

        void onPlaybackStateUpdated(PlaybackStateCompat newState);
    }
}

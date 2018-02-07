package com.lzx.musiclibrary;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.lzx.musiclibrary.aidl.listener.PlayControl;
import com.lzx.musiclibrary.aidl.model.MusicInfo;
import com.lzx.musiclibrary.playback.ExoPlayback;
import com.lzx.musiclibrary.playback.MediaPlayback;
import com.lzx.musiclibrary.playback.Playback;
import com.lzx.musiclibrary.playback.PlaybackManager;
import com.lzx.musiclibrary.playback.QueueManager;
import com.lzx.musiclibrary.playback.State;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by xian on 2018/1/20.
 */

public class MusicService extends Service implements QueueManager.MetadataUpdateListener, PlaybackManager.PlaybackServiceCallback {

    private static final int STOP_DELAY = 30000;

    private PlaybackManager mPlaybackManager;
    private QueueManager mQueueManager;
    private MediaSessionCompat mSession;
    private DelayedStopHandler mDelayedStopHandler;
    private Playback playback;
    private PlayMode mPlayMode;
    private Binder mBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayMode = new PlayMode();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        boolean isUseMediaPlayer = intent.getBooleanExtra("isUseMediaPlayer", false);
        boolean isAutoPlayNext = intent.getBooleanExtra("isAutoPlayNext", true);
        init(isUseMediaPlayer, isAutoPlayNext);
        return mBinder;
    }

    public void init(boolean isUseMediaPlayer, boolean isAutoPlayNext) {
        mDelayedStopHandler = new DelayedStopHandler(this);
        mQueueManager = new QueueManager(this,mPlayMode);

        playback = isUseMediaPlayer ? new MediaPlayback(this) : new ExoPlayback(this);
        mPlaybackManager = new PlaybackManager(playback, mQueueManager, mPlayMode, isAutoPlayNext);
        mPlaybackManager.setServiceCallback(this);

        mSession = new MediaSessionCompat(this, "MusicService");
        mSession.setCallback(mPlaybackManager.getMediaSessionCallback());
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mPlaybackManager.updatePlaybackState(null);

        mBinder = new PlayControl(this, mQueueManager, mPlaybackManager, mPlayMode);
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
    public void onCurrentQueueIndexUpdated(int queueIndex,boolean isJustPlay,  boolean isSwitchMusic) {
        mPlaybackManager.handlePlayPauseRequest(isJustPlay,isSwitchMusic);
    }

    /**
     * 播放队列更新时回调
     */
    @Override
    public void onQueueUpdated(List<MediaSessionCompat.QueueItem> newQueue, List<MusicInfo> playingQueue) {
        mSession.setQueue(newQueue);
    }

    @Override
    public void onNotificationRequired() {

    }

    @Override
    public void onPlaybackSwitch(MusicInfo info) {

    }

    @Override
    public void onPlaybackError(String errorMsg) {
    }

    @Override
    public void onPlaybackCompletion() {

    }

    @Override
    public void onPlaybackStateUpdated(int state, PlaybackStateCompat newState) {
        mSession.setPlaybackState(newState);
        if (state == State.STATE_PLAYING) {
            mSession.setActive(true);
            mDelayedStopHandler.removeCallbacksAndMessages(null);
            startService(new Intent(getApplicationContext(), MusicService.class));
        }
        if (state == State.STATE_ERROR) {
            mDelayedStopHandler.removeCallbacksAndMessages(null);
            mDelayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);
            stopForeground(true);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mDelayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlaybackManager.handleStopRequest(null);
        mDelayedStopHandler.removeCallbacksAndMessages(null);

        mSession.release();
    }

    private static class DelayedStopHandler extends Handler {
        private final WeakReference<MusicService> mWeakReference;

        private DelayedStopHandler(MusicService service) {
            mWeakReference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            MusicService service = mWeakReference.get();
            if (service != null && service.mPlaybackManager.getPlayback() != null) {
                if (service.mPlaybackManager.getPlayback().isPlaying()) {
                    return;
                }
                service.stopSelf();
            }
        }
    }


}

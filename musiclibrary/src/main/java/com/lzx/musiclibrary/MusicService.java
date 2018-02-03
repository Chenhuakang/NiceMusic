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
    private Binder mBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        mDelayedStopHandler = new DelayedStopHandler(this);
        mQueueManager = new QueueManager(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        boolean isUseMediaPlayer = intent.getBooleanExtra("isUseMediaPlayer", false);
        init(isUseMediaPlayer);
        return mBinder;
    }

    public void init(boolean isUseMediaPlayer) {
        playback = isUseMediaPlayer ? new MediaPlayback(this) : new ExoPlayback(this);
        mPlaybackManager = new PlaybackManager(playback, mQueueManager);
        mPlaybackManager.setServiceCallback(this);
        mPlaybackManager.updatePlaybackState(null);

        mSession = new MediaSessionCompat(this, "MusicService");
        mSession.setCallback(mPlaybackManager.getMediaSessionCallback());
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mBinder = new PlayControl(this, mQueueManager, mPlaybackManager);
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
    public void onCurrentQueueIndexUpdated(int queueIndex, boolean isSwitchMusic) {
        mPlaybackManager.handlePlayPauseRequest(isSwitchMusic);
    }

    /**
     * 播放队列更新时回调
     */
    @Override
    public void onQueueUpdated(List<MediaSessionCompat.QueueItem> newQueue, List<MusicInfo> playingQueue) {
        mSession.setQueue(newQueue);
    }

    @Override
    public void onPlaybackStart() {
        mSession.setActive(true);
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        startService(new Intent(getApplicationContext(), MusicService.class));

    }

    @Override
    public void onPlaybackPause() {

    }

    @Override
    public void onNotificationRequired() {

    }

    @Override
    public void onPlaybackStop() {
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mDelayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);
        stopForeground(true);

    }

    @Override
    public void onPlaybackError(String errorMsg) {
        onPlaybackStop();
    }

    @Override
    public void onPlaybackCompletion() {

    }

    @Override
    public void onPlaybackStateUpdated(PlaybackStateCompat newState) {
        mSession.setPlaybackState(newState);
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

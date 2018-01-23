package com.lzx.musiclibrary;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.lzx.musiclibrary.bean.MusicInfo;
import com.lzx.musiclibrary.helper.SourceHelper;
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
    private final DelayedStopHandler mDelayedStopHandler = new DelayedStopHandler(this);
    private Messenger mMessenger;
    private MessengerHandler mMessengerHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mMessengerHandler = new MessengerHandler(this);
        mMessenger = new Messenger(mMessengerHandler);
    }

    private void init(boolean isUseMediaPlayer) {
        mQueueManager = new QueueManager(this);

        Playback playback;
        if (isUseMediaPlayer) {
            playback = new MediaPlayback(this);
        } else {
            playback = new ExoPlayback(this);
        }
        mPlaybackManager = new PlaybackManager(playback, mQueueManager, this);

        mSession = new MediaSessionCompat(this, "MusicService");
        mSession.setCallback(mPlaybackManager.getMediaSessionCallback());
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mPlaybackManager.updatePlaybackState(null);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
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
    public void onCurrentQueueIndexUpdated(int queueIndex) {
        mPlaybackManager.handlePlayRequest();
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
    public void onNotificationRequired() {

    }

    @Override
    public void onPlaybackStop() {
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mDelayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);
        stopForeground(true);
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
        mMessengerHandler.removeCallbacksAndMessages(null);
        mMessengerHandler = null;
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

    private static class MessengerHandler extends Handler {
        private final WeakReference<MusicService> mWeakReference;

        private MessengerHandler(MusicService service) {
            mWeakReference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            MusicService service = mWeakReference.get();
            if (service == null) {
                return;
            }
            Bundle bundle = msg.getData();
            switch (msg.what) {
                //初始化
                case MusicConstants.MSG_INIT:
                    boolean isUseMediaPlayer = bundle.getBoolean(MusicConstants.KEY_IS_USE_MEDIAPLAYER);
                    service.init(isUseMediaPlayer);
                    break;
                //设置播放列表
                case MusicConstants.MSG_INIT_MUSIC_QUEUE:
                    List<MusicInfo> musicInfos = msg.getData().getParcelableArrayList(MusicConstants.KEY_MUSIC_LIST);
                    List<MusicInfo> musicQueue = SourceHelper.fetchMusicQueue(musicInfos);
                    service.mQueueManager.setCurrentQueue(musicQueue);
                    break;
                //根据音乐id播放
                case MusicConstants.MSG_PLAY_BY_MUSIC_ID:
                    String musicId = msg.getData().getString(MusicConstants.KEY_MUSIC_ID);

                    service.mQueueManager.setCurrentQueueItem(musicId);
                    break;
                //开始或暂停
                case MusicConstants.MSG_START_OR_PAUSE:
                    if (service.mPlaybackManager.getPlayback().isPlaying()) {
                        service.mPlaybackManager.handlePauseRequest();
                    }else {

                    }
                    break;
                default:
                    break;
            }
        }
    }


}

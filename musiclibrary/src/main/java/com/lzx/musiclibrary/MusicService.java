package com.lzx.musiclibrary;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
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
import com.lzx.musiclibrary.utils.LogUtil;

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
    private Messenger client;
    private MessengerHandler mMessengerHandler;
    private Handler mProgressHandler = new Handler();

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

        LogUtil.i("服务初始化成功....");
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
        sendMsgToClient(MusicConstants.MSG_MUSIC_START, null);
    }

    @Override
    public void onPlaybackPause() {
        sendMsgToClient(MusicConstants.MSG_MUSIC_PAUSE, null);
    }

    @Override
    public void onNotificationRequired() {

    }

    @Override
    public void onPlaybackStop() {
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mDelayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);
        stopForeground(true);
        sendMsgToClient(MusicConstants.MSG_MUSIC_STOP, null);
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
        mProgressHandler.removeCallbacksAndMessages(null);
        mMessengerHandler = null;
        mProgressHandler = null;
        mSession.release();
    }

    private Runnable mPublishRunnable = new Runnable() {
        @Override
        public void run() {
            sendMsgToClient(MusicConstants.MSG_MUSIC_PROGRESS,mPlaybackManager.getCurrentPosition());
            mProgressHandler.postDelayed(this, 1000);
        }
    };

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
            service.client = msg.replyTo;
            Bundle bundle = msg.getData();
            switch (msg.what) {
                //初始化
                case MusicConstants.MSG_INIT:
                    boolean isUseMediaPlayer = bundle.getBoolean(MusicConstants.KEY_IS_USE_MEDIAPLAYER);
                    service.init(isUseMediaPlayer);
                    break;
                //设置播放列表
                case MusicConstants.MSG_INIT_MUSIC_QUEUE:
                    List<MusicInfo> musicInfos = bundle.getParcelableArrayList(MusicConstants.KEY_MUSIC_LIST);
                    List<MusicInfo> musicQueue = SourceHelper.fetchMusicQueue(musicInfos);
                    service.mQueueManager.setCurrentQueue(musicQueue);
                    break;
                //根据音乐id播放
                case MusicConstants.MSG_PLAY_BY_MUSIC_ID:
                    String musicId = bundle.getString(MusicConstants.KEY_MUSIC_ID);
                    service.mQueueManager.setCurrentQueueItem(musicId, true);
                    break;
                //开始或暂停或切歌
                case MusicConstants.MSG_PLAY_BY_MUSIC_INFO:
                    MusicInfo info = bundle.getParcelable(MusicConstants.KEY_MUSIC_INFO);
                    if (info == null) {
                        return;
                    }
                    service.mQueueManager.addQueueItem(info);
                    String currMusicId = service.mQueueManager.getCurrentMusic().musicId;
                    service.mQueueManager.setCurrentQueueItem(info.musicId, !currMusicId.equals(info.musicId));
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 发送消息给客户端
     *
     * @param what   类型
     * @param bundle 数据
     */
    private void sendMsgToClient(int what, Bundle bundle) {
        if (client != null) {
            Message message = Message.obtain(null, what);
            message.setData(bundle);
            try {
                client.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

}

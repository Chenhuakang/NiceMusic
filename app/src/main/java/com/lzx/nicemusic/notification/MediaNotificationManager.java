package com.lzx.nicemusic.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.lzx.musiclibrary.IMediaNotification;
import com.lzx.musiclibrary.MusicService;
import com.lzx.musiclibrary.R;
import com.lzx.musiclibrary.aidl.model.SongInfo;
import com.lzx.musiclibrary.constans.State;
import com.lzx.musiclibrary.manager.QueueManager;
import com.lzx.musiclibrary.playback.PlaybackManager;
import com.lzx.musiclibrary.utils.AlbumArtCache;

/**
 * @author lzx
 * @date 2018/2/11
 */

public class MediaNotificationManager extends BroadcastReceiver implements IMediaNotification {

    private static final String CHANNEL_ID = "com.lzx.musiclibrary.MUSIC_CHANNEL_ID";

    private static final int NOTIFICATION_ID = 412;
    private static final int REQUEST_CODE = 100;

    public static final String ACTION_PAUSE = "com.lzx.musiclibrary.pause";
    public static final String ACTION_PLAY = "com.lzx.musiclibrary.play";
    public static final String ACTION_PREV = "com.lzx.musiclibrary.prev";
    public static final String ACTION_NEXT = "com.lzx.musiclibrary.next";
    public static final String ACTION_STOP = "com.lzx.musiclibrary.stop";
    public static final String ACTION_STOP_CASTING = "com.lzx.musiclibrary.stop_cast";

    private final NotificationManager mNotificationManager;

    private final PendingIntent mPlayIntent;
    private final PendingIntent mPauseIntent;
    private final PendingIntent mPreviousIntent;
    private final PendingIntent mNextIntent;
    private final PendingIntent mStopIntent;
    private final PendingIntent mStopCastIntent;

    private boolean mStarted = false;
    private MusicService mService;
    private PlaybackManager mPlaybackManager;
    private QueueManager mQueueManager;
    private SongInfo mSongInfo;


    public MediaNotificationManager(MusicService service, PlaybackManager playbackManager, QueueManager queueManager) {
        mService = service;
        mPlaybackManager = playbackManager;
        mQueueManager = queueManager;

        mNotificationManager = (NotificationManager) mService.getSystemService(Service.NOTIFICATION_SERVICE);

        String pkg = mService.getPackageName();
        mPauseIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE, new Intent(ACTION_PAUSE).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mPlayIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE, new Intent(ACTION_PLAY).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mPreviousIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE, new Intent(ACTION_PREV).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mNextIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE, new Intent(ACTION_NEXT).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mStopIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE, new Intent(ACTION_STOP).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mStopCastIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE, new Intent(ACTION_STOP_CASTING).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);

        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
        }
    }

    @Override
    public void stopNotification() {
        if (mStarted) {
            mStarted = false;
            try {
                mNotificationManager.cancel(NOTIFICATION_ID);
                mService.unregisterReceiver(this);
            } catch (IllegalArgumentException ex) {
                // ignore if the receiver is not registered.
            }
            mService.stopForeground(true);
        }
    }

    @Override
    public void startNotification() {
        if (!mStarted) {
            mSongInfo = mQueueManager.getCurrentMusic();
            Notification notification = createNotification();
            if (notification != null) {
                IntentFilter filter = new IntentFilter();
                filter.addAction(ACTION_NEXT);
                filter.addAction(ACTION_PAUSE);
                filter.addAction(ACTION_PLAY);
                filter.addAction(ACTION_PREV);
                filter.addAction(ACTION_STOP_CASTING);
                mService.registerReceiver(this, filter);
                mService.startForeground(NOTIFICATION_ID, notification);
                mStarted = true;
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        switch (action) {
            case ACTION_PAUSE:
                mPlaybackManager.handlePauseRequest();
                break;
            case ACTION_PLAY:
                mPlaybackManager.handlePlayRequest();
                break;
            case ACTION_NEXT:
                mPlaybackManager.playNextOrPre(1);
                break;
            case ACTION_PREV:
                mPlaybackManager.playNextOrPre(-1);
                break;
            case ACTION_STOP_CASTING:
//                Intent i = new Intent(context, MusicService.class);
//                i.setAction(MusicService.ACTION_CMD);
//                i.putExtra(MusicService.CMD_NAME, MusicService.CMD_STOP_CASTING);
//                mService.startService(i);
                break;
            default:
                break;
        }
    }

    private Notification createNotification() {
        if (mSongInfo == null) {
            return null;
        }
        String fetchArtUrl = null;
        Bitmap art = null;
        if (!TextUtils.isEmpty(mSongInfo.getSongCover())) {
            String artUrl = mSongInfo.getSongCover();
            art = AlbumArtCache.getInstance().getBigImage(artUrl);
            if (art == null) {
                fetchArtUrl = artUrl;
                art = BitmapFactory.decodeResource(mService.getResources(), R.drawable.ic_default_art);
            }
        }

        // Notification channels are only supported on Android O+.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        final NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(mService, CHANNEL_ID);
        notificationBuilder
                .setSmallIcon(R.drawable.ic_default_art)
                .setLargeIcon(art)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(createContentIntent(Notification.FLAG_ONGOING_EVENT))
                .setContent(createContentView())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker("正在播放")
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true);
        setNotificationPlaybackState(notificationBuilder);
        if (fetchArtUrl != null) {
            fetchBitmapFromURLAsync(fetchArtUrl, notificationBuilder);
        }
        return notificationBuilder.build();
    }

    /**
     * Creates Notification Channel. This is required in Android O+ to display notifications.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        if (mNotificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            NotificationChannel notificationChannel =
                    new NotificationChannel(CHANNEL_ID,
                            mService.getString(R.string.notification_channel),
                            NotificationManager.IMPORTANCE_LOW);

            notificationChannel.setDescription(
                    mService.getString(R.string.notification_channel_description));

            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private PendingIntent createContentIntent(int flags) {
        return PendingIntent.getActivity(mService, 1, new Intent(), flags);
    }

    private void setNotificationPlaybackState(NotificationCompat.Builder builder) {
        if (!mStarted) {
            mService.stopForeground(true);
            return;
        }
        builder.setOngoing(mPlaybackManager.getPlayback().getState() == State.STATE_PLAYING);
    }

    private void fetchBitmapFromURLAsync(final String bitmapUrl, final NotificationCompat.Builder builder) {
        AlbumArtCache.getInstance().fetch(bitmapUrl, new AlbumArtCache.FetchListener() {
            @Override
            public void onFetched(String artUrl, Bitmap bitmap, Bitmap icon) {
                if (mSongInfo != null && !TextUtils.isEmpty(mSongInfo.getSongCover()) && mSongInfo.getSongCover().equals(artUrl)) {
                    builder.setLargeIcon(bitmap);
                    mNotificationManager.notify(NOTIFICATION_ID, builder.build());
                }
            }
        });
    }

    private RemoteViews createContentView() {
        return null;
    }


    @Override
    public IBinder asBinder() {
        return null;
    }
}

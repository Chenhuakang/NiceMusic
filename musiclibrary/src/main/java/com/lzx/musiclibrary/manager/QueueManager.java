package com.lzx.musiclibrary.manager;

import android.graphics.Bitmap;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;

import com.lzx.musiclibrary.aidl.model.MusicInfo;
import com.lzx.musiclibrary.constans.PlayMode;
import com.lzx.musiclibrary.helper.QueueHelper;
import com.lzx.musiclibrary.utils.AlbumArtCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by xian on 2018/1/20.
 */

public class QueueManager {

    private List<MusicInfo> mPlayingQueue;
    private int mCurrentIndex;
    private MetadataUpdateListener mListener;
    private final ConcurrentMap<String, MusicInfo> mMusicListById;
    private PlayMode mPlayMode;

    public QueueManager(MetadataUpdateListener listener, PlayMode playMode) {
        mPlayingQueue = Collections.synchronizedList(new ArrayList<MusicInfo>());
        mMusicListById = new ConcurrentHashMap<>();
        mCurrentIndex = 0;
        mListener = listener;
        mPlayMode = playMode;
    }

    public void setListener(MetadataUpdateListener listener) {
        mListener = listener;
    }

    /**
     * 获取播放列表
     *
     * @return
     */
    public List<MusicInfo> getPlayingQueue() {
        return mPlayingQueue;
    }

    /**
     * 获取当前索引
     *
     * @return
     */
    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    /**
     * 设置当前的播放列表
     *
     * @param newQueue     整个队列
     * @param currentIndex 当前第几首
     */
    public void setCurrentQueue(List<MusicInfo> newQueue, int currentIndex) {
        mPlayingQueue = QueueHelper.fetchListWithMediaMetadata(newQueue);
        int index = 0;
        if (currentIndex != -1) {
            index = currentIndex;
        }
        mCurrentIndex = Math.max(index, 0);

        mMusicListById.clear();
        for (MusicInfo musicInfo : newQueue) {
            mMusicListById.put(musicInfo.musicId, musicInfo);
        }

        //通知播放列表更新了
        List<MediaSessionCompat.QueueItem> queueItems = QueueHelper.getQueueItems(mMusicListById);
        if (mListener != null) {
            mListener.onQueueUpdated(queueItems, mPlayingQueue);
        }
    }

    /**
     * 设置当前的播放列表 默认第一首
     *
     * @param newQueue 整个队列
     */
    public void setCurrentQueue(List<MusicInfo> newQueue) {
        setCurrentQueue(newQueue, -1);
    }

    /**
     * 添加一个音乐信息到队列中
     *
     * @param info 音乐信息
     */
    public void addQueueItem(MusicInfo info) {
        info = QueueHelper.fetchInfoWithMediaMetadata(info);
        if (mPlayingQueue.contains(info)) {
            return;
        }
        mPlayingQueue.add(info);
        mMusicListById.put(info.musicId, info);
        //通知播放列表更新了
        List<MediaSessionCompat.QueueItem> queueItems = QueueHelper.getQueueItems(mMusicListById);
        if (mListener != null) {
            mListener.onQueueUpdated(queueItems, mPlayingQueue);
        }
    }

    public void deleteQueueItem(MusicInfo info) {
        if (mPlayingQueue.size() == 0 || mMusicListById.size() == 0) {
            return;
        }
        if (!mPlayingQueue.contains(info) || !mMusicListById.containsKey(info.musicId)) {
            return;
        }
        //更改下标为下一首
        skipQueuePosition(1);
        mPlayingQueue.remove(info);
        mMusicListById.remove(info.musicId, info);
        List<MediaSessionCompat.QueueItem> queueItems = QueueHelper.getQueueItems(mMusicListById);
        if (mListener != null) {
            mListener.onQueueUpdated(queueItems, mPlayingQueue);
            //播放下一首
            mListener.onCurrentQueueIndexUpdated(mCurrentIndex, true, true);
        }
    }

    /**
     * 得到列表长度
     *
     * @return 队列长度
     */
    public int getCurrentQueueSize() {
        if (mPlayingQueue == null) {
            return 0;
        }
        return mPlayingQueue.size();
    }

    /**
     * 得到当前播放的音乐信息
     *
     * @return 音乐信息
     */
    public MusicInfo getCurrentMusic() {
        if (!QueueHelper.isIndexPlayable(mCurrentIndex, mPlayingQueue)) {
            return null;
        } else {
            return mPlayingQueue.get(mCurrentIndex);
        }
    }

    public void setCurrentMusic(int currentIndex) {
        if (mPlayingQueue.size() == 0) {
            return;
        }
        if (!QueueHelper.isIndexPlayable(currentIndex, mPlayingQueue)) {
            return;
        }
        this.mCurrentIndex = currentIndex;
    }

    /**
     * 转跳到指定位置
     *
     * @param amount 维度
     * @return
     */
    public boolean skipQueuePosition(int amount) {
        int index = mCurrentIndex + amount;
        if (index < 0) {
            // 在第一首歌曲之前向后跳，让你在第一首歌曲上
            index = 0;
        } else {
            //当在最后一首歌时点下一首将返回第一首个
            index %= mPlayingQueue.size();
        }
        if (!QueueHelper.isIndexPlayable(index, mPlayingQueue)) {
            return false;
        }
        mCurrentIndex = index;
        return true;
    }

    /**
     * 更新音乐艺术家信息
     *
     * @param musicId
     * @param bitmap
     * @param icon
     */
    public void updateMusicArt(String musicId, Bitmap bitmap, Bitmap icon) {
        MusicInfo musicInfo = QueueHelper.getMusicInfoById(mMusicListById, musicId);
        if (musicInfo == null) {
            return;
        }
        int index = mPlayingQueue.indexOf(musicInfo);
        musicInfo.musicCoverBitmap = bitmap;
        mPlayingQueue.set(index, musicInfo);
    }

    /**
     * 设置当前的音乐item，用于播放
     *
     * @param musicId 音乐id
     * @return
     */
    public void setCurrentQueueItem(String musicId, boolean isJustPlay, boolean isSwitchMusic) {
        int index = QueueHelper.getMusicIndexOnQueue(mPlayingQueue, musicId);

        setCurrentQueueIndex(index, isJustPlay, isSwitchMusic);
    }

    /**
     * 设置当前的音乐item，用于播放
     *
     * @param index 队列下标
     */
    private void setCurrentQueueIndex(int index, boolean isJustPlay, boolean isSwitchMusic) {
        if (index >= 0 && index < mPlayingQueue.size()) {
            mCurrentIndex = index;
            if (mListener != null) {
                mListener.onCurrentQueueIndexUpdated(mCurrentIndex, isJustPlay, isSwitchMusic);
            }
        }
    }

    /**
     * 得到上一首音乐信息
     */
    public MusicInfo getPreMusicInfo() {
        return getNextOrPreMusicInfo(-1);
    }

    /**
     * 得到下一首音乐信息
     */
    public MusicInfo getNextMusicInfo() {
        return getNextOrPreMusicInfo(1);
    }

    private MusicInfo getNextOrPreMusicInfo(int amount) {
        MusicInfo info = null;
        switch (mPlayMode.getCurrPlayMode()) {
            //单曲循环
            case PlayMode.PLAY_IN_SINGLE_LOOP:
                info = getCurrentMusic();
                break;
            //随机播放
            case PlayMode.PLAY_IN_RANDOM:
                //0到size-1的随机数
                int random = (int) (Math.random() * getCurrentQueueSize() - 1);
                if (skipQueuePosition(random)) {
                    info = getCurrentMusic();
                }
                break;
            //列表循环
            case PlayMode.PLAY_IN_LIST_LOOP:
                if (skipQueuePosition(amount)) {
                    info = getCurrentMusic();
                }
                break;
            default:
                info = null;
                break;
        }
        return info;
    }

    /**
     * 更新媒体信息
     */
    public void updateMetadata() {
        MusicInfo currentMusic = getCurrentMusic();
        if (currentMusic == null) {
            if (mListener != null) {
                mListener.onMetadataRetrieveError();
            }
            return;
        }
        final String musicId = currentMusic.musicId;
        MusicInfo metadata = QueueHelper.getMusicInfoById(mMusicListById, musicId);
        if (metadata == null) {
            throw new IllegalArgumentException("Invalid musicId " + musicId);
        }
        if (mListener != null) {
            mListener.onMetadataChanged(metadata);
        }
        // Set the proper album artwork on the media session, so it can be shown in the
        // locked screen and in other places.
        if (metadata.musicCoverBitmap == null && !TextUtils.isEmpty(metadata.musicCover)) {
            String albumUri = metadata.musicCover;
            AlbumArtCache.getInstance().fetch(albumUri, new AlbumArtCache.FetchListener() {
                @Override
                public void onFetched(String artUrl, Bitmap bitmap, Bitmap icon) {
                    updateMusicArt(musicId, bitmap, icon);

                    // If we are still playing the same music, notify the listeners:
                    MusicInfo currentMusic = getCurrentMusic();
                    if (currentMusic == null) {
                        return;
                    }
                    String currentPlayingId = currentMusic.musicId;
                    if (musicId.equals(currentPlayingId)) {
                        if (mListener != null) {
                            mListener.onMetadataChanged(QueueHelper.getMusicInfoById(mMusicListById, currentPlayingId));
                        }
                    }
                }
            });
        }
    }


    public interface MetadataUpdateListener {
        void onMetadataChanged(MusicInfo metadata);

        void onMetadataRetrieveError();

        void onCurrentQueueIndexUpdated(int queueIndex, boolean isJustPlay, boolean isSwitchMusic);

        void onQueueUpdated(List<MediaSessionCompat.QueueItem> newQueue, List<MusicInfo> playingQueue);
    }


}

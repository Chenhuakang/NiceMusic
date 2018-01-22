package com.lzx.nicemusic.lib.playback;

import android.graphics.Bitmap;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;

import com.lzx.nicemusic.lib.AlbumArtCache;
import com.lzx.nicemusic.lib.bean.MusicInfo;

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

    public QueueManager(MetadataUpdateListener listener) {
        mPlayingQueue = Collections.synchronizedList(new ArrayList<MusicInfo>());
        mMusicListById = new ConcurrentHashMap<>();
        mCurrentIndex = 0;
        mListener = listener;
    }

    /**
     * 设置当前的播放列表
     *
     * @param newQueue
     * @param currentIndex 当前第几首
     */
    public void setCurrentQueue(List<MusicInfo> newQueue, int currentIndex) {
        mPlayingQueue = newQueue;
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
        List<MediaSessionCompat.QueueItem> queueItems = getQueueItems(mPlayingQueue);
        mListener.onQueueUpdated(queueItems);
    }

    public void setCurrentQueue(List<MusicInfo> newQueue) {
        setCurrentQueue(newQueue, -1);
    }

    public void addQueueItem(MusicInfo info) {
        mPlayingQueue.add(info);
        mMusicListById.put(info.musicId, info);
        //通知播放列表更新了
        List<MediaSessionCompat.QueueItem> queueItems = getQueueItems(mPlayingQueue);
        mListener.onQueueUpdated(queueItems);
    }

    public MusicInfo getMusicInfoById(String musicId) {
        return mMusicListById.containsKey(musicId) ? mMusicListById.get(musicId) : null;
    }

    public List<MediaSessionCompat.QueueItem> getQueueItems(List<MusicInfo> queue) {
        List<MediaMetadataCompat> result = new ArrayList<>();
        Iterable<MediaMetadataCompat> musics = getMusics();
        for (MediaMetadataCompat metadata : musics) {
            result.add(metadata);
        }
        return convertToQueue(result);
    }

    public Iterable<MediaMetadataCompat> getMusics() {
        if (mMusicListById.size() == 0) {
            return Collections.emptyList();
        }
        List<MediaMetadataCompat> compatArrayList = new ArrayList<>(mMusicListById.size());
        for (MusicInfo mutableMetadata : mMusicListById.values()) {
            compatArrayList.add(mutableMetadata.metadataCompat);
        }
        return compatArrayList;
    }

    private static List<MediaSessionCompat.QueueItem> convertToQueue(Iterable<MediaMetadataCompat> tracks) {
        List<MediaSessionCompat.QueueItem> queue = new ArrayList<>();
        int count = 0;
        for (MediaMetadataCompat track : tracks) {

            // We create a hierarchy-aware mediaID, so we know what the queue is about by looking
            // at the QueueItem media IDs.
            String hierarchyAwareMediaID = track.getDescription().getMediaId();

            MediaMetadataCompat trackCopy = new MediaMetadataCompat.Builder(track)
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, hierarchyAwareMediaID)
                    .build();

            // We don't expect queues to change after created, so we use the item index as the
            // queueId. Any other number unique in the queue would work.
            MediaSessionCompat.QueueItem item = new MediaSessionCompat.QueueItem(
                    trackCopy.getDescription(), count++);
            queue.add(item);
        }
        return queue;

    }


    /**
     * 得到列表长度
     *
     * @return
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
     * @return
     */
    public MusicInfo getCurrentMusic() {
        if (!isIndexPlayable(mCurrentIndex, mPlayingQueue)) {
            return null;
        }
        return mPlayingQueue.get(mCurrentIndex);
    }

    public boolean skipQueuePosition(int amount) {
        int index = mCurrentIndex + amount;
        if (index < 0) {
            // 在第一首歌曲之前向后跳，让你在第一首歌曲上
            index = 0;
        } else {
            //当在最后一首歌时点下一首将返回第一首个
            index %= mPlayingQueue.size();
        }
        if (!isIndexPlayable(index, mPlayingQueue)) {
            return false;
        }
        mCurrentIndex = index;
        return true;
    }

    public void updateMusicArt(String musicId, Bitmap bitmap, Bitmap icon) {
        MusicInfo musicInfo = getMusicInfoById(musicId);
        int index = mPlayingQueue.indexOf(musicInfo);
        musicInfo.musicCoverBitmap = bitmap;
        mPlayingQueue.set(index, musicInfo);
    }

    public boolean setCurrentQueueItem(String mediaId) {
        // set the current index on queue from the music Id:
        int index = getMusicIndexOnQueue(mPlayingQueue, mediaId);
        setCurrentQueueIndex(index);
        return index >= 0;
    }

    private void setCurrentQueueIndex(int index) {
        if (index >= 0 && index < mPlayingQueue.size()) {
            mCurrentIndex = index;
            mListener.onCurrentQueueIndexUpdated(mCurrentIndex);
        }
    }

    public static int getMusicIndexOnQueue(Iterable<MusicInfo> queue,
                                           String mediaId) {
        int index = 0;
        for (MusicInfo item : queue) {
            if (mediaId.equals(item.musicId)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     * 更新媒体信息
     */
    public void updateMetadata() {
        MusicInfo currentMusic = getCurrentMusic();
        if (currentMusic == null) {
            mListener.onMetadataRetrieveError();
            return;
        }
        final String musicId = currentMusic.musicId;
        MusicInfo metadata = getMusicInfoById(musicId);
        if (metadata == null) {
            throw new IllegalArgumentException("Invalid musicId " + musicId);
        }

        mListener.onMetadataChanged(metadata);

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
                        mListener.onMetadataChanged(getMusicInfoById(currentPlayingId));
                    }
                }
            });
        }
    }

    /**
     * 判断index是否合法
     */
    public static boolean isIndexPlayable(int index, List<MusicInfo> queue) {
        return (queue != null && index >= 0 && index < queue.size());
    }

    public interface MetadataUpdateListener {
        void onMetadataChanged(MusicInfo metadata);

        void onMetadataRetrieveError();

        void onCurrentQueueIndexUpdated(int queueIndex);

        void onQueueUpdated(List<MediaSessionCompat.QueueItem> newQueue);
    }


}

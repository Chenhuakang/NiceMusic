package com.lzx.musiclibrary.helper;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.lzx.musiclibrary.bean.MusicInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by xian on 2018/1/22.
 */

public class QueueHelper {

    public static List<MediaSessionCompat.QueueItem> getQueueItems(ConcurrentMap<String, MusicInfo> musicListById) {
        List<MediaMetadataCompat> result = new ArrayList<>();
        Iterable<MediaMetadataCompat> musics = getMusics(musicListById);
        for (MediaMetadataCompat metadata : musics) {
            result.add(metadata);
        }
        return convertToQueue(result);
    }

    private static Iterable<MediaMetadataCompat> getMusics(ConcurrentMap<String, MusicInfo> musicListById) {
        if (musicListById.size() == 0) {
            return Collections.emptyList();
        }
        List<MediaMetadataCompat> compatArrayList = new ArrayList<>(musicListById.size());
        for (MusicInfo mutableMetadata : musicListById.values()) {
            compatArrayList.add(mutableMetadata.metadataCompat);
        }
        return compatArrayList;
    }

    private static List<MediaSessionCompat.QueueItem> convertToQueue(Iterable<MediaMetadataCompat> tracks) {
        List<MediaSessionCompat.QueueItem> queue = new ArrayList<>();
        int count = 0;
        for (MediaMetadataCompat track : tracks) {

            String hierarchyAwareMediaID = track.getDescription().getMediaId();

            MediaMetadataCompat trackCopy = new MediaMetadataCompat.Builder(track)
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, hierarchyAwareMediaID)
                    .build();

            MediaSessionCompat.QueueItem item = new MediaSessionCompat.QueueItem(
                    trackCopy.getDescription(), count++);
            queue.add(item);
        }
        return queue;
    }


    public static MusicInfo getMusicInfoById(ConcurrentMap<String, MusicInfo> musicListById,String musicId) {
        return musicListById.containsKey(musicId) ? musicListById.get(musicId) : null;
    }

    /**
     * 判断index是否合法
     */
    public static boolean isIndexPlayable(int index, List<MusicInfo> queue) {
        return (queue != null && index >= 0 && index < queue.size());
    }

    public static int getMusicIndexOnQueue(Iterable<MusicInfo> queue, String mediaId) {
        int index = 0;
        for (MusicInfo item : queue) {
            if (mediaId.equals(item.musicId)) {
                return index;
            }
            index++;
        }
        return -1;
    }


}

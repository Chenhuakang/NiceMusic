package com.lzx.nicemusic.lib.helper;

import android.support.v4.media.MediaMetadataCompat;

import com.lzx.nicemusic.lib.bean.MusicInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xian on 2018/1/20.
 */

public class SourceHelper {

    public static String CUSTOM_METADATA_TRACK_SOURCE = "__SOURCE__";

    public static List<MusicInfo> fetchMusicQueue(List<MusicInfo> queue) {
        List<MusicInfo> results = new ArrayList<>();
        for (MusicInfo info : queue) {
            info.metadataCompat = getMediaMetadataCompat(info);
            results.add(info);
        }
        return results;
    }

    private static MediaMetadataCompat getMediaMetadataCompat(MusicInfo info) {
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, info.musicId)
                .putString(CUSTOM_METADATA_TRACK_SOURCE, info.musicUrl)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, info.albumTitle)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, info.musicArtist)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, info.musicDuration)
                .putString(MediaMetadataCompat.METADATA_KEY_GENRE, info.musicGenre)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, info.musicCover)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, info.musicTitle)
                .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, info.trackNumber)
                .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, info.albumMusicCount)
                .build();
    }

}

package com.lzx.nicemusic.lib.model;

import android.support.v4.media.MediaMetadataCompat;

import com.lzx.nicemusic.lib.bean.MusicInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 模拟获取音乐数据
 *
 * @author lzx
 * @date 2018/1/16
 */

public class RemoteJSONSource implements MusicProviderSource {

    private List<MusicInfo> mMusicInfos = new ArrayList<>();

    @Override
    public Iterator<MusicInfo> iterator() {
        List<MusicInfo> tracks = fetchMusicInfoList(mMusicInfos);
        return tracks.iterator();
    }

    public void setMusicInfos(List<MusicInfo> musicInfos) {
        mMusicInfos = musicInfos;
    }

    public List<MusicInfo> fetchMusicInfoList(List<MusicInfo> musicInfos) {
        if (musicInfos == null) {
            return new ArrayList<>();
        }
        List<MusicInfo> list = new ArrayList<>();
        for (MusicInfo info : musicInfos) {
            info.metadataCompat = getMediaMetadataCompat(info);
            list.add(info);
        }
        return list;
    }

    private MediaMetadataCompat getMediaMetadataCompat(MusicInfo info) {
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, info.musicId)
                .putString(MusicProviderSource.CUSTOM_METADATA_TRACK_SOURCE, info.musicUrl)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, info.albumTitle)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, info.musicArtist)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, info.musicDuration)
                .putString(MediaMetadataCompat.METADATA_KEY_GENRE, info.musicGenre)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, info.albumCover)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, info.musicTitle)
                .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, info.trackNumber)
                .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, info.albumMusicCount)
                .build();
    }


}

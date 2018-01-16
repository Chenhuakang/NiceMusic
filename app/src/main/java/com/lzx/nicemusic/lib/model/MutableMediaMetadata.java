package com.lzx.nicemusic.lib.model;

import android.support.v4.media.MediaMetadataCompat;
import android.text.TextUtils;

/**
 * @author lzx
 * @date 2018/1/16
 */

public class MutableMediaMetadata {
    //音乐媒体信息
    public MediaMetadataCompat metadata;
    public final String trackId;

    public MutableMediaMetadata(String trackId, MediaMetadataCompat metadata) {
        this.metadata = metadata;
        this.trackId = trackId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != MutableMediaMetadata.class) {
            return false;
        }

        MutableMediaMetadata that = (MutableMediaMetadata) o;

        return TextUtils.equals(trackId, that.trackId);
    }

    @Override
    public int hashCode() {
        return trackId.hashCode();
    }
}

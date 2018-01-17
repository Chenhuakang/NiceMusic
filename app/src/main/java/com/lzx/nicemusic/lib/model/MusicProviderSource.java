package com.lzx.nicemusic.lib.model;

import com.lzx.nicemusic.lib.bean.MusicInfo;

import java.util.Iterator;

/**
 * @author lzx
 * @date 2018/1/16
 */

public interface MusicProviderSource {
    String CUSTOM_METADATA_TRACK_SOURCE = "__SOURCE__";

    // Iterator<MediaMetadataCompat> iterator();
    Iterator<MusicInfo> iterator();
}

package com.lzx.musiclibrary;

import android.content.Context;

import com.lzx.musiclibrary.utils.SPUtils;

/**
 * Created by xian on 2018/1/28.
 */

public class PlayMode {
    /**
     * 顺序播放
     */
    public static final int PLAY_IN_ORDER = 0;

    /**
     * 单曲循环
     */
    public static final int PLAY_IN_SINGLE_LOOP = 1;

    /**
     * 随机播放
     */
    public static final int PLAY_IN_RANDOM = 2;

    /**
     * 列表循环
     */
    public static final int PLAY_IN_LIST_LOOP = 3;

    private int currPlayMode = PLAY_IN_ORDER;

    public int getCurrPlayMode(Context context) {
        currPlayMode = (int) SPUtils.get(context, "music_key_play_model", PLAY_IN_ORDER);
        return currPlayMode;
    }

    public void setCurrPlayMode(Context context, int currPlayMode) {
        this.currPlayMode = currPlayMode;
        SPUtils.put(context, "music_key_play_model", currPlayMode);
    }

    public int getCurrPlayMode() {
        return currPlayMode;
    }

    public void setCurrPlayMode(int currPlayMode) {
        this.currPlayMode = currPlayMode;
    }
}

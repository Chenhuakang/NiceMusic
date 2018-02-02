package com.lzx.musiclibrary;


import com.lzx.musiclibrary.bean.MusicInfo;

/**
 * 播放进度监听器
 */
public interface OnPlayerEventListener {

    /**
     * 切换歌曲
     */
    void onMusicChange(MusicInfo music);

    /**
     * 继续播放
     */
    void onPlayerStart();

    /**
     * 暂停播放
     */
    void onPlayerPause();

    void onPlayerStop();

    /**
     * 播放完成
     */
    void onPlayCompletion();


    void onError(int what, int extra);
}

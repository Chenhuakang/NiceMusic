package com.lzx.musiclibrary.aidl.listener;

import com.lzx.musiclibrary.aidl.model.MusicInfo;

/**
 * @author lzx
 * @date 2018/2/3
 */

public interface OnPlayerEventListener {
    void onMusicChange(MusicInfo music);

    void onPlayerStart();

    void onPlayerPause();

    void onPlayerStop();

    void onPlayCompletion();

    void onError(String errorMsg);

    void onBuffering(boolean isFinishBuffer);
}

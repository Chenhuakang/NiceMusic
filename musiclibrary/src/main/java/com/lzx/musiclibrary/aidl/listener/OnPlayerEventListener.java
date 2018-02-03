package com.lzx.musiclibrary.aidl.listener;

import com.lzx.musiclibrary.aidl.model.MusicInfo;

/**
 * @author lzx
 * @date 2018/2/3
 */

public abstract class OnPlayerEventListener extends IOnPlayerEventListener.Stub {
    @Override
    public abstract void onMusicChange(MusicInfo music);

    @Override
    public abstract void onPlayerStart();

    @Override
    public abstract void onPlayerPause();

    @Override
    public abstract void onPlayerStop();

    @Override
    public abstract void onPlayCompletion();

    @Override
    public abstract void onError(String errorMsg);
}

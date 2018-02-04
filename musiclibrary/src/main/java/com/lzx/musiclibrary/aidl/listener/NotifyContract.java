package com.lzx.musiclibrary.aidl.listener;

import com.lzx.musiclibrary.aidl.model.MusicInfo;

/**
 * @author lzx
 * @date 2018/2/3
 */

public interface NotifyContract {
    /**
     * 统一通知播放状态改变
     */
    interface NotifyStatusChanged {
        void notify(MusicInfo info, int index, int status, String errorMsg);
    }
}

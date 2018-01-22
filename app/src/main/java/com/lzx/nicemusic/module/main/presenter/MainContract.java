package com.lzx.nicemusic.module.main.presenter;

import com.lzx.nicemusic.base.mvp.BaseContract;
import com.lzx.musiclibrary.bean.MusicInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by xian on 2018/1/13.
 */

public interface MainContract {
    interface View extends BaseContract.BaseView {
        void requestMainDataSuccess(ConcurrentMap<String, List<MusicInfo>> map, List<Map.Entry<String, String>> types);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void requestMusicList();

        void updateCache();
    }
}

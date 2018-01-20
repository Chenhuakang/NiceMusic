package com.lzx.nicemusic.module.main.presenter;

import com.lzx.nicemusic.base.mvp.BaseContract;
import com.lzx.nicemusic.bean.HomeInfo;
import com.lzx.nicemusic.lib.bean.MusicInfo;

import java.util.List;

/**
 * Created by xian on 2018/1/13.
 */

public interface MainContract {
    interface View extends BaseContract.BaseView {
        void requestMainDataSuccess(List<MusicInfo> dataList);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void requestMusicList();

        void updateCache();
    }
}

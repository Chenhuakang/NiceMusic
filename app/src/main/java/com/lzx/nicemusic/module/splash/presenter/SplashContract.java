package com.lzx.nicemusic.module.splash.presenter;

import com.lzx.nicemusic.base.mvp.BaseContract;
import com.lzx.nicemusic.bean.HomeInfo;

import java.util.List;

/**
 * Created by xian on 2018/1/14.
 */

public interface SplashContract {
    interface View extends BaseContract.BaseView {
        void requestMainDataSuccess(boolean hasCache);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void requestMusicList();
    }
}

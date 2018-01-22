package com.lzx.nicemusic.module.play.presenter;

import com.lzx.nicemusic.base.mvp.BaseContract;
import com.lzx.nicemusic.bean.SingerInfo;

/**
 * @author lzx
 * @date 2018/1/22
 */

public interface PlayContract {
    interface View extends BaseContract.BaseView {
        void onSingerInfoSuccess(SingerInfo singerInfo);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void requestSingerInfo(String uid);
    }
}

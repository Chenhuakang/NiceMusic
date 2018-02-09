package com.lzx.nicemusic.module.play.presenter;

import com.lzx.nicemusic.base.mvp.BaseContract;
import com.lzx.nicemusic.bean.LrcInfo;

/**
 * @author lzx
 * @date 2018/1/22
 */

public interface PlayContract {
    interface View extends BaseContract.BaseView {
        void onLrcInfoSuccess(LrcInfo info);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getLrcInfo(String musicId);
    }
}

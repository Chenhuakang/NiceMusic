package com.lzx.nicemusic.module.main.presenter;

import com.lzx.nicemusic.base.mvp.BaseContract;
import com.lzx.nicemusic.bean.BannerInfo;

import java.util.List;

/**
 * Created by xian on 2018/1/13.
 */

public interface MainContract {
    interface View extends BaseContract.BaseView {
        void requestBannerSuccess(List<BannerInfo> list);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void requestBanner();
    }
}

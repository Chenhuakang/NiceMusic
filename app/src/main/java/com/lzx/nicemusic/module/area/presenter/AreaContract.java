package com.lzx.nicemusic.module.area.presenter;

import com.lzx.nicemusic.base.mvp.BaseContract;
import com.lzx.nicemusic.bean.MusicInfo;

import java.util.List;

/**
 * Created by xian on 2018/1/15.
 */

public interface AreaContract {
    interface View extends BaseContract.BaseView {
        void loadAreaDataSuccess(List<MusicInfo> infoList);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void requestAreaData(String topid);
    }
}

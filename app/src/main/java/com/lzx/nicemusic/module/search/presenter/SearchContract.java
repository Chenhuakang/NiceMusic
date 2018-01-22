package com.lzx.nicemusic.module.search.presenter;

import com.lzx.nicemusic.base.mvp.BaseContract;
import com.lzx.musiclibrary.bean.MusicInfo;

import java.util.List;

/**
 * Created by xian on 2018/1/14.
 */

public interface SearchContract {

    interface View extends BaseContract.BaseView {
        void loadDefaultSearchDataSuccess(List<String> hotSearch, List<String> historys);

        void searchSuccess(List<MusicInfo> infoList);

        void deleteHistorySuccess();
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void requestDefaultSearchData();

        void addHistory(String title);

        void deleteHistory(String title);

        void searchMusic(String keyword);
    }

}

package com.lzx.nicemusic.module.search.presenter;

import com.lzx.musiclibrary.aidl.model.SongInfo;
import com.lzx.nicemusic.base.mvp.BaseContract;

import java.util.List;

/**
 * Created by xian on 2018/1/14.
 */

public interface SearchContract {

    interface View extends BaseContract.BaseView {
        void loadDefaultSearchDataSuccess(List<String> hotSearch, List<String> historys);

        void searchSuccess(List<SongInfo> infoList);

        void deleteHistorySuccess();
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void requestDefaultSearchData();

        void addHistory(String title);

        void deleteHistory(String title);

        void searchMusic(String keyword);
    }

}

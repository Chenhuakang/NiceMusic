package com.lzx.nicemusic.module.main.presenter;

import com.lzx.musiclibrary.aidl.model.SongInfo;
import com.lzx.nicemusic.base.mvp.BaseContract;

import java.util.List;

/**
 * @author lzx
 * @date 2018/2/5
 */

public interface SongListContract {

    interface View extends BaseContract.BaseView {
        void onGetSongListSuccess(List<SongInfo> list,String title);

        void onGetLiveSongSuccess(List<SongInfo> list);

        void loadMoreSongListSuccess(List<SongInfo> list,String title);

        void loadFinishAllData();
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void requestSongList(String title);

        void requestLiveList(String title);

        void loadMoreSongList(String title);

        int getAlbumCover(String title);
    }

}

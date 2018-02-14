package com.lzx.nicemusic.module.artist.presenter;

import com.lzx.musiclibrary.aidl.model.SongInfo;
import com.lzx.nicemusic.base.mvp.BaseContract;

import java.util.List;

/**
 * @author lzx
 * @date 2018/2/14
 */

public interface ArtistContract {
    interface View extends BaseContract.BaseView {
        void onArtistSongsSuccess(List<SongInfo> list);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getArtistSongs(String artistId);
    }
}

package com.lzx.nicemusic.module.artist.presenter;

import com.lzx.musiclibrary.aidl.model.SongInfo;
import com.lzx.musiclibrary.utils.LogUtil;
import com.lzx.nicemusic.base.mvp.factory.BasePresenter;
import com.lzx.nicemusic.helper.DataHelper;
import com.lzx.nicemusic.network.RetrofitHelper;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * @author lzx
 * @date 2018/2/14
 */

public class ArtistPresenter extends BasePresenter<ArtistContract.View> implements ArtistContract.Presenter<ArtistContract.View> {
    @Override
    public void getArtistSongs(String artistId) {
        Disposable subscriber = RetrofitHelper.getMusicApi().requestArtistSongList(artistId, 20)
                .map(new Function<ResponseBody, List<SongInfo>>() {

                    @Override
                    public List<SongInfo> apply(ResponseBody responseBody) throws Exception {
                        return DataHelper.fetchJSONFromUrl(responseBody);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<SongInfo>>() {
                    @Override
                    public void accept(List<SongInfo> list) throws Exception {
                        LogUtil.i("--------------" + list.size());
                        mView.onArtistSongsSuccess(list);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.i(throwable.getMessage());
                    }
                });
        addSubscribe(subscriber);
    }
}

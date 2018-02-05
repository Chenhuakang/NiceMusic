package com.lzx.nicemusic.module.songlist.presenter;

import android.widget.Toast;

import com.lzx.musiclibrary.aidl.model.MusicInfo;
import com.lzx.nicemusic.base.mvp.factory.BasePresenter;
import com.lzx.nicemusic.helper.DataHelper;
import com.lzx.nicemusic.network.RetrofitHelper;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * @author lzx
 * @date 2018/2/5
 */

public class SongListPresenter extends BasePresenter<SongListContract.View> implements SongListContract.Presenter<SongListContract.View> {

    private int size = 10;
    private int offset = 0;
    private boolean isMore;

    @Override
    public void requestSongList(String title) {
        int type = getListType(title);
        RetrofitHelper.getMusicApi().requestMusicList(type, size, offset)
                .map(new Function<ResponseBody, List<MusicInfo>>() {
                    @Override
                    public List<MusicInfo> apply(ResponseBody responseBody) throws Exception {
                        return DataHelper.fetchJSONFromUrl(responseBody);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<MusicInfo>>() {
                    @Override
                    public void accept(List<MusicInfo> list) throws Exception {
                        isMore = list.size() >= size;
                        mView.onGetSongListSuccess(list);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    @Override
    public void loadMoreSongList(String title) {
        if (isMore) {
            offset += 10;
            int type = getListType(title);
            RetrofitHelper.getMusicApi().requestMusicList(type, size, offset)
                    .map(new Function<ResponseBody, List<MusicInfo>>() {
                        @Override
                        public List<MusicInfo> apply(ResponseBody responseBody) throws Exception {
                            return DataHelper.fetchJSONFromUrl(responseBody);
                        }
                    })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<MusicInfo>>() {
                        @Override
                        public void accept(List<MusicInfo> list) throws Exception {
                            isMore = list.size() >= size;
                            mView.loadMoreSongListSuccess(list);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });
        } else {
            Toast.makeText(mContext, "没有更多了", Toast.LENGTH_SHORT).show();
        }
    }

    private int getListType(String title) {
        int type = 0;
        switch (title) {
            case "新歌榜":
                type = 1;
                break;
            case "热歌榜":
                type = 2;
                break;
            case "摇滚榜":
                type = 11;
                break;
            case "爵士":
                type = 12;
                break;
            case "流行":
                type = 16;
                break;
            case "欧美金曲榜":
                type = 21;
                break;
            case "经典老歌榜":
                type = 22;
                break;
            case "情歌对唱榜":
                type = 23;
                break;
            case "影视金曲榜":
                type = 24;
                break;
            case "网络歌曲榜":
                type = 25;
                break;
        }
        return type;
    }
}

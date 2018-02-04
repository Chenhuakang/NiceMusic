package com.lzx.nicemusic.module.play.presenter;

import com.google.gson.Gson;
import com.lzx.musiclibrary.utils.LogUtil;
import com.lzx.nicemusic.base.mvp.factory.BasePresenter;
import com.lzx.nicemusic.bean.SingerInfo;
import com.lzx.nicemusic.network.RetrofitHelper;

import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author lzx
 * @date 2018/1/22
 */

public class PlayPresenter extends BasePresenter<PlayContract.View> implements PlayContract.Presenter<PlayContract.View> {
    @Override
    public void requestSingerInfo(String uid) {
        RetrofitHelper.getMusicApi().requestArtistInfo(uid)
                .map(responseBody -> {
                    String json = responseBody.string();
                    json = json.substring(1, json.length() - 2);
                    JSONObject jsonObject = new JSONObject(json);
                    return new Gson().fromJson(jsonObject.toString(), SingerInfo.class);
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(singerInfo -> mView.onSingerInfoSuccess(singerInfo), throwable -> LogUtil.i("throwable = " + throwable.getMessage()));
    }
}

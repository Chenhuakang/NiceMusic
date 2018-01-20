package com.lzx.nicemusic.module.main.presenter;

import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzx.nicemusic.base.mvp.factory.BasePresenter;
import com.lzx.nicemusic.bean.BannerInfo;
import com.lzx.nicemusic.bean.HomeInfo;
import com.lzx.nicemusic.db.CacheManager;
import com.lzx.nicemusic.lib.bean.MusicInfo;
import com.lzx.nicemusic.network.RetrofitHelper;
import com.lzx.nicemusic.utils.LogUtil;
import com.lzx.nicemusic.utils.SpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by xian on 2018/1/13.
 */

public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter<MainContract.View> {

    private MainModel mMainModel;

    public MainPresenter() {
        mMainModel = new MainModel();
    }

    @Override
    public void requestMusicList() {
        Disposable subscriber = Observable.create((ObservableOnSubscribe<String>) emitter -> {
            String json = CacheManager.getImpl().findCache(CacheManager.KEY_HOME_LIST_DATA);
            emitter.onNext(json);
        })
        .map((Function<String, List<MusicInfo>>)
                json ->
                        new Gson().fromJson(json, new TypeToken<List<MusicInfo>>() {
                        }.getType())
        )
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(homeInfos -> {
                    mView.requestMainDataSuccess(homeInfos);
                    updateCache();
                },
                throwable -> {
                    LogUtil.i("Error#requestMusicList = " + throwable.getMessage());
                    Toast.makeText(mContext, "数据解析出错", Toast.LENGTH_SHORT).show();
                });
        addSubscribe(subscriber);
    }

    @Override
    public void updateCache() {
        long currTime = SpUtil.getInstance().getLong("cache_main_data", System.currentTimeMillis());
        if (System.currentTimeMillis() - currTime > 24 * 60 * 60 * 1000) {
            SpUtil.getInstance().putLong("cache_main_data", System.currentTimeMillis());
            Disposable subscriber
            = mMainModel.loadMainData()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(musicInfos -> {
                    },
                    throwable -> {
                        LogUtil.i("Error#requestMusicList = " + throwable.getMessage());
                        Toast.makeText(mContext, "更新缓存出错", Toast.LENGTH_SHORT).show();
                    });
            addSubscribe(subscriber);
        }
    }


}


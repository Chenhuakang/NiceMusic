package com.lzx.nicemusic.module.area.presenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzx.nicemusic.base.mvp.factory.BasePresenter;
import com.lzx.nicemusic.db.CacheManager;
import com.lzx.nicemusic.lib.bean.MusicInfo;
import com.lzx.nicemusic.utils.LogUtil;
import com.lzx.nicemusic.utils.SpUtil;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xian on 2018/1/15.
 */

public class AreaPresenter extends BasePresenter<AreaContract.View> implements AreaContract.Presenter<AreaContract.View> {

    private AreaModel mAreaModel;

    public AreaPresenter() {
        mAreaModel = new AreaModel();
    }

    @Override
    public void requestAreaData(String topid) {
        Disposable disposable = Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            String key = topid + "_area";
            boolean hasCache = CacheManager.getImpl().hasCache(key);
            emitter.onNext(hasCache);
        }).filter(aBoolean -> {
            if (aBoolean) {
                String key = topid + "_area";
                String json = CacheManager.getImpl().findCache(key);
                List<MusicInfo> musicInfoList = new Gson().fromJson(json, new TypeToken<List<MusicInfo>>() {
                }.getType());
                mView.loadAreaDataSuccess(musicInfoList);
                return false;
            }
            return true;
        }).flatMap(aBoolean -> mAreaModel.requestAreaData(topid))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(infoList -> {
                    mView.loadAreaDataSuccess(infoList);
                    updateCache(topid);
                }, throwable -> {
                    LogUtil.i("-requestAreaData->" + throwable.getMessage());
                });
        addSubscribe(disposable);
    }

    private void updateCache(String topid) {
        long currTime = SpUtil.getInstance().getLong("cache_area_data_" + topid, System.currentTimeMillis());
        if (System.currentTimeMillis() - currTime > 24 * 60 * 60 * 1000) {
            SpUtil.getInstance().putLong("cache_area_data_" + topid, System.currentTimeMillis());
            mAreaModel.requestAreaData(topid)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(infoList -> {
                        LogUtil.i("地区列表更新缓存成功");
                    }, throwable -> {
                        LogUtil.i("-->" + throwable.getMessage());
                    });
        }
    }
}

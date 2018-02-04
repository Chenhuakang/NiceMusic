package com.lzx.nicemusic.module.main.presenter;

import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzx.musiclibrary.aidl.model.MusicInfo;
import com.lzx.musiclibrary.utils.LogUtil;
import com.lzx.nicemusic.base.mvp.factory.BasePresenter;
import com.lzx.nicemusic.db.CacheManager;
import com.lzx.nicemusic.helper.DataHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
        .map(json -> {
            List<MusicInfo> infoList = new Gson().fromJson(json, new TypeToken<List<MusicInfo>>() {
            }.getType());
            ConcurrentMap<String, List<MusicInfo>> musicListByType = new ConcurrentHashMap<>();
            for (MusicInfo info : infoList) {
                if (!musicListByType.containsKey(info.musicTitle)) {
                    musicListByType.put(info.musicType, DataHelper.getMusicByType(infoList, info.musicType));
                }
            }
            return musicListByType;
        })
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(homeInfos -> {
                    Map<String, String> types = new HashMap<>();
                    types.put("1", "新歌榜");
                    types.put("2", "热歌榜");
                    types.put("11", "摇滚榜");
                    types.put("12", "爵士");
                    types.put("16", "流行");
                    types.put("21", "欧美金曲榜");
                    types.put("22", "经典老歌榜");
                    types.put("23", "情歌对唱榜");
                    types.put("24", "影视金曲榜");
                    types.put("25", "网络歌曲榜");
                    //排序
                    List<Map.Entry<String, String>> list = new ArrayList<>(types.entrySet());
                    Collections.sort(list, (mapping1, mapping2) -> mapping1.getKey().compareTo(mapping2.getKey()));

                    mView.requestMainDataSuccess(homeInfos, list);
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
        //  long currTime = SpUtil.getInstance().getLong("cache_main_data", System.currentTimeMillis());
        //  if (System.currentTimeMillis() - currTime > 24 * 60 * 60 * 1000) {
        //      SpUtil.getInstance().putLong("cache_main_data", System.currentTimeMillis());
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
        //  }
    }


}


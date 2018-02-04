package com.lzx.nicemusic.module.search.presenter;


import com.google.gson.Gson;
import com.lzx.musiclibrary.aidl.model.MusicInfo;
import com.lzx.musiclibrary.utils.LogUtil;
import com.lzx.nicemusic.base.mvp.factory.BasePresenter;
import com.lzx.nicemusic.db.SearchManager;
import com.lzx.nicemusic.network.RetrofitHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xian on 2018/1/14.
 */

public class SearchPresenter extends BasePresenter<SearchContract.View> implements SearchContract.Presenter<SearchContract.View> {


    @Override
    public void requestDefaultSearchData() {
        Disposable disposable = Observable.create((ObservableOnSubscribe<List<String>>) emitter -> {
            List<String> historys = SearchManager.getImpl().findHistorys();
            emitter.onNext(historys);
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(strings -> {
                    List<String> hotSearch = new ArrayList<>();
                    hotSearch.add("告白气球");
                    hotSearch.add("空空如也");
                    hotSearch.add("我们不一样");
                    hotSearch.add("追光者");
                    hotSearch.add("浮夸");
                    hotSearch.add("演员");
                    mView.loadDefaultSearchDataSuccess(hotSearch, strings);
                }, throwable -> {
                    LogUtil.i("-->" + throwable.getMessage());
                });
        addSubscribe(disposable);
    }

    @Override
    public void addHistory(String title) {
        Disposable disposable = Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            SearchManager.getImpl().saveHistory(title);
            emitter.onNext(true);
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    LogUtil.i("-->添加搜索历史成功");
                }, throwable -> {
                    LogUtil.i("-->" + throwable.getMessage());
                });
        addSubscribe(disposable);
    }

    @Override
    public void deleteHistory(String title) {
        Disposable disposable = Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            SearchManager.getImpl().deleteHistory(title);
            emitter.onNext(true);
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    mView.deleteHistorySuccess();
                }, throwable -> {
                    LogUtil.i("-->" + throwable.getMessage());
                });
        addSubscribe(disposable);
    }

    @Override
    public void searchMusic(String keyword) {
        Disposable disposable = RetrofitHelper.getMusicApi().searchMusic(keyword)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONArray array = jsonObject.getJSONObject("showapi_res_body")
                            .getJSONObject("pagebean").getJSONArray("contentlist");
                    List<MusicInfo> list = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        MusicInfo info = new Gson().fromJson(object.toString(), MusicInfo.class);
                        list.add(info);
                    }
                    return list;
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(infoList -> mView.searchSuccess(infoList),
                        throwable -> {
                            LogUtil.i("-->" + throwable.getMessage());
                        });
        addSubscribe(disposable);
    }


}

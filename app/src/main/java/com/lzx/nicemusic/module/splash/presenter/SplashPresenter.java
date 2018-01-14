package com.lzx.nicemusic.module.splash.presenter;

import com.google.gson.Gson;
import com.lzx.nicemusic.base.mvp.factory.BasePresenter;
import com.lzx.nicemusic.bean.BannerInfo;
import com.lzx.nicemusic.bean.HomeInfo;
import com.lzx.nicemusic.db.CacheManager;
import com.lzx.nicemusic.module.main.presenter.MainModel;
import com.lzx.nicemusic.network.RetrofitHelper;
import com.lzx.nicemusic.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by xian on 2018/1/14.
 */

public class SplashPresenter extends BasePresenter<SplashContract.View> implements SplashContract.Presenter<SplashContract.View> {

    private MainModel mMainModel;

    public SplashPresenter() {
        mMainModel = new MainModel();
    }

    @Override
    public void requestMusicList() {
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            boolean hasCache = CacheManager.getImpl().hasCache(CacheManager.KEY_HOME_LIST_DATA);
            emitter.onNext(hasCache);
        }).filter(aBoolean -> {
            if (aBoolean) {
                mView.requestMainDataSuccess(true);
                return false;
            }
            return true;
        })
                .flatMap(aBoolean -> mMainModel.loadMainData())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mainDataList1 -> mView.requestMainDataSuccess(false),
                        throwable -> LogUtil.i("-->" + throwable.getMessage()));
    }
}

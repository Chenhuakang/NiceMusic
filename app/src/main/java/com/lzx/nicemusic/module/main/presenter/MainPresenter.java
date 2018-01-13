package com.lzx.nicemusic.module.main.presenter;

import com.lzx.nicemusic.base.mvp.factory.BasePresenter;
import com.lzx.nicemusic.bean.BannerInfo;
import com.lzx.nicemusic.network.RetrofitHelper;
import com.lzx.nicemusic.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by xian on 2018/1/13.
 */

public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter<MainContract.View> {
    @Override
    public void requestBanner() {
        RetrofitHelper.getNewsApi().requestBanner()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONObject body = jsonObject.getJSONObject("showapi_res_body");
                    List<BannerInfo> list = new ArrayList<>();
                    for (int i = 0; i < 5; i++) {
                        JSONObject object = body.getJSONObject(String.valueOf(i));
                        BannerInfo info = new BannerInfo();
                        info.setThumb(object.getString("thumb"));
                        info.setTitle(object.getString("title"));
                        info.setUrl(object.getString("url"));
                        list.add(info);
                    }
                    mView.requestBannerSuccess(list);
                });
    }
}

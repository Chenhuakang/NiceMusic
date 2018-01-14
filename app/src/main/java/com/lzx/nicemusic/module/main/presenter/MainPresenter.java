package com.lzx.nicemusic.module.main.presenter;

import com.google.gson.Gson;
import com.lzx.nicemusic.base.mvp.factory.BasePresenter;
import com.lzx.nicemusic.bean.BaiSiBuDeJieMusic;
import com.lzx.nicemusic.bean.BannerInfo;
import com.lzx.nicemusic.bean.HomeInfo;
import com.lzx.nicemusic.bean.MainDataList;
import com.lzx.nicemusic.bean.MusicInfo;
import com.lzx.nicemusic.network.RetrofitHelper;
import com.lzx.nicemusic.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by xian on 2018/1/13.
 */

public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter<MainContract.View> {

    @Override
    public void requestMusicList() {
        List<HomeInfo> homeInfos = new ArrayList<>();
        RetrofitHelper.getNewsApi().requestBanner()
                .flatMap(responseBody -> {
                    HomeInfo homeInfo = new HomeInfo();
                    homeInfo.setBannerList(getBannerList(responseBody, 5, "Banner", HomeInfo.TYPE_ITEM_BANNER));
                    homeInfos.add(homeInfo);
                    return RetrofitHelper.getMusicApi().requestMusicList("26");//热歌;
                })
                .flatMap(responseBody -> {
                    homeInfos.add(new HomeInfo("流行熱歌", HomeInfo.TYPE_ITEM_TITLE));
                    homeInfos.addAll(getMusicList(responseBody, 6, "流行熱歌", HomeInfo.TYPE_ITEM_THREE));
                    return RetrofitHelper.getMusicApi().requestMusicList("27"); //新歌
                })
                .flatMap(responseBody -> {
                    homeInfos.add(new HomeInfo("新歌榜", HomeInfo.TYPE_ITEM_TITLE));
                    homeInfos.addAll(getMusicList(responseBody, 5, "新歌榜", HomeInfo.TYPE_ITEM_ONE));
                    return RetrofitHelper.getBaiSiBuDeJieApi().requestSong("31"); //百思不得姐音乐
                })
                .flatMap(responseBody -> {
                    homeInfos.add(new HomeInfo("随心听", HomeInfo.TYPE_ITEM_TITLE));
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONArray array = jsonObject.getJSONObject("showapi_res_body")
                            .getJSONObject("pagebean").getJSONArray("contentlist");
                    for (int i = 0; i < 4; i++) {
                        HomeInfo baiSiBuDeJieMusic = new Gson().fromJson(array.getJSONObject(i).toString(), HomeInfo.class);
                        baiSiBuDeJieMusic.setItemTitle("随心听");
                        baiSiBuDeJieMusic.setItemType(HomeInfo.TYPE_ITEM_TWO);
                        homeInfos.add(baiSiBuDeJieMusic);
                    }
                    return RetrofitHelper.getNewsApi().requestWelfare(38, 3); //大长腿
                })
                .flatMap(responseBody -> {
                    HomeInfo homeInfo = new HomeInfo();
                    homeInfo.setLongLegs(getBannerList(responseBody, 3, "大长腿", HomeInfo.TYPE_ITEM_LONGLEGS));
                    homeInfo.setItemType(HomeInfo.TYPE_ITEM_LONGLEGS);
                    homeInfos.add(homeInfo);
                    return RetrofitHelper.getMusicApi().requestMusicList("36"); //K歌金曲
                })
                .flatMap(responseBody -> {
                    homeInfos.add(new HomeInfo("K歌金曲", HomeInfo.TYPE_ITEM_TITLE));
                    homeInfos.addAll(getMusicList(responseBody, 6, "K歌金曲", HomeInfo.TYPE_ITEM_THREE));
                    return RetrofitHelper.getMusicApi().requestMusicList("28"); //网络歌曲
                })
                .flatMap(responseBody -> {
                    homeInfos.add(new HomeInfo("网络歌曲", HomeInfo.TYPE_ITEM_TITLE));
                    homeInfos.addAll(getMusicList(responseBody, 6, "网络歌曲", HomeInfo.TYPE_ITEM_THREE));
                    return RetrofitHelper.getNewsApi().requestWelfare(36, 1); //文艺范
                })
                .map(responseBody -> {
                    HomeInfo homeInfo = new HomeInfo();
                    homeInfo.setArtGirl(getBannerList(responseBody, 1, "文艺范", HomeInfo.TYPE_ITEM_ARTS).get(0));
                    homeInfo.setItemType(HomeInfo.TYPE_ITEM_ARTS);
                    homeInfos.add(homeInfo);
                    return homeInfos;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mainDataList1 -> mView.requestMainDataSuccess(mainDataList1),
                        throwable -> LogUtil.i("-->" + throwable.getMessage()));
    }

    private List<HomeInfo> getMusicList(ResponseBody responseBody, int num, String itemTitle, int itemType) throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject(responseBody.string());
        JSONArray array = jsonObject.getJSONObject("showapi_res_body")
                .getJSONObject("pagebean").getJSONArray("songlist");
        List<HomeInfo> musicInfoList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            HomeInfo musicInfo = new Gson().fromJson(array.getJSONObject(i).toString(), HomeInfo.class);
            musicInfo.setItemTitle(itemTitle);
            musicInfo.setItemType(itemType);
            musicInfoList.add(musicInfo);
        }
        return musicInfoList;
    }

    private List<BannerInfo> getBannerList(ResponseBody responseBody, int num, String itemTitle, int itemType) throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject(responseBody.string());
        JSONObject body = jsonObject.getJSONObject("showapi_res_body");
        List<BannerInfo> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            JSONObject object = body.getJSONObject(String.valueOf(i));
            BannerInfo info = new BannerInfo();
            info.setThumb(object.getString("thumb"));
            info.setTitle(object.getString("title"));
            info.setUrl(object.getString("url"));
            info.setItemTitle(itemTitle);
            info.setItemType(itemType);


            list.add(info);
        }
        return list;
    }
}


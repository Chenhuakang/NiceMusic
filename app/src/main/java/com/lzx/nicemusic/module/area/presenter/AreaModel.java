package com.lzx.nicemusic.module.area.presenter;


import com.google.gson.Gson;
import com.lzx.nicemusic.bean.HomeInfo;
import com.lzx.nicemusic.bean.MusicInfo;
import com.lzx.nicemusic.db.CacheManager;
import com.lzx.nicemusic.network.RetrofitHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Created by xian on 2018/1/15.
 */

public class AreaModel {

    public Observable<List<MusicInfo>> requestAreaData(String topid) {
        return RetrofitHelper.getMusicApi().requestMusicList(topid)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONArray array = jsonObject.getJSONObject("showapi_res_body")
                            .getJSONObject("pagebean").getJSONArray("songlist");
                    List<MusicInfo> musicInfoList = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        MusicInfo musicInfo = new Gson().fromJson(array.getJSONObject(i).toString(), MusicInfo.class);
                        musicInfoList.add(musicInfo);
                    }
                    String json = new Gson().toJson(musicInfoList);
                    String key = topid + "_area";
                    CacheManager.getImpl().saveCache(key, json);
                    return musicInfoList;
                });
    }

}

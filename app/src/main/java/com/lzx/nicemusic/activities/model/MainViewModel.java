package com.lzx.nicemusic.activities.model;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzx.nicemusic.bean.BannerInfo;
import com.lzx.nicemusic.bean.HighQuality;
import com.lzx.nicemusic.bean.Personalized;
import com.lzx.nicemusic.network.MediaApi;
import com.lzx.nicemusic.network.RetrofitHelper;
import com.lzx.starrysky.model.SongInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class MainViewModel extends ViewModel {

    private MediaApi mMediaApi;
    private Context mContext;
    private String before;

    public MainViewModel(Context context) {
        mMediaApi = RetrofitHelper.Companion.get().createApi(MediaApi.class);
        mContext = context;
    }

    public Observable<List<HighQuality>> requestHighquality(boolean isLoadMore) {
        Map<String, String> map = new HashMap<>();
        map.put("limit", "50");
        if (isLoadMore) {
            map.put("before", before);
        }
        return mMediaApi.requestHighQuality(map)
                .map((Function<ResponseBody, List<HighQuality>>) responseBody -> {
                    String json = responseBody.string();
                    JSONArray jsonArray = new JSONObject(json).getJSONArray("playlists");
                    return new Gson().fromJson(jsonArray.toString(), new TypeToken<List<HighQuality>>() {
                    }.getType());
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<SongInfo>> requestPlayListDetail(String id) {
        return mMediaApi.requestPlayListDetail(id)
                .map(responseBody -> {
                    String json = responseBody.string();
                    JSONObject jsonObject = new JSONObject(json).getJSONObject("result");
                    JSONObject creator = jsonObject.getJSONObject("creator");
                    JSONArray tracks = jsonObject.getJSONArray("tracks");
                    List<SongInfo> list = new ArrayList<>();
                    for (int i = 0; i < tracks.length(); i++) {
                        JSONObject object = tracks.getJSONObject(i);
                        SongInfo songInfo = new SongInfo();
                        songInfo.setAlbumName(jsonObject.optString("name"));
                        songInfo.setAlbumArtist(creator.optString("nickname"));
                        songInfo.setSongCover(object.optJSONObject("album").optString("picUrl"));
                        songInfo.setSongId(object.optString("id"));
                        songInfo.setSongName(object.optString("name"));
                        songInfo.setDuration(object.optLong("duration"));
                        if (object.optJSONArray("artists").length() > 0) {
                            songInfo.setArtist(object.optJSONArray("artists").optJSONObject(0).optString("name"));
                        }
                        songInfo.setSongUrl("http://music.163.com/song/media/outer/url?id=" + songInfo.getSongId() + ".mp3");
                        list.add(songInfo);
                    }
                    return list;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Personalized>> requestPersonalized() {
        return mMediaApi.requestPersonalized()
                .map(responseBody -> {
                    String json = responseBody.string();
                    JSONArray jsonArray = new JSONObject(json).getJSONArray("result");
                    List<Personalized> list = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Personalized personalized = new Personalized();
                        personalized.setId(object.optString("id"));
                        personalized.setName(object.optString("name"));
                        personalized.setCopywriter(object.optString("copywriter"));
                        personalized.setPicUrl(object.optString("picUrl"));
                        list.add(personalized);
                    }
                    return list;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<List<BannerInfo>> getBanner() {
        return mMediaApi.requestBanner()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private Context context;

        public Factory(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new MainViewModel(context);
        }
    }

}

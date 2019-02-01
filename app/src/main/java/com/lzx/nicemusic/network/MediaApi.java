package com.lzx.nicemusic.network;

import com.lzx.nicemusic.bean.BannerInfo;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface MediaApi {

    //https://music.163.com/api/playlist/highquality/list
    String basApi = "https://music.163.com/api/";

    @GET("playlist/highquality/list")
    Observable<ResponseBody> requestHighQuality(@QueryMap Map<String, String> map);

    @GET("v2/banner/get?clientType=pc")
    Observable<List<BannerInfo>> requestBanner();

    @GET("playlist/detail")
    Observable<ResponseBody> requestPlayListDetail(@Query("id") String id);

    @GET("https://music.163.com/api/personalized/playlist")
    Observable<ResponseBody> requestPersonalized();
}

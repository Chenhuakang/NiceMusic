package com.lzx.nicemusic.network;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by xian on 2018/1/13.
 */

public interface MusicApi {

    @GET("213-4?showapi_appid=22640&showapi_sign=0676cf5617eb46f1a6da7bcf7853f423")
    Observable<ResponseBody> requestMusicList(@Query("topid") String topid);

    @GET("213-1?showapi_appid=22640&showapi_sign=0676cf5617eb46f1a6da7bcf7853f423")
    Observable<ResponseBody> searchMusic(@Query("keyword") String keyword);

}

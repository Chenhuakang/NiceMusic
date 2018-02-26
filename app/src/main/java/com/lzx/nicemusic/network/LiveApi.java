package com.lzx.nicemusic.network;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * @author lzx
 * @date 2018/2/26
 */

public interface LiveApi {
    @GET("live/radios")
    Observable<ResponseBody> getLiveList(@QueryMap Map<String, String> map);
}

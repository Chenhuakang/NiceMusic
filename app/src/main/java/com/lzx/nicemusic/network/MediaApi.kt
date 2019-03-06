package com.lzx.nicemusic.network

import com.lzx.nicemusic.bean.BannerInfo
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface MediaApi {

    @GET("playlist/highquality/list")
    fun requestHighQuality(@QueryMap map: Map<String, String>): Observable<ResponseBody>

    @GET("v2/banner/get?clientType=pc")
    fun requestBanner(): Observable<List<BannerInfo>>

    @GET("playlist/detail")
    fun requestPlayListDetail(@Query("id") id: String): Observable<ResponseBody>

    @GET("https://music.163.com/api/personalized/playlist")
    fun requestPersonalized(): Observable<ResponseBody>
}
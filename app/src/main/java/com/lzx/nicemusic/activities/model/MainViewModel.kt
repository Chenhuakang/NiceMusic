package com.lzx.nicemusic.activities.model

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lzx.nicemusic.bean.BannerInfo
import com.lzx.nicemusic.bean.HighQuality
import com.lzx.nicemusic.bean.Personalized
import com.lzx.nicemusic.network.MediaApi
import com.lzx.nicemusic.network.RetrofitHelper
import com.lzx.starrysky.model.SongInfo

import org.json.JSONObject

import java.util.ArrayList
import java.util.HashMap

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody

class MainViewModel : ViewModel() {

    private val mMediaApi: MediaApi = RetrofitHelper.get()!!.createApi(MediaApi::class.java)
    private val before: String? = null


    val banner: Observable<List<BannerInfo>>
        get() = mMediaApi.requestBanner()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

    fun requestHighquality(isLoadMore: Boolean): Observable<Function<ResponseBody, List<HighQuality>>>? {
        val map = HashMap<String, String>()
        map["limit"] = "50"
        if (isLoadMore) {
            map["before"] = before!!
        }
        return mMediaApi.requestHighQuality(map)
                .map {
                    return@map Function<ResponseBody, List<HighQuality>> {
                        val json = it.string()
                        val jsonArray = JSONObject(json).getJSONArray("playlists")
                        Gson().fromJson<List<HighQuality>>(jsonArray.toString(), object : TypeToken<List<HighQuality>>() {
                        }.type)
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun requestPlayListDetail(id: String): Observable<List<SongInfo>> {
        return mMediaApi.requestPlayListDetail(id)
                .map<List<SongInfo>> { responseBody ->
                    val json = responseBody.string()
                    val jsonObject = JSONObject(json).getJSONObject("result")
                    val creator = jsonObject.getJSONObject("creator")
                    val tracks = jsonObject.getJSONArray("tracks")
                    val list = ArrayList<SongInfo>()
                    for (i in 0 until tracks.length()) {
                        val `object` = tracks.getJSONObject(i)
                        val songInfo = SongInfo()
                        songInfo.albumName = jsonObject.optString("name")
                        songInfo.albumArtist = creator.optString("nickname")
                        songInfo.songCover = `object`.optJSONObject("album").optString("picUrl")
                        songInfo.songId = `object`.optString("id")
                        songInfo.songName = `object`.optString("name")
                        songInfo.duration = `object`.optLong("duration")
                        if (`object`.optJSONArray("artists").length() > 0) {
                            songInfo.artist = `object`.optJSONArray("artists").optJSONObject(0).optString("name")
                        }
                        songInfo.songUrl = "http://music.163.com/song/media/outer/url?id=" + songInfo.songId + ".mp3"
                        list.add(songInfo)
                    }
                    list
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun requestPersonalized(): Observable<List<Personalized>> {
        return mMediaApi.requestPersonalized()
                .map<List<Personalized>> { responseBody ->
                    val json = responseBody.string()
                    val jsonArray = JSONObject(json).getJSONArray("result")
                    val list = ArrayList<Personalized>()
                    for (i in 0 until jsonArray.length()) {
                        val `object` = jsonArray.getJSONObject(i)
                        val personalized = Personalized()
                        personalized.id = `object`.optString("id")
                        personalized.name = `object`.optString("name")
                        personalized.copywriter = `object`.optString("copywriter")
                        personalized.picUrl = `object`.optString("picUrl")
                        list.add(personalized)
                    }
                    list
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    class Factory : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel() as T
        }
    }

}

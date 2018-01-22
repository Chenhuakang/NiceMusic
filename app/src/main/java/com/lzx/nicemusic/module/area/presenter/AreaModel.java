package com.lzx.nicemusic.module.area.presenter;


import com.lzx.musiclibrary.bean.MusicInfo;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by xian on 2018/1/15.
 */

public class AreaModel {

    public Observable<List<MusicInfo>> requestAreaData(String topid) {
        return null;
//        return RetrofitHelper.getMusicApi().requestMusicList(topid)
//                .map(responseBody -> {
//                    JSONObject jsonObject = new JSONObject(responseBody.string());
//                    JSONArray array = jsonObject.getJSONObject("showapi_res_body")
//                            .getJSONObject("pagebean").getJSONArray("songlist");
//                    List<MusicInfo> musicInfoList = new ArrayList<>();
//                    for (int i = 0; i < array.length(); i++) {
//                        MusicInfo musicInfo = new MusicInfo();
//                        JSONObject object = array.getJSONObject(i);
//                        musicInfo.musicId = object.getString("songid"); //音乐id
//                        musicInfo.musicTitle = object.getString("songname"); //音乐标题
//                        musicInfo.musicCover = object.getString("albumpic_big"); //音乐封面
//                        musicInfo.musicUrl = "http://zhangmenshiting.qianqian.com/data2/music/fd75dd310d9c4f759ba376145a5e2aa9/540319732/540319732.mp3?xcode=09e19f280c4b9b61e4d6351b3c1b445d"; //音乐播放地址
//                        musicInfo.musicGenre = DataHelper.getMusicGenreByTopid(topid); //类型（流派）
//                        musicInfo.musicType = "popular"; //类型
//                        musicInfo.musicSize = "0"; //音乐大小
//                        musicInfo.musicDuration = object.getInt("seconds") * 1000; //音乐长度
//                        musicInfo.musicArtist = object.getString("singername"); //音乐艺术家
//                        musicInfo.musicDownloadUrl = object.getString("downUrl"); //音乐下载地址
//                        musicInfo.musicSite = ""; //地点
//                        musicInfo.favorites = Integer.parseInt(object.getString("songid")); //喜欢数
//                        musicInfo.playCount = object.getInt("singerid"); //播放数
//                        musicInfo.trackNumber = i + 1; //媒体的曲目号码（序号：1234567……）
//
//                        musicInfo.albumId = object.getString("albumid"); //专辑id
//                        musicInfo.albumTitle = object.getString("songname"); //专辑名称
//                        musicInfo.albumCover = object.getString("albumpic_big"); //专辑封面
//                        musicInfo.albumArtist = object.getString("singername"); //专辑艺术家
//                        musicInfo.albumMusicCount = array.length(); //专辑音乐数
//                        musicInfo.albumPlayCount = Integer.parseInt(object.getString("songid")); //专辑播放数
//                        musicInfoList.add(musicInfo);
//                    }
//                    String json = new Gson().toJson(musicInfoList);
//                    String key = topid + "_area";
//                    CacheManager.getImpl().saveCache(key, json);
//                    return musicInfoList;
//                });
    }

}

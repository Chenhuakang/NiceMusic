package com.lzx.nicemusic.helper;

import android.graphics.Bitmap;

import com.lzx.nicemusic.lib.bean.MusicInfo;
import com.lzx.nicemusic.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * Created by xian on 2018/1/20.
 */

public class DataHelper {
    /**
     * 获取流派
     *
     * @param topid
     * @return
     */
    public static String getMusicGenreByTopid(String topid) {
        switch (topid) {
            case "16":
                return "韩国";
            case "5":
                return "内地";
            case "6":
                return "港台";
            case "3":
                return "欧美";
            case "17":
                return "日本";
            default:
                return "Genre";
        }
    }

    /**
     * 得到一个随机列表
     *
     * @return
     */
    public static List<MusicInfo> getShuffleMusicList(List<MusicInfo> list, int size) {
        Collections.shuffle(list);
        List<MusicInfo> results = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            results.add(list.get(i));
        }
        return results;
    }

    /**
     * 解析json
     */
    public static List<MusicInfo> fetchJSONFromUrl(ResponseBody responseBody) throws IOException, JSONException {
        List<MusicInfo> musicInfos = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(responseBody.string());
        JSONArray array = jsonObject.getJSONArray("song_list");
        JSONObject billboard = jsonObject.getJSONObject("billboard");
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            MusicInfo info = new MusicInfo();
            info.musicId = object.getString("song_id"); //音乐id
            info.musicTitle = object.getString("title"); //音乐标题
            info.musicCover = object.getString("pic_big"); //音乐封面
            info.musicUrl = ""; //音乐播放地址
            info.musicGenre = billboard.getString("name"); //类型（流派）
            info.musicType = billboard.getString("billboard_type"); //类型
            info.musicSize = ""; //音乐大小
            info.musicDuration = object.getInt("file_duration") * 1000; //音乐长度
            info.musicArtist = object.getString("author"); //音乐艺术家
            info.artistId = object.getString("ting_uid"); //音乐艺术家id
            info.musicDownloadUrl = ""; //音乐下载地址
            info.musicSite = object.getString("country"); //地点
            info.favorites = Integer.parseInt(object.optString("hot", "0")); //喜欢数
            info.playCount = info.favorites * 2; //播放数
            info.trackNumber = i; //媒体的曲目号码（序号：1234567……）

            info.language = object.getString("language");//语言
            info.country = object.getString("country"); //地区
            info.proxyCompany = object.getString("si_proxycompany");//代理公司
            info.publishTime = object.getString("publishtime");//发布时间
            info.musicInfo = object.getString("info"); //音乐描述
            info.versions = object.getString("versions"); //版本

            info.albumId = object.getString("album_id"); //专辑id
            info.albumTitle = object.getString("album_title"); //专辑名称
            info.albumCover = object.getString("album_500_500"); //专辑封面
            info.temp_1 = object.getString("pic_huge"); //长方形的封面
            info.temp_2 = object.getString("pic_premium"); //高清的封面
            info.albumArtist = object.getString("artist_name"); //专辑艺术家
            info.albumMusicCount = 0; //专辑音乐数
            info.albumPlayCount = 0; //专辑播放数
            musicInfos.add(info);
        }
        return musicInfos;
    }

    public static List<MusicInfo> subList(List<MusicInfo> list, int index, int size) {
        List<MusicInfo> musicInfos = new ArrayList<>();
        for (int i = index; i < size; i++) {
            musicInfos.add(list.get(i));
            size = size + i * size;
        }
        return musicInfos;
    }

    public static List<MusicInfo> getMusicByType(List<MusicInfo> list, String type) {
        List<MusicInfo> musicInfos = new ArrayList<>();
        for (MusicInfo info : list) {
            if (info.musicType.equals(type)) {
                musicInfos.add(info);
            }
        }
        return musicInfos;
    }

}

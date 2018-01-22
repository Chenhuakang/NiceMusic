package com.lzx.nicemusic.helper;

import android.content.Context;
import android.widget.Toast;

import com.lzx.nicemusic.lib.bean.MusicInfo;
import com.lzx.nicemusic.network.RetrofitHelper;

import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author lzx
 * @date 2018/1/22
 */

public class PlayHelper {

    public void playMusic(Context context, MusicInfo musicInfo) {
        RetrofitHelper.getMusicApi().playMusic(musicInfo.musicId)
                .map(responseBody -> {
                    String json = responseBody.string();
                    json = json.substring(1, json.length() - 2);
                    JSONObject jsonObject = new JSONObject(json);
                    JSONObject bitrate = jsonObject.getJSONObject("bitrate");
                    return bitrate.getString("file_link");
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String file_link) throws Exception {
                        musicInfo.musicUrl = file_link;

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(context, "播放失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}

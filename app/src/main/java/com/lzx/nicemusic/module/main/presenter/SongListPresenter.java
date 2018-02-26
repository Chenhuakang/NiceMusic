package com.lzx.nicemusic.module.main.presenter;

import android.widget.Toast;

import com.lzx.musiclibrary.aidl.model.AlbumInfo;
import com.lzx.musiclibrary.aidl.model.SongInfo;
import com.lzx.musiclibrary.utils.LogUtil;
import com.lzx.nicemusic.base.mvp.factory.BasePresenter;
import com.lzx.nicemusic.helper.DataHelper;
import com.lzx.nicemusic.network.RetrofitHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author lzx
 * @date 2018/2/5
 */

public class SongListPresenter extends BasePresenter<SongListContract.View> implements SongListContract.Presenter<SongListContract.View> {

    public int size = 10;
    private int offset = 0;
    private boolean isMore;

    @Override
    public void requestSongList(String title) {
        Disposable subscriber = getSongList(title)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    isMore = list.size() >= size;
                    mView.onGetSongListSuccess(list, title);
                }, throwable -> {
                    LogUtil.i("error = " + throwable.getMessage());
                    Toast.makeText(mContext, "获取数据失败", Toast.LENGTH_SHORT).show();
                });
        addSubscribe(subscriber);
    }

    @Override
    public void requestLiveList(String title) {
        Map<String, String> map = new HashMap<>();
        map.put("access_token", "1ee5f68cae68705cd636c5fc82c038eb");
        map.put("app_key", "b617866c20482d133d5de66fceb37da3");
        map.put("client_os_type", "2");
        map.put("device_id", "64b0e91456f5d3e9");
        map.put("pack_id", "com.app.test.android");
        map.put("province_code", "360000");
        map.put("radio_type", "2");
        map.put("sdk_version", "v1.0.1.9");
        map.put("sig", "a84861e2e472b62e1c35c54eca78a6f2");
        RetrofitHelper.getLiveApi().getLiveList(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONArray array = jsonObject.getJSONArray("radios");
                    List<SongInfo> musicInfos = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        SongInfo info = new SongInfo();
                        info.setSongId(object.getString("id")); //音乐id
                        info.setSongName(object.getString("radio_name")); //音乐标题
                        info.setSongCover(object.getString("cover_url_large"));  //音乐封面
                        info.setSongUrl(object.getString("rate24_aac_url")); //音乐播放地址
                        info.setGenre(object.getString("kind"));  //类型（流派）
                        info.setType(object.getString("kind"));  //类型
                        info.setSize("");   //音乐大小
                        info.setDuration(0);   //音乐长度
                        info.setArtist(object.getString("radio_name"));  //音乐艺术家
                        info.setTrackNumber(i);   //媒体的曲目号码（序号：1234567……）

                        AlbumInfo albumInfo = new AlbumInfo();
                        albumInfo.setAlbumId(object.getString("id")); //专辑id
                        albumInfo.setAlbumName(object.getString("program_name"));   //专辑名称
                        albumInfo.setAlbumCover(object.getString("cover_url_large"));  //专辑封面
                        albumInfo.setSongCount(0);   //专辑音乐数
                        albumInfo.setPlayCount(0);   //专辑播放数

                        info.setAlbumInfo(albumInfo);
                        musicInfos.add(info);
                    }
                    return musicInfos;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    mView.onGetSongListSuccess(list, title);
                }, throwable -> {
                    LogUtil.i("直播 = " + throwable.getMessage());
                    Toast.makeText(mContext, "获取直播数据失败", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void loadMoreSongList(String title) {
        if (isMore) {
            offset += 10;
            Disposable subscriber = getSongList(title)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(list -> {
                        isMore = list.size() >= size;
                        mView.loadMoreSongListSuccess(list, title);
                    }, throwable -> {
                        LogUtil.i("error = " + throwable.getMessage());
                    });
            addSubscribe(subscriber);
        } else {
            mView.loadFinishAllData();
        }
    }

    private Observable<List<SongInfo>> getSongList(String title) {
        int type = getListType(title);
        return RetrofitHelper.getMusicApi().requestMusicList(type, size, offset)
                .map(responseBody -> {
                    List<SongInfo> list = DataHelper.fetchJSONFromUrl(responseBody);
                    List<SongInfo> newList = new ArrayList<>();
                    for (SongInfo info : list) {
                        RetrofitHelper.getMusicApi().playMusic(info.getSongId())
                                .map(responseUrlBody -> {
                                    String json = responseUrlBody.string();
                                    json = json.substring(1, json.length() - 2);
                                    JSONObject jsonObject = new JSONObject(json);
                                    JSONObject bitrate = jsonObject.getJSONObject("bitrate");
                                    return bitrate.getString("file_link");
                                })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(url -> {
                                    info.setSongUrl(url);
                                    newList.add(info);
                                }, throwable -> {
                                    LogUtil.i("1error = " + throwable.getMessage());
                                });
                    }
                    return newList;
                });
    }


    @Override
    public int getAlbumCover(String title) {
        return -1;
    }

    private int getListTypeIndex(String title) {
        int index = 0;
        switch (title) {
            case "我的歌单":
                index = 0;
                break;
            case "新歌榜":
                index = 1;
                break;
            case "热歌榜":
                index = 2;
                break;
            case "摇滚榜":
                index = 3;
                break;
            case "爵士":
                index = 4;
                break;
            case "流行":
                index = 5;
                break;
            case "欧美金曲榜":
                index = 6;
                break;
            case "经典老歌榜":
                index = 7;
                break;
            case "情歌对唱榜":
                index = 8;
                break;
            case "影视金曲榜":
                index = 9;
                break;
            case "网络歌曲榜":
                index = 10;
                break;
        }
        return index;
    }

    private int getListType(String title) {
        int type = 0;
        switch (title) {
            case "新歌榜":
                type = 1;
                break;
            case "热歌榜":
                type = 2;
                break;
            case "摇滚榜":
                type = 11;
                break;
            case "爵士":
                type = 12;
                break;
            case "流行":
                type = 16;
                break;
            case "欧美金曲榜":
                type = 21;
                break;
            case "经典老歌榜":
                type = 22;
                break;
            case "情歌对唱榜":
                type = 23;
                break;
            case "影视金曲榜":
                type = 24;
                break;
            case "网络歌曲榜":
                type = 25;
                break;
        }
        return type;
    }
}

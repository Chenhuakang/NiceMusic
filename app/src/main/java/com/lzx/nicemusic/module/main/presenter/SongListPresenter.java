package com.lzx.nicemusic.module.main.presenter;

import android.widget.Toast;

import com.lzx.musiclibrary.aidl.model.AlbumInfo;
import com.lzx.musiclibrary.aidl.model.SongInfo;
import com.lzx.musiclibrary.utils.LogUtil;
import com.lzx.nicemusic.base.mvp.factory.BasePresenter;
import com.lzx.nicemusic.helper.DataHelper;
import com.lzx.nicemusic.network.RetrofitHelper;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioList;

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
        int mRadioType = 2;
        int mProvinceCode = 360000;
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RADIOTYPE, "" + mRadioType);
        map.put(DTransferConstants.PROVINCECODE, "" + mProvinceCode);
        CommonRequest.getRadios(map, new IDataCallBack<RadioList>() {

            @Override
            public void onSuccess(RadioList object) {
                if (object != null && object.getRadios() != null) {
                    List<SongInfo> musicInfos = new ArrayList<>();
                    for (int i = 0; i < object.getRadios().size(); i++) {
                        Radio radio = object.getRadios().get(i);
                        SongInfo info = new SongInfo();
                        info.setSongId(String.valueOf(radio.getDataId())); //音乐id
                        info.setSongName(radio.getRadioName()); //音乐标题
                        info.setSongCover(radio.getCoverUrlLarge());  //音乐封面
                        info.setSongUrl(radio.getRate64AacUrl()); //音乐播放地址
                        info.setGenre(radio.getKind());  //类型（流派）
                        info.setType(radio.getKind());  //类型
                        info.setSize("");   //音乐大小
                        info.setDuration(0);   //音乐长度
                        info.setArtist(radio.getRadioName());  //音乐艺术家
                        info.setTrackNumber(i);   //媒体的曲目号码（序号：1234567……）

                        AlbumInfo albumInfo = new AlbumInfo();
                        albumInfo.setAlbumId(String.valueOf(radio.getDataId())); //专辑id
                        albumInfo.setAlbumName(radio.getProgramName());   //专辑名称
                        albumInfo.setAlbumCover(radio.getCoverUrlLarge());  //专辑封面
                        albumInfo.setSongCount(0);   //专辑音乐数
                        albumInfo.setPlayCount(0);   //专辑播放数

                        info.setAlbumInfo(albumInfo);
                        musicInfos.add(info);
                    }
                    mView.onGetSongListSuccess(musicInfos, title);
                    mView.onGetLiveSongSuccess(musicInfos);
                }
            }

            @Override
            public void onError(int code, String message) {
                LogUtil.i("直播 = " + message);
                Toast.makeText(mContext, "获取直播数据失败", Toast.LENGTH_SHORT).show();
            }
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

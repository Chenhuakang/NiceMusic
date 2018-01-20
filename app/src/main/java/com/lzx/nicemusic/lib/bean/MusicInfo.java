package com.lzx.nicemusic.lib.bean;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.media.MediaMetadataCompat;

/**
 * 统一音乐信息
 *
 * @author lzx
 * @date 2018/1/17
 */

public class MusicInfo implements Parcelable {
    public String musicId = ""; //音乐id
    public String musicTitle = ""; //音乐标题
    public String musicCover = ""; //音乐封面
    public Bitmap musicCoverBitmap;
    public String musicUrl = ""; //音乐播放地址
    public String musicGenre = ""; //类型（流派）
    public String musicType = ""; //类型
    public String musicSize = "0"; //音乐大小
    public long musicDuration = 0; //音乐长度
    public String musicArtist = ""; //音乐艺术家
    public String artistId = ""; //音乐艺术家id
    public String musicDownloadUrl = ""; //音乐下载地址
    public String musicSite = ""; //地点
    public int favorites = 0; //喜欢数
    public int playCount = 0; //播放数
    public int trackNumber = 0; //媒体的曲目号码（序号：1234567……）

    public String language = "";//语言
    public String country = ""; //地区
    public String proxyCompany = "";//代理公司
    public String publishTime = "";//发布时间
    public String musicInfo = ""; //音乐描述
    public String versions = ""; //版本

    public String albumId = ""; //专辑id
    public String albumTitle = ""; //专辑名称
    public String albumCover = ""; //专辑封面
    public Bitmap albumCoverBitmap;
    public String albumArtist = ""; //专辑艺术家
    public int albumMusicCount = 0; //专辑音乐数
    public int albumPlayCount = 0; //专辑播放数

    public MediaMetadataCompat metadataCompat; //媒体信息

    //提供9个临时字段：上面字段不够用的话，可以用临时字段
    public String temp_1 = ""; //临时字段
    public String temp_2 = ""; //临时字段
    public String temp_3 = ""; //临时字段
    public String temp_4 = ""; //临时字段
    public String temp_5 = ""; //临时字段
    public String temp_6 = ""; //临时字段
    public String temp_7 = ""; //临时字段
    public String temp_8 = ""; //临时字段
    public String temp_9 = ""; //临时字段

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MusicInfo) {
            return this.musicId.equals(((MusicInfo) obj).musicId);
        }
        return false;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.musicId);
        dest.writeString(this.musicTitle);
        dest.writeString(this.musicCover);
        dest.writeParcelable(this.musicCoverBitmap, flags);
        dest.writeString(this.musicUrl);
        dest.writeString(this.musicGenre);
        dest.writeString(this.musicType);
        dest.writeString(this.musicSize);
        dest.writeLong(this.musicDuration);
        dest.writeString(this.musicArtist);
        dest.writeString(this.artistId);
        dest.writeString(this.musicDownloadUrl);
        dest.writeString(this.musicSite);
        dest.writeInt(this.favorites);
        dest.writeInt(this.playCount);
        dest.writeInt(this.trackNumber);
        dest.writeString(this.language);
        dest.writeString(this.country);
        dest.writeString(this.proxyCompany);
        dest.writeString(this.publishTime);
        dest.writeString(this.musicInfo);
        dest.writeString(this.versions);
        dest.writeString(this.albumId);
        dest.writeString(this.albumTitle);
        dest.writeString(this.albumCover);
        dest.writeParcelable(this.albumCoverBitmap, flags);
        dest.writeString(this.albumArtist);
        dest.writeInt(this.albumMusicCount);
        dest.writeInt(this.albumPlayCount);
        dest.writeParcelable(this.metadataCompat, flags);
        dest.writeString(this.temp_1);
        dest.writeString(this.temp_2);
        dest.writeString(this.temp_3);
        dest.writeString(this.temp_4);
        dest.writeString(this.temp_5);
        dest.writeString(this.temp_6);
        dest.writeString(this.temp_7);
        dest.writeString(this.temp_8);
        dest.writeString(this.temp_9);
    }

    public MusicInfo() {
    }

    protected MusicInfo(Parcel in) {
        this.musicId = in.readString();
        this.musicTitle = in.readString();
        this.musicCover = in.readString();
        this.musicCoverBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        this.musicUrl = in.readString();
        this.musicGenre = in.readString();
        this.musicType = in.readString();
        this.musicSize = in.readString();
        this.musicDuration = in.readLong();
        this.musicArtist = in.readString();
        this.artistId = in.readString();
        this.musicDownloadUrl = in.readString();
        this.musicSite = in.readString();
        this.favorites = in.readInt();
        this.playCount = in.readInt();
        this.trackNumber = in.readInt();
        this.language = in.readString();
        this.country = in.readString();
        this.proxyCompany = in.readString();
        this.publishTime = in.readString();
        this.musicInfo = in.readString();
        this.versions = in.readString();
        this.albumId = in.readString();
        this.albumTitle = in.readString();
        this.albumCover = in.readString();
        this.albumCoverBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        this.albumArtist = in.readString();
        this.albumMusicCount = in.readInt();
        this.albumPlayCount = in.readInt();
        this.metadataCompat = in.readParcelable(MediaMetadataCompat.class.getClassLoader());
        this.temp_1 = in.readString();
        this.temp_2 = in.readString();
        this.temp_3 = in.readString();
        this.temp_4 = in.readString();
        this.temp_5 = in.readString();
        this.temp_6 = in.readString();
        this.temp_7 = in.readString();
        this.temp_8 = in.readString();
        this.temp_9 = in.readString();
    }

    public static final Parcelable.Creator<MusicInfo> CREATOR = new Parcelable.Creator<MusicInfo>() {
        @Override
        public MusicInfo createFromParcel(Parcel source) {
            return new MusicInfo(source);
        }

        @Override
        public MusicInfo[] newArray(int size) {
            return new MusicInfo[size];
        }
    };
}

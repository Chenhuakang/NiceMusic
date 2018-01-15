package com.lzx.nicemusic.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xian on 2018/1/13.
 */

public class MusicInfo {
    private String songname;
    private String seconds;
    private String albummid;
    private String songid;
    private String singerid;
    @SerializedName("albumpic_big")
    private String albumpicBig;
    @SerializedName("albumpic_small")
    private String albumpicSmall;
    private String downUrl;
    private String url;
    private String singername;
    private String albumid;
    private String m4a;
    private String albumname;

    public String getSongname() {
        return songname;
    }

    public void setSongname(String songname) {
        this.songname = songname;
    }

    public String getSeconds() {
        return seconds;
    }

    public void setSeconds(String seconds) {
        this.seconds = seconds;
    }

    public String getAlbummid() {
        return albummid;
    }

    public void setAlbummid(String albummid) {
        this.albummid = albummid;
    }

    public String getSongid() {
        return songid;
    }

    public void setSongid(String songid) {
        this.songid = songid;
    }

    public String getSingerid() {
        return singerid;
    }

    public void setSingerid(String singerid) {
        this.singerid = singerid;
    }

    public String getAlbumpicBig() {
        return albumpicBig;
    }

    public void setAlbumpicBig(String albumpicBig) {
        this.albumpicBig = albumpicBig;
    }

    public String getAlbumpicSmall() {
        return albumpicSmall;
    }

    public void setAlbumpicSmall(String albumpicSmall) {
        this.albumpicSmall = albumpicSmall;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSingername() {
        return singername;
    }

    public void setSingername(String singername) {
        this.singername = singername;
    }

    public String getAlbumid() {
        return albumid;
    }

    public void setAlbumid(String albumid) {
        this.albumid = albumid;
    }

    public String getM4a() {
        return m4a;
    }

    public void setM4a(String m4a) {
        this.m4a = m4a;
    }

    public String getAlbumname() {
        return albumname;
    }

    public void setAlbumname(String albumname) {
        this.albumname = albumname;
    }
}

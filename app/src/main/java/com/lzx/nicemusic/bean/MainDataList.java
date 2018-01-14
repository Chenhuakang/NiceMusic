package com.lzx.nicemusic.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xian on 2018/1/13.
 */

public class MainDataList {
    private List<MusicInfo> hotMusic = new ArrayList<>();
    private List<MusicInfo> newMusic = new ArrayList<>();
    private List<MusicInfo> ktvMusic = new ArrayList<>();
    private List<MusicInfo> webMusic = new ArrayList<>();
    private List<BaiSiBuDeJieMusic> baiSiBuDeJieMusics = new ArrayList<>();
    private List<BannerInfo> bannerList = new ArrayList<>();
    private List<BannerInfo> longLegs = new ArrayList<>();
    private BannerInfo artGirl = new BannerInfo();

    public List<MusicInfo> getHotMusic() {
        return hotMusic;
    }

    public void setHotMusic(List<MusicInfo> hotMusic) {
        this.hotMusic = hotMusic;
    }

    public List<MusicInfo> getNewMusic() {
        return newMusic;
    }

    public void setNewMusic(List<MusicInfo> newMusic) {
        this.newMusic = newMusic;
    }

    public List<MusicInfo> getKtvMusic() {
        return ktvMusic;
    }

    public void setKtvMusic(List<MusicInfo> ktvMusic) {
        this.ktvMusic = ktvMusic;
    }

    public List<MusicInfo> getWebMusic() {
        return webMusic;
    }

    public void setWebMusic(List<MusicInfo> webMusic) {
        this.webMusic = webMusic;
    }

    public List<BaiSiBuDeJieMusic> getBaiSiBuDeJieMusics() {
        return baiSiBuDeJieMusics;
    }

    public void setBaiSiBuDeJieMusics(List<BaiSiBuDeJieMusic> baiSiBuDeJieMusics) {
        this.baiSiBuDeJieMusics = baiSiBuDeJieMusics;
    }

    public List<BannerInfo> getLongLegs() {
        return longLegs;
    }

    public void setLongLegs(List<BannerInfo> longLegs) {
        this.longLegs = longLegs;
    }

    public BannerInfo getArtGirl() {
        return artGirl;
    }

    public void setArtGirl(BannerInfo artGirl) {
        this.artGirl = artGirl;
    }

    public List<BannerInfo> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<BannerInfo> bannerList) {
        this.bannerList = bannerList;
    }
}

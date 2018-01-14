package com.lzx.nicemusic.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xian on 2018/1/13.
 */

public class HomeInfo {
    private List<BannerInfo> bannerList = new ArrayList<>();
    private List<BannerInfo> longLegs = new ArrayList<>();
    private BannerInfo artGirl = new BannerInfo();


    private String itemTitle;
    private int itemType;

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

    private String text;
    private String hate;
    private String videotime;
    private String voicetime;
    @SerializedName("weixin_url")
    private String weixinUrl;
    @SerializedName("profile_image")
    private String profileImage;
    private String width;
    private String voiceuri;
    private String type;
    private String id;
    private String love;
    private String height;
    @SerializedName("voice_uri")
    private String voiceUri;
    private String voicelength;
    private String name;
    @SerializedName("create_time")
    private String createTime;
    private String image3;


    public static final int TYPE_ITEM_BANNER = 0;
    public static final int TYPE_ITEM_TITLE = 1;
    public static final int TYPE_ITEM_ONE = 2;
    public static final int TYPE_ITEM_LONGLEGS = 3;
    public static final int TYPE_ITEM_TWO = 4;
    public static final int TYPE_ITEM_THREE = 5;
    public static final int TYPE_ITEM_ARTS = 6;

    public HomeInfo(String itemTitle, int itemType) {
        this.itemTitle = itemTitle;
        this.itemType = itemType;
    }

    public HomeInfo() {
    }

    public List<BannerInfo> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<BannerInfo> bannerList) {
        this.bannerList = bannerList;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHate() {
        return hate;
    }

    public void setHate(String hate) {
        this.hate = hate;
    }

    public String getVideotime() {
        return videotime;
    }

    public void setVideotime(String videotime) {
        this.videotime = videotime;
    }

    public String getVoicetime() {
        return voicetime;
    }

    public void setVoicetime(String voicetime) {
        this.voicetime = voicetime;
    }

    public String getWeixinUrl() {
        return weixinUrl;
    }

    public void setWeixinUrl(String weixinUrl) {
        this.weixinUrl = weixinUrl;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getVoiceuri() {
        return voiceuri;
    }

    public void setVoiceuri(String voiceuri) {
        this.voiceuri = voiceuri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLove() {
        return love;
    }

    public void setLove(String love) {
        this.love = love;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getVoiceUri() {
        return voiceUri;
    }

    public void setVoiceUri(String voiceUri) {
        this.voiceUri = voiceUri;
    }

    public String getVoicelength() {
        return voicelength;
    }

    public void setVoicelength(String voicelength) {
        this.voicelength = voicelength;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

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
}

package com.lzx.nicemusic.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @author lzx
 * @date 2018/1/22
 */

public class SingerInfo {
    private String nickname;
    @SerializedName("avatar_s500")
    private String avatar;
    private String intro;
    private String country;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}

package com.miaotu.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Jayden on 2015/5/29.
 * 通讯录
 */
public class Address {
    @JsonProperty("Phone")
    private String phone;
    @JsonProperty("Nickname")
    private String nickname;
    @JsonProperty("IsLike")
    private String islike;
    @JsonProperty("IsOwn")
    private String isown;
    @JsonProperty("Uid")
    private String uid;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIslike() {
        return islike;
    }

    public void setIslike(String islike) {
        this.islike = islike;
    }

    public String getIsown() {
        return isown;
    }

    public void setIsown(String isown) {
        this.isown = isown;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

package com.miaotu.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Jayden on 2015/7/23.
 */
public class Review {

    @JsonProperty("Yjid")
    private String yjid;
    @JsonProperty("Uid")
    private String uid;
    @JsonProperty("Yid")
    private String yid;
    @JsonProperty("UserName")
    private String userName;
    @JsonProperty("UserPhone")
    private String userPhone;
    @JsonProperty("UserMark")
    private String userMark;
    @JsonProperty("Created")
    private String created;
    @JsonProperty("Status")
    private String status;
    @JsonProperty("PassTime")
    private String passTime;

    public String getYjid() {
        return yjid;
    }

    public void setYjid(String yjid) {
        this.yjid = yjid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getYid() {
        return yid;
    }

    public void setYid(String yid) {
        this.yid = yid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserMark() {
        return userMark;
    }

    public void setUserMark(String userMark) {
        this.userMark = userMark;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPassTime() {
        return passTime;
    }

    public void setPassTime(String passTime) {
        this.passTime = passTime;
    }
}

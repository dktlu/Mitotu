package com.miaotu.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Jayden on 2015/7/28.
 */
public class TogetherComment {
    @JsonProperty("Yrid")
    private String yrid;
    @JsonProperty("Uid")
    private String uid;
    @JsonProperty("Yid")
    private String yid;
    @JsonProperty("Status")
    private String status;
    @JsonProperty("Content")
    private String content;
    @JsonProperty("Pyrid")
    private String pyrid;
    @JsonProperty("Created")
    private String created;

    public String getYrid() {
        return yrid;
    }

    public void setYrid(String yrid) {
        this.yrid = yrid;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPyrid() {
        return pyrid;
    }

    public void setPyrid(String pyrid) {
        this.pyrid = pyrid;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}

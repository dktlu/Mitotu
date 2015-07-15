package com.miaotu.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Jayden on 2015/5/29.
 */
public class ImageWall {
    @JsonProperty("Sid")
    private String sid;
    @JsonProperty("Url")
    private String url;
    @JsonProperty("Content")
    private String content;
    @JsonProperty("StateReplyCount")
    private String statereplycount;
    @JsonProperty("StateLikeCount")
    private String statelikecount;
    @JsonProperty("IsLike")
    private String islike;
    @JsonProperty("Created")
    private String created;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatereplycount() {
        return statereplycount;
    }

    public void setStatereplycount(String statereplycount) {
        this.statereplycount = statereplycount;
    }

    public String getStatelikecount() {
        return statelikecount;
    }

    public void setStatelikecount(String statelikecount) {
        this.statelikecount = statelikecount;
    }

    public String getIslike() {
        return islike;
    }

    public void setIslike(String islike) {
        this.islike = islike;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}

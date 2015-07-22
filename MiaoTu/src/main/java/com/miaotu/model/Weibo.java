package com.miaotu.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Jayden on 2015/7/22.
 */
public class Weibo {

//    "id": 1404376560,
//            "screen_name": "zaku",
//            "name": "zaku",
//            "province": "11",
//            "city": "5",
//            "location": "北京 朝阳区",
//            "description": "人生五十年，乃如梦如幻；有生斯有死，壮士复何憾。",
//            "url": "http://blog.sina.com.cn/zaku",
//            "profile_image_url": "http://tp1.sinaimg.cn/1404376560/50/0/1",
//            "domain": "zaku",
//            "gender": "m",
//            "followers_count": 1204,
//            "friends_count": 447,
//            "statuses_count": 2908,
//            "favourites_count": 0,
//            "created_at": "Fri Aug 28 00:00:00 +0800 2009",
//            "following": false,
//            "allow_all_act_msg": false,
//            "remark": "",
//            "geo_enabled": true,
//            "verified": false,
    @JsonProperty("id")
    private String id;
    @JsonProperty("screen_name")
    private String screen_name;
    @JsonProperty("name")
    private String name;
    @JsonProperty("province")
    private String province;
    @JsonProperty("city")
    private String city;
    @JsonProperty("location")
    private String location;
    @JsonProperty("description")
    private String description;
    @JsonProperty("url")
    private String url;
    @JsonProperty("profile_image_url")
    private String profile_image_url;
    @JsonProperty("followers_count")
    private String followers_count;
    @JsonProperty("friends_count")
    private String friends_count;
    @JsonProperty("status")
    private WeiboStatus weiboStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    public String getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(String followers_count) {
        this.followers_count = followers_count;
    }

    public String getFriends_count() {
        return friends_count;
    }

    public void setFriends_count(String friends_count) {
        this.friends_count = friends_count;
    }
}

package com.miaotu.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.miaotu.model.Address;
import com.miaotu.model.Weibo;

import java.util.List;

/**
 * Created by Jayden on 2015/5/29.
 * 通讯录
 */
public class WeiboResult {
    @JsonProperty("users")
    private List<Weibo> weiboList;
    @JsonProperty("next_cursor")
    private String next_cursor;
    @JsonProperty("previous_cursor")
    private String previous_cursor;
    @JsonProperty("total_number")
    private String total_number;

    public List<Weibo> getWeiboList() {
        return weiboList;
    }

    public void setWeiboList(List<Weibo> weiboList) {
        this.weiboList = weiboList;
    }

    public String getNext_cursor() {
        return next_cursor;
    }

    public void setNext_cursor(String next_cursor) {
        this.next_cursor = next_cursor;
    }

    public String getPrevious_cursor() {
        return previous_cursor;
    }

    public void setPrevious_cursor(String previous_cursor) {
        this.previous_cursor = previous_cursor;
    }

    public String getTotal_number() {
        return total_number;
    }

    public void setTotal_number(String total_number) {
        this.total_number = total_number;
    }
}

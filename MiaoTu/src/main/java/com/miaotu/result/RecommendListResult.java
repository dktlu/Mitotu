package com.miaotu.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.miaotu.model.Address;
import com.miaotu.model.Recommend;

import java.util.List;

/**
 * Created by Jayden on 2015/5/29.
 * 通讯录
 */
public class RecommendListResult extends BaseResult {
    @JsonProperty("Items")
    private List<Recommend> recommendList;

    public List<Recommend> getRecommendList() {
        return recommendList;
    }

    public void setRecommendList(List<Recommend> recommendList) {
        this.recommendList = recommendList;
    }
}

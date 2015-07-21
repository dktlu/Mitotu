package com.miaotu.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.miaotu.model.Recommend;
import com.miaotu.model.Topic;


import java.util.List;

/**
 * 
 * @author zhangying
 *
 */
public class TopicListResult extends BaseResult{
	@JsonProperty("Items")
	private List<Topic> topics;
    @JsonProperty("RecommentUser")
    private List<Recommend> recommends;

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public List<Recommend> getRecommends() {
        return recommends;
    }

    public void setRecommends(List<Recommend> recommends) {
        this.recommends = recommends;
    }
}

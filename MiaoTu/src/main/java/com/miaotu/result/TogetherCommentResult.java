package com.miaotu.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.miaotu.model.Address;
import com.miaotu.model.TogetherComment;

import java.util.List;

/**
 * Created by Jayden on 2015/5/29.
 * 通讯录
 */
public class TogetherCommentResult extends BaseResult {
    @JsonProperty("Items")
    private TogetherComment togetherComment;

    public TogetherComment getTogetherComment() {
        return togetherComment;
    }

    public void setTogetherComment(TogetherComment togetherComment) {
        this.togetherComment = togetherComment;
    }
}

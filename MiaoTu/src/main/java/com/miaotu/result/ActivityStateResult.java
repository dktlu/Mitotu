package com.miaotu.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.miaotu.model.ActivityState;
import com.miaotu.model.Address;

import java.util.List;

/**
 * Created by Jayden on 2015/5/29.
 * 通讯录
 */
public class ActivityStateResult extends BaseResult {
    @JsonProperty("Items")
    private ActivityState state;

    public ActivityState getState() {
        return state;
    }

    public void setState(ActivityState state) {
        this.state = state;
    }
}

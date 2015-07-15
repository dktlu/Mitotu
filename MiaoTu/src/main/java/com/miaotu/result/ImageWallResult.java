package com.miaotu.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.miaotu.model.BlackInfo;
import com.miaotu.model.ImageWall;

import java.util.List;

/**
 * Created by Jayden on 2015/5/29.
 */
public class ImageWallResult extends BaseResult {
    @JsonProperty("Items")
    private List<ImageWall> imageWallList;

    public List<ImageWall> getImageWallList() {
        return imageWallList;
    }

    public void setImageWallList(List<ImageWall> imageWallList) {
        this.imageWallList = imageWallList;
    }
}

package com.iustu.identification.api.message.request;

import com.google.gson.annotations.SerializedName;
import com.iustu.identification.config.ParametersConfig;

import java.util.List;

/**
 * Created by Liu Yuchuan on 2017/11/29.
 */

public class AdcancedFaceSearchRequest {
    private String rect;
    private String image;
    @SerializedName("max_result")
    private int maxResult;
    @SerializedName("multi_face")
    private boolean multiFace;
    @SerializedName("face_set")
    private List<String> faceSet;

    public String getRect() {
        return rect;
    }

    public void setRect(String rect) {
        this.rect = rect;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getMaxResult() {
        return maxResult;
    }

    public void setMaxResult(int maxResult) {
        this.maxResult = maxResult;
    }

    public boolean isMultiFace() {
        return multiFace;
    }

    public void setMultiFace(boolean multiFace) {
        this.multiFace = multiFace;
    }

    public List<String> getFaceSet() {
        return faceSet;
    }

    public void setFaceSet(List<String> faceSet) {
        this.faceSet = faceSet;
    }
}

package com.iustu.identification.api.message.response;

import com.google.gson.annotations.SerializedName;
import com.iustu.identification.bean.FaceResult;

import java.util.List;

/**
 * Created by Liu Yuchuan on 2017/11/29.
 */

public class AdvancedFaceSearchResponse {
    @SerializedName("face_rect")
    private String faceRect;
    @SerializedName("face_results")
    private List<FaceResult> faceResults;

    public String getFaceRect() {
        return faceRect;
    }

    public void setFaceRect(String faceRect) {
        this.faceRect = faceRect;
    }

    public List<FaceResult> getFaceResults() {
        return faceResults;
    }

    public void setFaceResults(List<FaceResult> faceResults) {
        this.faceResults = faceResults;
    }
}

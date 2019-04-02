package com.iustu.identification.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FaceResult {
    @SerializedName("face_set_id")
    private String faceSetId;
    @SerializedName("face_set_results")
    private List<FaceSetResult> faceSetResults;

    public String getFaceSetId() {
        return faceSetId;
    }

    public void setFaceSetId(String faceSetId) {
        this.faceSetId = faceSetId;
    }

    public List<FaceSetResult> getFaceSetResults() {
        return faceSetResults;
    }

    public void setFaceSetResults(List<FaceSetResult> faceSetResults) {
        this.faceSetResults = faceSetResults;
    }
}
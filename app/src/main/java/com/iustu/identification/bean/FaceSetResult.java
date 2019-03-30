package com.iustu.identification.bean;

import com.google.gson.annotations.SerializedName;

public class FaceSetResult{
    @SerializedName("people_id")
    private String peopleId;
    @SerializedName("face_id")
    private String faceId;
    private double score;

    public String getPeopleId() {
        return peopleId;
    }

    public void setPeopleId(String peopleId) {
        this.peopleId = peopleId;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
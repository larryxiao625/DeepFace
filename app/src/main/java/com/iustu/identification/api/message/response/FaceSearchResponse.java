package com.iustu.identification.api.message.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Liu Yuchuan on 2017/11/29.
 */

public class FaceSearchResponse {
    private String rect;
    private List<Result> result;

    public String getRect() {
        return rect;
    }

    public void setRect(String rect) {
        this.rect = rect;
    }

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public static class Result{
        @SerializedName("face_set_id")
        private String faceSetId;
        @SerializedName("people_id")
        private String peopleId;
        @SerializedName("face_id")
        private String faceId;
        private double score;

        public String getFaceSetId() {
            return faceSetId;
        }

        public void setFaceSetId(String faceSetId) {
            this.faceSetId = faceSetId;
        }

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
}

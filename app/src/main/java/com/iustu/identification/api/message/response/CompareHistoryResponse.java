package com.iustu.identification.api.message.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Liu Yuchuan on 2017/11/28.
 */

public class CompareHistoryResponse {
    private int page;
    @SerializedName("total_page")
    private int totalPage;
    private int count;
    private List<Result> result;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public static class Result{
        @SerializedName("task_id")
        private String taskId;
        @SerializedName("camera_id")
        private String cameraId;
        @SerializedName("capture_face_id")
        private String captureFaceId;
        @SerializedName("face_set_id")
        private String faceSetId;
        @SerializedName("people_id")
        private String peopleId;
        @SerializedName("face_id")
        private String faceId;
        private double score;
        private String time;

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public String getCameraId() {
            return cameraId;
        }

        public void setCameraId(String cameraId) {
            this.cameraId = cameraId;
        }

        public String getCaptureFaceId() {
            return captureFaceId;
        }

        public void setCaptureFaceId(String captureFaceId) {
            this.captureFaceId = captureFaceId;
        }

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

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}

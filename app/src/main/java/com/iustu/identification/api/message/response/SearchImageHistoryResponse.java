package com.iustu.identification.api.message.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Liu Yuchuan on 2017/11/28.
 */

public class SearchImageHistoryResponse {
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

    public static class Result {
        private String id;
        @SerializedName("max_result")
        private String maxResult;
        @SerializedName("min_score")
        private double minScore;
        private String time;
        @SerializedName("result_count")
        private int resultCount;
        @SerializedName("face_count")
        private int faceCount;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMaxResult() {
            return maxResult;
        }

        public void setMaxResult(String maxResult) {
            this.maxResult = maxResult;
        }

        public double getMinScore() {
            return minScore;
        }

        public void setMinScore(double minScore) {
            this.minScore = minScore;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getResultCount() {
            return resultCount;
        }

        public void setResultCount(int resultCount) {
            this.resultCount = resultCount;
        }

        public int getFaceCount() {
            return faceCount;
        }

        public void setFaceCount(int faceCount) {
            this.faceCount = faceCount;
        }
    }
}

package com.iustu.identification.api.message.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Liu Yuchuan on 2017/11/28.
 */

public class HistoryRequest {
    @SerializedName("begin_time")
    private String beginTime;
    @SerializedName("end_time")
    private String endTime;
    private int page;
    @SerializedName("page_size")
    private int pageSize;

    public HistoryRequest(){}

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}

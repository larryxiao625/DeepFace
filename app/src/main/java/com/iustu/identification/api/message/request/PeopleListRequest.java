package com.iustu.identification.api.message.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Liu Yuchuan on 2017/11/28.
 */

public class PeopleListRequest {
    @SerializedName("face_set_id")
    private String faceSetId;
    private int page;
    @SerializedName("page_size")
    private int pageSize;

    public String getFaceSetId() {
        return faceSetId;
    }

    public void setFaceSetId(String faceSetId) {
        this.faceSetId = faceSetId;
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

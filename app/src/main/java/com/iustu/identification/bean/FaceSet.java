package com.iustu.identification.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Liu Yuchuan on 2017/11/28.
 */

public class FaceSet {
    private String id;
    private String name;
    @SerializedName("people_count")
    private int peopleCount;
    @SerializedName("face_count")
    private int faceCount;
    private String remark;
    @SerializedName("create_time")
    private String createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(int peopleCount) {
        this.peopleCount = peopleCount;
    }

    public int getFaceCount() {
        return faceCount;
    }

    public void setFaceCount(int faceCount) {
        this.faceCount = faceCount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}

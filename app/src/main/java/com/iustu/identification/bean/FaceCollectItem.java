package com.iustu.identification.bean;


import android.content.ContentValues;

import org.litepal.crud.DataSupport;

/**
 * Created by Liu Yuchuan on 2017/11/22.
 */

public class FaceCollectItem extends DataSupport{
    private String imgUrl;
    private String time;
    private String faceId;
    private int id;

    public FaceCollectItem(String imgUrl, String time, String faceId, int id) {
        this.imgUrl = imgUrl;
        this.time = time;
        this.faceId = faceId;
        this.id = id;
    }

    public FaceCollectItem() {}

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put("imgUrl", this.imgUrl);
        values.put("faceId", this.faceId);
        values.put("time", this.time);
        return values;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String id) {
        this.faceId = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

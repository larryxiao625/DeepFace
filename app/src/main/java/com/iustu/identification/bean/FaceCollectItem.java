package com.iustu.identification.bean;


import android.content.ContentValues;

import org.litepal.crud.DataSupport;

/**
 * Created by Liu Yuchuan on 2017/11/22.
 */

public class FaceCollectItem extends DataSupport{
    private String imgUrl;
    private String hourTime;
    private String time;
    private String faceId;
    private int id;
    private String originalPhoto;        // 最初的照片
    private int isUpload;     //图片是否上传，0表示未上传，1代表上传成功

    public FaceCollectItem() {}

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put("imgUrl", this.imgUrl);
        values.put("faceId", this.faceId);
        values.put("time", this.time);
        values.put("originalPath", this.originalPhoto);
        values.put("hourTime",this.hourTime);
        values.put("isUpload",this.isUpload);
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

    public void setOriginalPhoto(String originalPhoto) {
        this.originalPhoto = originalPhoto;
    }

    public String getOriginalPhoto() {
        return this.originalPhoto;
    }

    public void setHourTime(String hourTime) {
        this.hourTime = hourTime;
    }

    public String getHourTime() {
        return this.hourTime;
    }

    public int getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(int isUpload) {
        this.isUpload = isUpload;
    }
}

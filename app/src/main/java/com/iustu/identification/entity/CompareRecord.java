package com.iustu.identification.entity;

/**
 * created by sgh, 2019-4-2
 *
 * 用来记录对比记录的数据表
 */
public class CompareRecord {
    public String uploadPhotoPath;          // 对比时上传的图片的uri
    public float[] feature;          // 外键， 人脸特征
    public String time;             // 时间戳， 比对的时间
    public float rate;              // 比对的相似度
    public boolean isExtend=false;    //是否展开
    public CompareRecord(){

    }

    public CompareRecord(String uploadPhotoPath, float[] feature, String time, float rate) {
        this.uploadPhotoPath = uploadPhotoPath;
        this.feature = feature;
        this.time = time;
        this.rate = rate;
    }

    public String getUploadPhotoPath() {
        return uploadPhotoPath;
    }

    public void setUploadPhotoPath(String uploadPhotoPath) {
        this.uploadPhotoPath = uploadPhotoPath;
    }

    public float[] getFeature() {
        return feature;
    }

    public void setFeature(float[] feature) {
        this.feature = feature;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public boolean isExtend() {
        return isExtend;
    }

    public void setExtend(boolean extend) {
        isExtend = extend;
    }
}

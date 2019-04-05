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
}

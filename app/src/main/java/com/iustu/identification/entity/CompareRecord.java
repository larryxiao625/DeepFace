package com.iustu.identification.entity;

/**
 * created by sgh, 2019-4-2
 *
 * 用来记录对比记录的数据表
 */
public class CompareRecord {
    String uploadImagePath;          // 对比时上传的图片的uri
    float[] feature;          // 外键， 人脸特征
    String time;             // 时间戳， 比对的时间
    float rate;              // 比对的相似度
}

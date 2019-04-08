package com.iustu.identification.entity;

/**
 * created by sgh, 2019-4-2
 *
 * 人脸库中数据对应的数据表
 */
public class PersionInfo {
    public int libId;            // 外键，为Library的libId,表示该信息所在的人脸库
    public String name;
    public String gender;        // 性别
    public String photoPath = "test";       // 图片路径（可能有多张人脸，所以该字符串是多个拼接起来的）
    public String identity;         // 身份证号(选)
    public String home;           // 籍贯(选)
    public String other;         // 备注
    public String feature;     // key 人脸特点，SDK生成的，用来唯一标识人脸
}

package com.iustu.identification.entity;

import android.content.ContentValues;

/**
 * created by sgh, 2019-4-2
 *
 * 人脸库中数据对应的数据表
 */
public class PersionInfo {
    public String libName;            // 该属性并不绑定于一个Library，只是用来在创建
    public String name;
    public String gender;        // 性别
    public String photoPath = "test";       // 图片路径（可能有多张人脸，所以该字符串是多个拼接起来的）
    public String identity;         // 身份证号(选)
    public String home;           // 籍贯(选)
    public String other;         // 备注
    public String feature;     // 人脸特点，SDK生成的，用来唯一标识人脸
    public String image_id;    // key

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put("libName", libName);
        values.put("name", name);
        values.put("gender", gender);
        values.put("photoPath", photoPath);
        values.put("identity", identity);
        values.put("home", home);
        values.put("other", other);
        values.put("feature", feature);
        values.put("image_id", image_id);
        return values;
    }
}

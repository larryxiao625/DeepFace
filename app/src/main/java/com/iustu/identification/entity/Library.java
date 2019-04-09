package com.iustu.identification.entity;

import android.content.ContentValues;

/**
 * created by sgh, 2019-4-2
 *
 * 人脸库对应的数据表
 */
public class Library {
    public int inUsed;     // 表示是否被选中
    public String libName;  // key
    public String description;      // 库描述
    public int count;      // 库数量
    public int libId; // autoincre



    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put("libName", this.libName);
        values.put("count", this.count);
        values.put("description", description);
        values.put("inUsed", inUsed);
        return values;
    }
}

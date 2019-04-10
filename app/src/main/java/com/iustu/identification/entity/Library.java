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


    public boolean isInUse;     //是否使用
    public Library(){

    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put("libName", this.libName);
        values.put("count", this.count);
        values.put("description", description);
        values.put("inUsed", inUsed);
        return values;
    }

    public Library(String libName, String description, int count, boolean isInUse) {
        this.libName = libName;
        this.description = description;
        this.count = count;
        this.isInUse = isInUse;
    }

    public String getLibName() {
        return libName;
    }

    public void setLibName(String libName) {
        this.libName = libName;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isInUse() {
        return isInUse;
    }

    public void setInUse(boolean inUse) {
        isInUse = inUse;
    }
}

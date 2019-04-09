package com.iustu.identification.entity;

/**
 * created by sgh, 2019-4-2
 *
 * 人脸库对应的数据表
 */
public class Library {
    public String libName;
    public int libId;       // key
    public String description;      // 库描述
    public int count;      // 库数量
    public boolean isInUse;     //是否使用
    public Library(){

    }
    public Library(String libName, int libId, String discription, int count) {
        this.libName = libName;
        this.count = count;
        this.description = discription;
        this.libId = libId;
    }

    public Library(String libName, String description, int count) {
        this.libName = libName;
        this.description = description;
        this.count = count;
    }

    public Library(String libName, int libId, String description, int count, boolean isInUse) {
        this.libName = libName;
        this.libId = libId;
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

    public int getLibId() {
        return libId;
    }

    public void setLibId(int libId) {
        this.libId = libId;
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

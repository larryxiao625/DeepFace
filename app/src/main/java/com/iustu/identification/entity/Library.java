package com.iustu.identification.entity;

/**
 * created by sgh, 2019-4-2
 *
 * 人脸库对应的数据表
 */
public class Library {
    public String libName;
    public int libId;       // key
    public String discription;      // 库描述
    public int count;      // 库数量

    public Library(String libName, int libId, String discription, int count) {
        this.libName = libName;
        this.count = count;
        this.discription = discription;
        this.libId = libId;
    }
}

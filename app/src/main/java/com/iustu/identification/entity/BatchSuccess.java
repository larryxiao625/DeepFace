package com.iustu.identification.entity;

/**
 * created by sgh, 2019-4-22
 * 用来记录批量导入成功的数目
 * 和关联的library
 */
public class BatchSuccess {
    public int success;
    public int index;
    public String libName;
    public BatchSuccess(int success, int index, String libName) {
        this.index = index;
        this.success = success;
        this.libName = libName;
    }
}

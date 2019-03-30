package com.iustu.identification.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * Created by Liu Yuchuan on 2017/11/20.
 */

public class BatchCompareImg extends DataSupport {
    private int id;
    private String path;
    private boolean isCompared;
    @Column(ignore = true)
    private boolean isChoose;

    public BatchCompareImg(){
    }

    public BatchCompareImg(int id, String path, boolean isCompared) {
        this.id = id;
        this.path = path;
        this.isCompared = isCompared;
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCompared() {
        return isCompared;
    }

    public void setCompared(boolean compared) {
        isCompared = compared;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }

        if(! (obj instanceof BatchCompareImg)){
            return false;
        }

        if((((BatchCompareImg) obj).id == id)){
            return true;
        }

        return path.equals(((BatchCompareImg) obj).path);
    }

    @Override
    public int hashCode() {
        return id;
    }
}
package com.iustu.identification.bean;

import android.util.Log;

import com.iustu.identification.config.LibraryConfig;

/**
 * Created by Liu Yuchuan on 2017/11/20.
 */

public class Library {
    private int id;
    private String name;
    private int count;
    private boolean inUse;
    private boolean isLock;
    private String idOnServer;
    private String remark;

    public Library(){}

    public Library(FaceSet faceSet, int id){
        Log.e("new FaceSet", faceSet.getId() + " " + faceSet.getName());
        this.id = id;
        this.name = faceSet.getName();
        this.count = faceSet.getFaceCount();
        this.idOnServer = faceSet.getId();
        this.remark = faceSet.getRemark();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isInUse() {
        return inUse;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }
    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public String getIdOnServer() {
        return idOnServer;
    }

    public void setIdOnServer(String idOnServer) {
        this.idOnServer = idOnServer;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

package com.iustu.identification.bean;

import com.iustu.identification.api.Api;

/**
 * Created by Liu Yuchuan on 2017/12/6.
 */

public class SearchCompareItem{
    private double score;
    private String faceSetId;
    private String peopleId;
    private String faceId;
    private PersonInfo personInfo;
    private String photoUrl;
    private boolean isInitInfo;
    private boolean isInitPhotoUrl;
    private boolean isExtend;
    private int width;
    private int height;
    private String rect;
    private String photoPath;

    public SearchCompareItem(String faceSetId, FaceSetResult faceSetResult, String rect){
        this.score = faceSetResult.getScore();
        this.faceSetId = faceSetId;
        this.peopleId = faceSetResult.getPeopleId();
        this.faceId = faceSetResult.getFaceId();
        this.photoUrl = Api.getFaceImageFaceUrl(faceSetId, peopleId, faceId);
        this.rect = rect;
    }

    public SearchCompareItem(String faceSetId, FaceSetResult faceSetResult, String rect, String photoPath){
        this.score = faceSetResult.getScore();
        this.faceSetId = faceSetId;
        this.peopleId = faceSetResult.getPeopleId();
        this.faceId = faceSetResult.getFaceId();
        this.photoUrl = Api.getFaceImageFaceUrl(faceSetId, peopleId, faceId);
        this.rect = rect;
        this.photoPath = photoPath;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getRect() {
        return rect;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getFaceSetId() {
        return faceSetId;
    }

    public void setFaceSetId(String faceSetId) {
        this.faceSetId = faceSetId;
    }

    public String getPeopleId() {
        return peopleId;
    }

    public void setPeopleId(String peopleId) {
        this.peopleId = peopleId;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public PersonInfo getPersonInfo() {
        return personInfo;
    }

    public void setPersonInfo(PersonInfo personInfo) {
        this.personInfo = personInfo;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isInitInfo() {
        return isInitInfo;
    }

    public void setInitInfo(boolean initInfo) {
        isInitInfo = initInfo;
    }

    public boolean isInitPhotoUrl() {
        return isInitPhotoUrl;
    }

    public void setInitPhotoUrl(boolean initPhotoUrl) {
        isInitPhotoUrl = initPhotoUrl;
    }

    public boolean isExtend() {
        return isExtend;
    }

    public void setExtend(boolean extend) {
        isExtend = extend;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}

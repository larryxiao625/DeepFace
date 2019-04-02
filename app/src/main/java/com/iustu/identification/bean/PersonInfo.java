package com.iustu.identification.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.iustu.identification.api.Api;

import java.util.ArrayList;
import java.util.List;

public class PersonInfo{
    @SerializedName("face_set_id")
    private String faceSetId;
    private String id;
    private String name;
    private String gender;
    private String race;
    private String code;
    private String address;
    private String birthday;
    private String tel;
    private String remark;
    @Expose
    private final List<String> faceIdList;
    @Expose
    private final List<String> faceUrlList;
    @Expose
    private int urlPosition;
    @Expose
    private boolean isInitUrls;

    public PersonInfo(){
        isInitUrls = false;
        faceUrlList = new ArrayList<>();
        faceIdList = new ArrayList<>();
    }

    public PersonInfo(PersonInfo personInfo){
        this();
        faceSetId = personInfo.faceSetId;
        id = personInfo.id;
        name = personInfo.name;
        gender = personInfo.gender;
        race = personInfo.race;
        code = personInfo.code;
        address = personInfo.address;
        birthday = personInfo.birthday;
        tel = personInfo.tel;
        remark = personInfo.remark;
    }

    public String getFaceSetId() {
        return faceSetId;
    }

    public void setFaceSetId(String faceSetId) {
        this.faceSetId = faceSetId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setFaceUrlList(List<String> faceIdList) {
        this.faceIdList.clear();
        this.faceIdList.addAll(faceIdList);
        this.faceUrlList.clear();
        for (String s : faceIdList) {
            faceUrlList.add(Api.getFaceImageFaceUrl(faceSetId, id, s));
        }
        if(faceIdList.size() > 0){
            urlPosition = 0;
        }else {
            urlPosition = -1;
        }
    }

    public int getUrlPosition() {
        if(!isInitUrls) return -1;
        return urlPosition;
    }

    public boolean nextPosition(){
        if(urlPosition < faceUrlList.size() - 1){
            urlPosition++;
            return true;
        }

        return false;
    }

    public void deletePhoto(int pos){
        faceIdList.remove(pos);
        faceUrlList.remove(pos);
        if(faceUrlList.isEmpty()){
            urlPosition = -1;
        }else {
            urlPosition = 0;
        }
    }

    public int getPhotoPos(String id){
        return faceIdList.indexOf(id);
    }

    public void addPhoto(String id){
        faceIdList.add(0, id);
        faceUrlList.add(Api.getFaceImageFaceUrl(faceSetId, this.id, id));
        urlPosition = 0;
    }

    public boolean lastPosition(){
        if(urlPosition >= 1){
            urlPosition--;
            return true;
        }

        return false;
    }

    public boolean isInitUrls() {
        return isInitUrls;
    }

    public void setInitUrls(boolean initUrls) {
        isInitUrls = initUrls;
    }

    public List<String> getFaceUrlList() {
        return faceUrlList;
    }

    public String getUrlAt(int position){
        return faceUrlList.get(position);
    }

    public String getIdAt(int position){
        return faceIdList.get(position);
    }
}
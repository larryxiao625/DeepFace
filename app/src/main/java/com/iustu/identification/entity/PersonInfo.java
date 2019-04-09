package com.iustu.identification.entity;

/**
 * created by sgh, 2019-4-2
 *
 * 人脸库中数据对应的数据表
 */
public class PersonInfo {
    public int libId;            // 外键，为Library的libId,表示该信息所在的人脸库
    public String name;
    public String gender;        // 性别
    public String photoPath = "test";       // 图片路径（可能有多张人脸，所以该字符串是多个拼接起来的）
    public String identity;         // 身份证号(选)
    public String home;           // 籍贯(选)
    public String other;         // 备注
    public String feature;     // key 人脸特点，SDK生成的，用来唯一标识人脸
    public String birthday;     //生日
    public String address;      //地址
    public PersonInfo(){

    }
    public PersonInfo(int libId, String name, String gender, String photoPath, String identity, String home, String other, String feature, String birthday, String address) {
        this.libId = libId;
        this.name = name;
        this.gender = gender;
        this.photoPath = photoPath;
        this.identity = identity;
        this.home = home;
        this.other = other;
        this.feature = feature;
        this.birthday = birthday;
        this.address = address;
    }

    public int getLibId() {
        return libId;
    }

    public void setLibId(int libId) {
        this.libId = libId;
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

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}

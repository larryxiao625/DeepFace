package com.iustu.identification.api.message.response;

import com.google.gson.annotations.SerializedName;
import com.iustu.identification.bean.PersonInfo;

import java.util.List;

/**
 * Created by Liu Yuchuan on 2017/11/28.
 */

public class PeopleListResponse {
    private int page;
    @SerializedName("total_page")
    private int totalPage;
    private int count;
    private List<PersonInfo> peoples;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<PersonInfo> getPeoples() {
        return peoples;
    }

    public void setPeoples(List<PersonInfo> peoples) {
        this.peoples = peoples;
    }

    public static class Person{
        private String id;
        private String name;
        private String gender;
        private String race;
        private String code;
        private String address;
        private String birthday;
        private String tel;
        private String remark;

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
    }
}

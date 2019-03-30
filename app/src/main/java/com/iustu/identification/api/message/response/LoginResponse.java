package com.iustu.identification.api.message.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Liu Yuchuan on 2017/12/7.
 */

public class LoginResponse {
    @SerializedName("user_name")
    private String userName;
    private String name;
    private String session;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }
}

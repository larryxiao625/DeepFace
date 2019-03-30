package com.iustu.identification.bean;

/**
 * Created by Liu Yuchuan on 2017/11/20.
 */

public class User {
    private String username;
    private String name;
    private String session;
    private String id;
    private boolean isVerify;

    public static User makeUnVerifiedUser(){
        User user = new User();
        user.username = "未认证用户";
        user.name = "未认证用户";
        user.isVerify = false;
        return user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isVerify() {
        return isVerify;
    }

    public void setVerify(boolean verify) {
        isVerify = verify;
    }
}

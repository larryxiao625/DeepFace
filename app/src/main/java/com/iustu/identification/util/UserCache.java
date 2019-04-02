package com.iustu.identification.util;

import android.content.SharedPreferences;

import com.iustu.identification.bean.User;

/**
 * Created by Liu Yuchuan on 2017/12/5.
 */

public class UserCache {
    private static User user;
    private static boolean isLogin;

    public static User getUser(){
        if(user == null){
            readUser();
        }
        return user;
    }

    public static boolean isLogin() {
        if(user == null){
            readUser();
        }
        return isLogin;
    }

    public static void exit(){
        isLogin = false;
        MSP.getInstance("user")
                .edit()
                .clear()
                .apply();
    }

    public static void setUser(User userLogin){
        user = userLogin;
        writeUser();
    }

    private static void writeUser(){
        synchronized (User.class) {
            isLogin = true;
            MSP.getInstance("user")
                    .edit()
                    .putString("username", user.getUsername())
                    .putString("name", user.getName())
                    .putString("session", user.getSession())
                    .putString("id", user.getId())
                    .putBoolean("isLogin", true)
                    .apply();
        }
    }

    private static void readUser(){
        user = new User();
        SharedPreferences preferences = MSP.getInstance("user");
        isLogin = preferences.getBoolean("isLogin", false);
        user.setUsername(preferences.getString("username", null));
        user.setSession(preferences.getString("session", null));
        user.setId(preferences.getString("id", null));
        user.setName(preferences.getString("name", null));
    }
}
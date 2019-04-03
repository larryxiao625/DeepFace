package com.iustu.identification.entity;

import com.google.gson.Gson;
import com.iustu.identification.util.MSP;

import retrofit2.http.GET;

/**
 * created by sgh, 2019-4-2
 *
 * 用来记录登录账户的数据表
 */
public class Account {
    public String name;     // 作为主键
    public String password;

    // 将对象转化为json字符串，存进SharePreference中
    private String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    // 将json字符串转化为ParameterConfig对象
    // 主要是从SharePreference中取出String后转化为对象
    private static Account fromJsonString(String json) {
        if (json == null)
            return null;
        Gson gson = new Gson();
        return gson.fromJson(json, Account.class);
    }

    // 将ParameterConfig对象转化为json字符串之后保存到SharePreference中
    public void save() {
        String jsonString = this.toJsonString();
        MSP.getInstance(MSP.SP_ACCOUNT).edit().putString(MSP.ACCOUNT, jsonString).commit();
    }

    // 从json中解析对象并返回
    public static Account getFromSP() {
        return fromJsonString(MSP.getInstance(MSP.SP_ACCOUNT).getString(MSP.ACCOUNT, null));
    }
}

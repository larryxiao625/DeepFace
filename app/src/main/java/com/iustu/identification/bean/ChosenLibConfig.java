package com.iustu.identification.bean;

import com.google.gson.Gson;
import com.iustu.identification.util.MSP;

import java.util.List;

/**
 * created by sgh, 2019-4-2
 *
 * 用来记录被选中的人脸库
 */
public class ChosenLibConfig {
    List<Integer> libId;        // 被选中的数据库的id

    public ChosenLibConfig(){}
    public ChosenLibConfig(List list) {
        this.libId = list;
    }

    // 将对象转化为json字符串，存进SharePreference中
    private String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    // 将json字符串转化为ParameterConfig对象
    // 主要是从SharePreference中取出String后转化为对象
    private static ChosenLibConfig fromJsonString(String json) {
        if (json == null)
            return null;
        Gson gson = new Gson();
        return gson.fromJson(json, ChosenLibConfig.class);
    }

    // 将ParameterConfig对象转化为json字符串之后保存到SharePreference中
    public void save() {
        String jsonString = this.toJsonString();
        MSP.getInstance(MSP.SP_CHOSEN).edit().putString(MSP.CHOSEN, jsonString).commit();
    }

    // 从json中解析对象并返回
    public static ChosenLibConfig getFromSP() {
        return fromJsonString(MSP.getInstance(MSP.SP_CHOSEN).getString(MSP.CHOSEN, null));
    }
}

package com.iustu.identification.bean;

import com.google.gson.Gson;
import com.iustu.identification.util.MSP;

/**
 * created by sgh, 2019-4-2
 *
 * 人脸库管理界面中参数设置界面对应的设置类
 */
public class ParameterConfig {
    // "min_size 40\n pyramid_threshold 12\n factor 0.709\n thresholds 0.6 0.7 0.7\n"
    int min_size = 40;         // 为需要最小检测的人脸框大小，单位为像素，默认是40，小于min_rect太多的人脸将无法检测
    float factor = 0.709F;     // 为图像金字塔缩放的比率，用来检测出远大于min_rect大小的人脸，默认为0.709
    float threshold1;
    float threshold2;
    float threshold3;
    int saveCount;
    int displayCount;

    public ParameterConfig(){}
    public ParameterConfig(int min_size, float factor, float threshold1, float threshold2, float threshold3, int saveCount, int displayCount) {
        this.min_size = min_size;
        this.factor = factor;
        this.threshold1 = threshold1;
        this.threshold2 = threshold2;
        this.threshold3 = threshold3;
        this.saveCount = saveCount;
        this.displayCount = displayCount;
    }
    // 将对象转化为json字符串，存进SharePreference中
    private String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    // 将json字符串转化为ParameterConfig对象
    // 主要是从SharePreference中取出String后转化为对象
    private static ParameterConfig fromJsonString(String json) {
        if (json == null)
            return null;
        Gson gson = new Gson();
        return gson.fromJson(json, ParameterConfig.class);
    }

    // 将ParameterConfig对象转化为json字符串之后保存到SharePreference中
    public void save() {
        String jsonString = this.toJsonString();
        MSP.getInstance(MSP.SP_PARAMETERS).edit().putString(MSP.PARAMETERS, jsonString).commit();
    }

    // 从json中解析对象并返回
    public static ParameterConfig getFromSP() {
        return fromJsonString(MSP.getInstance(MSP.SP_PARAMETERS).getString(MSP.PARAMETERS, null));
    }
}

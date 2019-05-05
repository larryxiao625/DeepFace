package com.iustu.identification.bean;

import android.util.Log;

import com.google.gson.Gson;
import com.iustu.identification.util.MSP;

/**
 * created by sgh, 2019-4-2
 *
 * 人脸库管理界面中参数设置界面对应的设置类
 */
public class ParameterConfig {
    public static final int ONLYMP3 = 0;
    public static final int ONLYSHAKE = 1;
    public static final int MP3ANDSHAKE = 2;
    // "min_size 40\n pyramid_threshold 12\n factor 0.709\n thresholds 0.6 0.7 0.7\n"
    int min_size = 200;         // 为需要最小检测的人脸框大小，单位为像素，默认是40，小于min_rect太多的人脸将无法检测
    float factor = 0.89F;     // 用来判断比对结果是否为同一个人
    float threshold1 = 0.6f;
    float threshold2 = 0.8f;
    float threshold3 = 0.98f;
    int saveCount = 1000;
    int savePosition = 1;      // saveCount 选中的位置
    int displayCount = 12;
    int displayPosition = 0;    // displayCount 选中的位置
    int dpiWidth=1920;
    int dpiHeight=1080;
    int dpiCount=0;
    float filterScore = 0.73f;       // 人脸比对的阈值
    float thresholdQuanity = 0.76f;       // 临界值
    int alarmType = ONLYMP3;
    int minEyesDistance = 20;      // 比对最小瞳距

    public ParameterConfig(){}

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

    // 生成人脸比对时setArgument需要的字符串
    public String gengrateArguments() {
        return String.format("min_size %d\n pyramid_threshold 16\n factor 0.709\n thresholds %f %f %f\n", this.min_size, this.threshold1, this.threshold2, this.threshold3);
    }

    // 将ParameterConfig对象转化为json字符串之后保存到SharePreference中
    public void save() {
        String jsonString = this.toJsonString();
        MSP.getInstance(MSP.SP_PARAMETERS).edit().putString(MSP.SP_PARAMETERS, jsonString).apply();
    }

    // 从json中解析对象并返回
    public static ParameterConfig getFromSP() {
        return fromJsonString(MSP.getInstance(MSP.SP_PARAMETERS).getString(MSP.SP_PARAMETERS, new ParameterConfig().toJsonString()));
    }

    public void setFilterScore(float d) {
        this.filterScore = d;
    }
    public float getFilterScore() {
        return filterScore;
    }

    public void setThresholdQuanity(float factor) {
        this.thresholdQuanity = factor;
    }

    public float getThresholdQuanity() {
        return this.thresholdQuanity;
    }
    public int getMin_size() {
        return min_size;
    }

    public void setMin_size(int min_size) {
        this.min_size = min_size;
    }

    public float getFactor() {
        return factor;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }

    public float getThreshold1() {
        return threshold1;
    }

    public void setThreshold1(float threshold1) {
        this.threshold1 = threshold1;
    }

    public float getThreshold2() {
        return threshold2;
    }

    public void setThreshold2(float threshold2) {
        this.threshold2 = threshold2;
    }

    public float getThreshold3() {
        return threshold3;
    }

    public void setThreshold3(float threshold3) {
        this.threshold3 = threshold3;
    }

    public int getSaveCount() {
        return saveCount;
    }

    public int getSavePosition() {
        return savePosition;
    }

    public void setSavePosition(int position) {
        this.savePosition = position;
    }

    public void setDiaplayPosition(int position) {
        this.displayPosition = position;
    }
    public int getDiaplayPosition() {
        return displayPosition;
    }

    public void setSaveCount(int saveCount) {
        this.saveCount = saveCount;
    }

    public int getDisplayCount() {
        return displayCount;
    }

    public void setDisplayCount(int displayCount) {
        this.displayCount = displayCount;
    }

    public int getDpiWidth() {
        return dpiWidth;
    }

    public void setDpiWidth(int dpiWidth) {
        this.dpiWidth = dpiWidth;
    }

    public int getDpiHeight() {
        return dpiHeight;
    }

    public void setDpiHeight(int dpiHeight) {
        this.dpiHeight = dpiHeight;
    }

    public int getDpiCount() {
        return dpiCount;
    }

    public void setDpiCount(int dpiCount) {
        this.dpiCount = dpiCount;
    }

    public void setAlarmType(int type) {
        this.alarmType = type;
    }

    public int getAlarmType() {
        return this.alarmType;
    }

    public void setMinEyesDistance(int eyesDistance) {
        this.minEyesDistance = eyesDistance;
    }

    public int getMinEyesDistance() {
        return this.minEyesDistance;
    }
}

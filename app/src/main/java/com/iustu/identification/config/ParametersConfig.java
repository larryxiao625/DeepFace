package com.iustu.identification.config;

import android.content.SharedPreferences;

import com.iustu.identification.App;
import com.iustu.identification.util.MSP;

/**
 * Created by Liu Yuchuan on 2017/11/10.
 */
public class ParametersConfig {
    private static final String KEY_THRESHOLD_VALUE_FACE = "face";
    private static final String KEY_THRESHOLD_VALUE_PAPER = "paper";
    private static final String KEY_DISPLAY_COUNT = "displayCount";
    private static final String KEY_SEX = "sex";
    private static final String KEY_AGE_MIN = "ageMin";
    private static final String KEY_AGE_MAX = "ageMax";
    private static final String KEY_LOCATION = "location";

    private static ParametersConfig mInstance;

    private static final String name = "parametersConfig";

    private ParametersConfig(){
        SharedPreferences preferences = MSP.getInstance(name);
        thresholdValueFace = preferences.getFloat(KEY_THRESHOLD_VALUE_FACE, 0.3f);
        thresholdValuePaper = preferences.getFloat(KEY_THRESHOLD_VALUE_PAPER, 0.3F);
        displayCount = preferences.getInt(KEY_DISPLAY_COUNT, 10);
        sex = preferences.getInt(KEY_SEX, 0);
        ageMin = preferences.getInt(KEY_AGE_MIN, 0);
        ageMax = preferences.getInt(KEY_AGE_MAX, 100);
        location = preferences.getString(KEY_LOCATION, "无限制");
    }

    public static ParametersConfig getInstance(){
        if(mInstance == null){
            mInstance = new ParametersConfig();
        }
        return mInstance;
    }

    public void save(){
        MSP.getInstance(name)
                .edit()
                .putFloat(KEY_THRESHOLD_VALUE_FACE, thresholdValueFace)
                .putFloat(KEY_THRESHOLD_VALUE_PAPER, thresholdValuePaper)
                .putInt(KEY_DISPLAY_COUNT, displayCount)
                .putInt(KEY_SEX, sex)
                .putInt(KEY_AGE_MIN, ageMin)
                .putInt(KEY_AGE_MAX, ageMax)
                .putString(KEY_LOCATION, location)
                .apply();
    }

    private float thresholdValueFace;
    private float thresholdValuePaper;
    private int displayCount;
    private int sex;
    private int ageMin;
    private int ageMax;
    private String location;

    public static String getKeyThresholdValueFace() {
        return KEY_THRESHOLD_VALUE_FACE;
    }

    public static String getKeyThresholdValuePaper() {
        return KEY_THRESHOLD_VALUE_PAPER;
    }

    public static String getKeyDisplayCount() {
        return KEY_DISPLAY_COUNT;
    }

    public static String getKeySex() {
        return KEY_SEX;
    }

    public static String getKeyAgeMin() {
        return KEY_AGE_MIN;
    }

    public static String getKeyAgeMax() {
        return KEY_AGE_MAX;
    }

    public static String getKeyLocation() {
        return KEY_LOCATION;
    }

    public static ParametersConfig getmInstance() {
        return mInstance;
    }

    public static void setmInstance(ParametersConfig mInstance) {
        ParametersConfig.mInstance = mInstance;
    }

    public float getThresholdValueFace() {
        return thresholdValueFace;
    }

    public void setThresholdValueFace(float thresholdValueFace) {
        this.thresholdValueFace = thresholdValueFace;
    }

    public float getThresholdValuePaper() {
        return thresholdValuePaper;
    }

    public void setThresholdValuePaper(float thresholdValuePaper) {
        this.thresholdValuePaper = thresholdValuePaper;
    }

    public int getDisplayCount() {
        return displayCount;
    }

    public void setDisplayCount(int displayCount) {
        this.displayCount = displayCount;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAgeMin() {
        return ageMin;
    }

    public void setAgeMin(int ageMin) {
        this.ageMin = ageMin;
    }

    public int getAgeMax() {
        return ageMax;
    }

    public void setAgeMax(int ageMax) {
        this.ageMax = ageMax;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

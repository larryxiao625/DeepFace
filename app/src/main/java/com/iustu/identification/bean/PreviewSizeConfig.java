package com.iustu.identification.bean;

import com.google.gson.Gson;
import com.iustu.identification.util.MSP;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PreviewSizeConfig {
    private  static List<Integer> previewHeight=new ArrayList<>();
    private  static List<Integer> previewWidth=new ArrayList<>();
    static Gson gson=new Gson();

    public PreviewSizeConfig(){

    }

    public PreviewSizeConfig(List<Integer> previewHeight, List<Integer> previewWidth) {
        this.previewHeight = previewHeight;
        this.previewWidth = previewWidth;
    }

    public String toJsonString(){
        return gson.toJson(this);
    }

    public static PreviewSizeConfig fromJsonString(String json){
        if(json==null){
            previewHeight.add(640);
            previewHeight.add(160);
            previewHeight.add(160);
            previewHeight.add(176);
            previewHeight.add(320);
            previewHeight.add(320);
            previewHeight.add(352);
            previewHeight.add(432);
            previewHeight.add(640);
            previewHeight.add(800);
            previewHeight.add(800);
            previewHeight.add(864);
            previewHeight.add(960);
            previewHeight.add(1024);
            previewHeight.add(1280);
            previewHeight.add(1600);
            previewHeight.add(1920);
            previewHeight.add(2304);
            previewHeight.add(2304);
            previewWidth.add(480);
            previewWidth.add(90);
            previewWidth.add(120);
            previewWidth.add(144);
            previewWidth.add(180);
            previewWidth.add(240);
            previewWidth.add(288);
            previewWidth.add(240);
            previewWidth.add(360);
            previewWidth.add(448);
            previewWidth.add(600);
            previewWidth.add(480);
            previewWidth.add(720);
            previewWidth.add(576);
            previewWidth.add(720);
            previewWidth.add(896);
            previewWidth.add(1080);
            previewWidth.add(1296);
            previewWidth.add(1536);
            return new PreviewSizeConfig();
        }else {
            return gson.fromJson(json, PreviewSizeConfig.class);
        }
    }

    public void save(){
        String json=this.toJsonString();
        MSP.getInstance(MSP.SP_PREVIEW_SIZE).edit().putString(MSP.SP_PREVIEW_SIZE,json).apply();
    }
    public List<Integer> getPreviewHeight() {
        return previewHeight;
    }

    public static PreviewSizeConfig getFramSp(){
        return fromJsonString(MSP.getInstance(MSP.SP_PREVIEW_SIZE).getString(MSP.SP_PREVIEW_SIZE,new PreviewSizeConfig().toJsonString()));
    }

    public void setPreviewHeight(List<Integer> previewHeight) {
        this.previewHeight = previewHeight;
    }

    public List<Integer> getPreviewWidth() {
        return previewWidth;
    }

    public void setPreviewWidth(List<Integer> previewWidth) {
        this.previewWidth = previewWidth;
    }

}

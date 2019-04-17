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
            previewWidth.add(640);
            previewWidth.add(160);
            previewWidth.add(160);
            previewWidth.add(176);
            previewWidth.add(320);
            previewWidth.add(320);
            previewWidth.add(352);
            previewWidth.add(432);
            previewWidth.add(640);
            previewWidth.add(800);
            previewWidth.add(800);
            previewWidth.add(864);
            previewWidth.add(960);
            previewWidth.add(1024);
            previewWidth.add(1280);
            previewWidth.add(1600);
            previewWidth.add(1920);
            previewWidth.add(2304);
            previewWidth.add(2304);
            previewHeight.add(480);
            previewHeight.add(90);
            previewHeight.add(120);
            previewHeight.add(144);
            previewHeight.add(180);
            previewHeight.add(240);
            previewHeight.add(288);
            previewHeight.add(240);
            previewHeight.add(360);
            previewHeight.add(448);
            previewHeight.add(600);
            previewHeight.add(480);
            previewHeight.add(720);
            previewHeight.add(576);
            previewHeight.add(720);
            previewHeight.add(896);
            previewHeight.add(1080);
            previewHeight.add(1296);
            previewHeight.add(1536);
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

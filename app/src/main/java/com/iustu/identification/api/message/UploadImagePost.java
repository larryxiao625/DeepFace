package com.iustu.identification.api.message;

public class UploadImagePost {

    /**
     * deviceId : ZTTTCKXZD00043 设备编号
     * snapTime : 2018-11-28 15:15:37.321 照片抓拍时间、精确到毫秒
     * imageContent : iVBORw0KGgoAAAANSUhEUgAAAd4AAAI0CAYAAACzqUi6AAAMImlDQ1BJ Q0MgUHJvZmlsZQAASImVlwdUU0kXx+eVVBJaIAJSQu9K 图片内容的Base64编码
     */

    private String deviceId;
    private String snapTime;
    private String imageContent;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSnapTime() {
        return snapTime;
    }

    public void setSnapTime(String snapTime) {
        this.snapTime = snapTime;
    }

    public String getImageContent() {
        return imageContent;
    }

    public void setImageContent(String imageContent) {
        this.imageContent = imageContent;
    }
}

package com.iustu.identification.api.message.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Liu Yuchuan on 2017/11/28.
 */

public class PeopleInfoRequest {
    @SerializedName("face_set_id")
    private String faceSetId;
    private String id;

    public String getFaceSetId() {
        return faceSetId;
    }

    public void setFaceSetId(String faceSetId) {
        this.faceSetId = faceSetId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

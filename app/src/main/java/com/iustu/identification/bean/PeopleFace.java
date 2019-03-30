package com.iustu.identification.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Liu Yuchuan on 2017/12/4.
 */

public class PeopleFace {
    @SerializedName("face_set_id")
    private String faceSetId;
    @SerializedName("people_id")
    private String peopleId;
    private String id;
    private String image;

    public String getFaceSetId() {
        return faceSetId;
    }

    public void setFaceSetId(String faceSetId) {
        this.faceSetId = faceSetId;
    }

    public String getPeopleId() {
        return peopleId;
    }

    public void setPeopleId(String peopleId) {
        this.peopleId = peopleId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

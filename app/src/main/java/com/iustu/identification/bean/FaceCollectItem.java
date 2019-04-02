package com.iustu.identification.bean;

import com.iustu.identification.api.Api;
import com.iustu.identification.api.message.response.SearchImageHistoryResponse;

import org.litepal.crud.DataSupport;

/**
 * Created by Liu Yuchuan on 2017/11/22.
 */

public class FaceCollectItem extends DataSupport{
    private String imgUrl;
    private String time;
    private String faceId;
    private int id;

    public FaceCollectItem(SearchImageHistoryResponse.Result result){
        this.time = result.getTime();
        this.faceId = result.getId();
        this.id = faceId.hashCode();
        imgUrl = Api.getFaceSearchImageUrl(faceId);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFaceId() {
        return faceId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

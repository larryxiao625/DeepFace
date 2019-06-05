package com.iustu.identification.api;


import com.iustu.identification.api.message.UploadImageCallBack;
import com.iustu.identification.api.message.UploadImagePost;

import javax.security.auth.Subject;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class Api {

    static String API_JSON_FORAMT="application/json";

    /**
     * 抓拍图片上传接口
     * @param picBase64 Base64图片编码
     * @param snapTime 抓拍时间（精确到毫秒）
     * @return
     */
    public static Observable<UploadImageCallBack> uploadImageCallBackObservable(String picBase64, String snapTime){
        UploadImagePost uploadImagePost=new UploadImagePost(snapTime,picBase64);
        return ApiManager.getInstance()
                .getApi()
                .uploadImage(uploadImagePost)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}

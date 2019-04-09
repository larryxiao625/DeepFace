package com.iustu.identification.ui.main.library.addperson.mvp;

import android.content.ContentValues;

import com.example.agin.facerecsdk.DetectResult;
import com.example.agin.facerecsdk.FeatureResult;
import com.iustu.identification.entity.PersonInfo;
import com.iustu.identification.ui.main.library.addperson.AddPersonFragment;
import com.iustu.identification.util.RxUtil;
import com.iustu.identification.util.SDKUtil;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * created by sgh， 2019-4-3
 */
public class AddPersionPresenter {
    private Disposable disposable;

    AddPersionModel model;
    AddPersionView view;

    int NO_FACE=0; //未检测到人脸
    int MORE_THAN_ONE_FACE=2; //检测到多张人脸


    public AddPersionPresenter(AddPersionModel model) {
        this.model = model;
    }

    public void setView(AddPersionView view) {
        this.view = view;
    }
    /**
     * 点击“提交”按钮时触发
     */
    public void onAddPersion(PersonInfo personInfo) {
        view.showWaitDialog("正在添加...");
        ContentValues values = new ContentValues();
        values.put("feature", System.currentTimeMillis() + "");
        values.put("libId", personInfo.libId);
        values.put("name", personInfo.name);
        values.put("gender", personInfo.gender);
        values.put("photoPath", personInfo.photoPath);
        values.put("identity", personInfo.identity);
        values.put("home", personInfo.home);
        values.put("other", personInfo.other);
        Observable observable = RxUtil.getInsertObservable(RxUtil.DB_PERSIONINFO, values);
        observable.subscribe(new Observer<Object>() {

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                // 调用onError说明添加失败
                view.onAddError(e.getMessage());
            }

            @Override
            public void onComplete() {
                ((AddPersonFragment)view).clear();
                disposable.dispose();
                view.dissmissDialog();
                view.onAddSuccess();
            }
        });
    }

    /**
     * 添加人脸信息调用方法
     * @param picPath 图片路径
     * @return 特征数据长度(否则返回 FACEREC_ERROR)
     */
    public int getPersonFeat(String picPath){
        DetectResult detectResult=new DetectResult();
        int num=SDKUtil.getDetectHandler().faceDetector(picPath,detectResult);
        if(num==1){
            FeatureResult featureResult=new FeatureResult();
            return SDKUtil.getVerifyHandler().extractFeature(detectResult,featureResult);
        }else if(num==0){
            return NO_FACE;
        }else {
            return MORE_THAN_ONE_FACE;
        }
    }
}

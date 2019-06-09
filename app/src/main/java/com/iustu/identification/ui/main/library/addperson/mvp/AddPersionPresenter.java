package com.iustu.identification.ui.main.library.addperson.mvp;

import android.content.ContentValues;

import com.example.agin.facerecsdk.DetectResult;
import com.example.agin.facerecsdk.FeatureResult;
import com.iustu.identification.entity.PersionInfo;
import com.iustu.identification.ui.main.library.addperson.AddPersonFragment;
import com.iustu.identification.util.RxUtil;
import com.iustu.identification.util.SDKUtil;
import com.iustu.identification.util.ToastUtil;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * created by sgh， 2019-4-3
 */
public class AddPersionPresenter {
    private Disposable disposable;
    private FeatureResult featureResult;        // 用来保存人脸特征提取的结果

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
    public void onAddPersion(PersionInfo persionInfo) {
        view.showWaitDialog("正在添加...");
        int result = getPersonFeat(persionInfo.photoPath);
        if (result == NO_FACE || result == MORE_THAN_ONE_FACE) {
            ToastUtil.show("该图片含有多个人脸或者不含人脸，添加失败...");
            view.dissmissDialog();
            return;
        }
        Observable<Integer> observable = RxUtil.getAddPersionObservable(persionInfo);
        observable.subscribe(new Observer<Integer>() {

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Integer o) {
                if (o == -1) {
                    ToastUtil.show("sdkError: " + o.intValue());
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                // 调用onError说明添加失败
                view.onAddError(e.getMessage());
                view.dissmissDialog();
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
            featureResult=new FeatureResult();
            int result = SDKUtil.getVerifyHandler().extractFeature(detectResult,featureResult);
            return result;
        }else if(num==0){
            return NO_FACE;
        }else {
            return MORE_THAN_ONE_FACE;
        }
    }
}

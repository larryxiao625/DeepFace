package com.iustu.identification.ui.main.camera.prenster;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.widget.ImageView;

import com.example.agin.facerecsdk.DetectResult;
import com.example.agin.facerecsdk.FeatureResult;
import com.example.agin.facerecsdk.HandlerFactory;
import com.example.agin.facerecsdk.SearchDBItem;
import com.example.agin.facerecsdk.SearchHandler;
import com.example.agin.facerecsdk.SearchResultItem;
import com.iustu.identification.util.RxUtil;
import com.iustu.identification.util.SDKUtil;
import com.iustu.identification.util.TextUtil;
import com.jiangdg.usbcamera.UVCCameraHelper;
import com.serenegiant.usb.common.AbstractUVCCameraHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.SafeObserver;

public class CapturePicService extends Service implements AbstractUVCCameraHandler.OnCaptureListener {
    UVCCameraHelper cameraHelper=UVCCameraHelper.getInstance();
    String picPath= Environment.getExternalStorageDirectory()+"/DeepFace";
    private CameraPrenster cameraPrenster;
    CaptureBind mBind;
    SearchHandler searchHandler;
    public static String path=Environment.getExternalStorageDirectory()+"/DeepFace/faceLib";
    public class CaptureBind extends Binder{
        public CapturePicService getService(){
            return CapturePicService.this;
        }
        public void setOnMyDevConnectListener(CameraPrenster cameraPrenster){
            CapturePicService.this.cameraPrenster=cameraPrenster;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBind=new CaptureBind();
        File file=new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
        searchHandler= (SearchHandler) HandlerFactory.createSearcher(path,0,1);
        /**
         * 测试数据
         */
        ArrayList<String> picPaths=new ArrayList<>();
        picPaths.add(picPath+"/2019-04-09 20:44:42.jpg");
        ArrayList<DetectResult> detectResults=SDKUtil.detectFace(picPaths);
        if(detectResults.size()!=0){
            FeatureResult featureResult=SDKUtil.featureResult(detectResults);
            if(featureResult.getAllFeats().size()!=0){
                ArrayList<SearchDBItem> searchDBItems=new ArrayList<>();
                for(float[] floats:featureResult.getFeat(0)){
                    SearchDBItem searchDBItem=new SearchDBItem();
                    searchDBItem.feat=floats;
                    searchDBItem.image_id="test";
                    searchDBItems.add(searchDBItem);
                }
                setSearchLib(searchDBItems);
            }

        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @SuppressLint("CheckResult")
    public void capturePic() {
        createDir(picPath);
        Observable.interval(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        cameraHelper.capturePicture(picPath+"/"+ TextUtil.dateMessage(Calendar.getInstance().getTime())+".jpg",CapturePicService.this::onCaptureResult);
                        Log.d("CameraFragment",TextUtil.dateMessage(Calendar.getInstance().getTime()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void createDir(String dirPath) {
        File file=new File(dirPath);
        if(!file.exists()){
            Log.d("Camera", String.valueOf(file.mkdirs()));
        }
    }

    public void setSearchLib(ArrayList<SearchDBItem> searchDBItems){
        searchHandler.searchBuildLib(searchDBItems);
        capturePic();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("CapturePicService","onBind");
        return mBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCaptureResult(String picPath) {
        ArrayList<String> picPaths=new ArrayList<>();
        picPaths.add(picPath);
        getVerify(SDKUtil.detectFace(picPaths));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Camera","onDestroy");
    }
    public void getVerify(ArrayList<DetectResult> detectResults){
        if(detectResults.size()!=0){
            FeatureResult featureResult=SDKUtil.featureResult(detectResults);
            if(featureResult.getAllFeats().size()!=0){
                for(ArrayList<float[]> arrayList:featureResult.getAllFeats()){
                    for(float[] floats:arrayList){
                        searchFace(floats);
                    }
                }
            }
        }
    }
    public void searchFace(float[] feat){
        ArrayList<SearchResultItem> searchResultItems=new ArrayList<>(100);
        searchHandler.searchFind(feat,1,searchResultItems, (float) 0.85);
        Log.d("CameraSearchSize", String.valueOf(searchResultItems.size()));
        if(searchResultItems.size()!=0){
            Log.d("CameraSearch", String.valueOf(searchResultItems.get(0).score));
        }
    }

}

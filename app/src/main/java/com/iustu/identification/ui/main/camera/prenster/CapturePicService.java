package com.iustu.identification.ui.main.camera.prenster;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.agin.facerecsdk.DetectResult;
import com.example.agin.facerecsdk.FeatureResult;
import com.example.agin.facerecsdk.SearchDBItem;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DefaultObserver;

public class CapturePicService extends Service implements AbstractUVCCameraHandler.OnCaptureListener {
    UVCCameraHelper cameraHelper=UVCCameraHelper.getInstance();
    String picPath= Environment.getExternalStorageDirectory()+"/DeepFace";
    private CameraPrenster cameraPrenster;
    CaptureBind mBind;
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
        /**
         * 测试数据
         */
        ArrayList<SearchDBItem> searchDBItems=new ArrayList<>();
        ArrayList<String> picPaths=new ArrayList<>();
        picPaths.add(picPath+"/2019-04-08 21:42:18.jpg");
        RxUtil.getDetectObservable(picPaths).subscribe(new Consumer<ArrayList<DetectResult>>() {
            @Override
            public void accept(ArrayList<DetectResult> o) throws Exception {
                RxUtil.getFeatureResultObservable(o).subscribe(new Consumer<FeatureResult>() {
                    @Override
                    public void accept(FeatureResult o) throws Exception {
                        ArrayList<SearchDBItem> searchDBItems1=new ArrayList<>();
                        SearchDBItem searchDBItem=new SearchDBItem();
                        searchDBItem.feat=o.getFeat(0).get(0);
                        searchDBItem.image_id="test";
                        searchDBItems1.add(searchDBItem);
                        Log.d("Camera", String.valueOf(searchDBItems1.size()));
                        setSearchLib(searchDBItems1);
                    }
                });
            }
        });
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
                .subscribe(c-> {
                    cameraHelper.capturePicture(picPath+"/"+ TextUtil.dateMessage(Calendar.getInstance().getTime())+".jpg",this);
                    Log.d("CameraFragment",TextUtil.dateMessage(Calendar.getInstance().getTime()));
                });
    }

    public void createDir(String dirPath) {
        File file=new File(dirPath);
        if(!file.exists()){
            Log.d("Camera", String.valueOf(file.mkdirs()));
        }
    }

    public void setSearchLib(ArrayList<SearchDBItem> searchDBItems){
        SDKUtil.getSearchHandler().searchBuildLib(searchDBItems);
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
        RxUtil.getDetectObservable(picPaths).subscribe((Consumer<ArrayList<DetectResult>>) o -> {
            getVerify(o);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Camera","onDestroy");
    }
    public void getVerify(ArrayList<DetectResult> detectResults){
        RxUtil.getFeatureResultObservable(detectResults).subscribe((Consumer<FeatureResult>) f -> searchFace(f.getFeat(0).get(0)));
        Log.d("Camera","getVerify");
    }
    public void searchFace(float[] feat){
        ArrayList<SearchResultItem> searchResultItems=new ArrayList<>();
        RxUtil.getSearchFaceObservable(feat).subscribe((Consumer<SearchResultItem>) o -> {
            Log.d("Camera", String.valueOf(o.score));
        });
    }

}

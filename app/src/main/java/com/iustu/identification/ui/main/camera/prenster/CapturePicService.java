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
import com.iustu.identification.util.SDKUtil;
import com.iustu.identification.util.TextUtil;
import com.jiangdg.usbcamera.UVCCameraHelper;
import com.serenegiant.usb.common.AbstractUVCCameraHandler;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @SuppressLint("CheckResult")
    public void capturePic() {
        createDir(picPath);
        Observable.interval(500, TimeUnit.MILLISECONDS)
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
        DetectResult detectResult=new DetectResult();
        Log.d("Camera", String.valueOf(SDKUtil.getDetectHandler().faceDetector(picPath,detectResult)));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Camera","onDestroy");
    }

}

package com.iustu.identification.ui.main.camera.prenster;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;

import com.iustu.identification.ui.main.camera.view.IVew;
import com.iustu.identification.util.TextUtil;
import com.jiangdg.usbcamera.UVCCameraHelper;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.common.AbstractUVCCameraHandler;
import com.serenegiant.usb.widget.CameraViewInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CameraPrenster implements UVCCameraHelper.OnMyDevConnectListener,IPenster, AbstractUVCCameraHandler.OnCaptureListener {
    boolean isRequest=false;
    UVCCameraHelper cameraHelper= UVCCameraHelper.getInstance();
    IVew iVew;
    String picPath= Environment.getExternalStorageDirectory()+"/DeepFace";
    @Override
    public void onAttachDev(UsbDevice device) {
        Log.d("CameraPrenster","attachDev");
        //申请相机权限
        if(!isRequest){
            isRequest=true;
            if(cameraHelper!=null){
                cameraHelper.requestPermission(0);
            }
        }

    }

    @Override
    public void onDettachDev(UsbDevice device) {
        Log.d("CameraPrenster","dettachDev");
        //释放camera资源
        if(isRequest){
            isRequest=false;
            cameraHelper.closeCamera();
        }
    }

    @Override
    public void onConnectDev(UsbDevice device, boolean isConnected) {
        Log.d("cameraPrenster","cameraConnect");
        iVew.showShortMsg("摄像头已连接");
    }

    @Override
    public void onDisConnectDev(UsbDevice device) {
        Log.d("cameraPrenster","cameraDisConnect");
        iVew.showShortMsg("摄像头已断开连接");
    }

    @Override
    public void attchView(IVew iVew) {
        this.iVew=iVew;
    }

    @Override
    public void capturePic() {
        createDir(picPath);
        Observable.interval(2000,TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(c-> {
                    cameraHelper.capturePicture(picPath+"/"+TextUtil.dateMessage(Calendar.getInstance().getTime())+".jpg",this);
                    Log.d("CameraFragment",TextUtil.dateMessage(Calendar.getInstance().getTime()));
                });
    }

    @Override
    public void createDir(String dirPath) {
        File file=new File(dirPath);
        if(!file.exists()){
            Log.d("Camera", String.valueOf(file.mkdirs()));
        }
    }

    @Override
    public void onCaptureResult(String picPath) {
        Log.d("Camera",picPath);
    }
}

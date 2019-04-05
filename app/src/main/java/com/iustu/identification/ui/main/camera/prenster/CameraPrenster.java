package com.iustu.identification.ui.main.camera.prenster;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.view.Surface;

import com.iustu.identification.ui.main.camera.view.IVew;
import com.jiangdg.usbcamera.UVCCameraHelper;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.widget.CameraViewInterface;

public class CameraPrenster implements UVCCameraHelper.OnMyDevConnectListener,IPenster {
    boolean isRequest=false;
    UVCCameraHelper cameraHelper= UVCCameraHelper.getInstance();
    IVew iVew;
    @Override
    public void onAttachDev(UsbDevice device) {
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

}

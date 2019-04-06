package com.iustu.identification.ui.main.camera.view;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iustu.identification.App;
import com.iustu.identification.R;
import com.iustu.identification.bean.ParameterConfig;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.main.MainActivity;
import com.iustu.identification.ui.main.camera.prenster.CameraPrenster;
import com.iustu.identification.ui.widget.camera.CameraPreview;
import com.iustu.identification.ui.widget.dialog.SingleButtonDialog;
import com.iustu.identification.ui.widget.dialog.WaitProgressDialog;
import com.iustu.identification.util.DataCache;
import com.iustu.identification.util.ExceptionUtil;
import com.iustu.identification.util.FileCallBack;
import com.iustu.identification.util.IconFontUtil;
import com.iustu.identification.util.ImageUtils;
import com.jiangdg.usbcamera.UVCCameraHelper;
import com.serenegiant.usb.widget.CameraViewInterface;
import com.serenegiant.usb.widget.UVCCameraTextureView;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Liu Yuchuan on 2017/11/4.
 */

public class CameraFragment extends BaseFragment implements CameraViewInterface.Callback {
    boolean isPreview=false;
    boolean isFirstTime=true;
    UVCCameraHelper cameraHelper=UVCCameraHelper.getInstance();
    @BindView(R.id.photo_layout)
    FrameLayout frameLayout;
    @BindView(R.id.capture_uvc_camera)
    UVCCameraTextureView cameraTextureView;

    CameraPrenster cameraPrenster=new CameraPrenster();

    @Override
    protected int postContentView() {
        return R.layout.fragment_camera;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        Log.d("CameraFragment","initView");
        IconFontUtil util = IconFontUtil.getDefault();
        cameraPrenster.attchView(iVew);
        if(cameraHelper.getUSBMonitor()==null) {
            Log.d("CameraFragment","initMonitor");
            cameraHelper.setDefaultPreviewSize(1920, 1080);
            cameraHelper.initUSBMonitor(getActivity(),cameraTextureView,cameraPrenster);
        }else {
            Log.d("CameraFragment","updateDpi");
            cameraHelper.updateResolution(DataCache.getParameterConfig().getDpiWidth(), DataCache.getParameterConfig().getDpiHeight());
        }
        cameraTextureView.setCallback(this);
        cameraHelper.registerUSB();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden&&cameraTextureView!=null){
            cameraHelper.unregisterUSB();
            if(cameraHelper.getUsbDeviceCount()!=0){
                cameraPrenster.onDettachDev(cameraHelper.getUsbDeviceList().get(0));
            }
            cameraHelper.release();
            cameraTextureView.onPause();
        }
    }

    @Override
    public void onHide() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSurfaceCreated(CameraViewInterface view, Surface surface) {
        Log.d("CameraFragment","onSurfaceCreated");
        Log.d("CameraFragment",String.valueOf(cameraHelper.isCameraOpened()));
        Log.d("CameraFragment","IsPreview:"+isPreview);
        Log.d("CameraFragment","IsFirstTime:"+isFirstTime);
        if(!isPreview&&cameraHelper.isCameraOpened()){
            Log.d("CameraFragment","startPreview");
            cameraHelper.startPreview(cameraTextureView);
            isPreview=true;
        }
    }

    @Override
    public void onSurfaceChanged(CameraViewInterface view, Surface surface, int width, int height) {
    }

    @Override
    public void onSurfaceDestroy(CameraViewInterface view, Surface surface) {
        Log.d("CameraFragment","surfaceDestroy");
        if(isPreview&&cameraHelper.isCameraOpened()){
            Log.d("CameraFragment","stopPreview");
            cameraHelper.stopPreview();
            isPreview=false;
        }else if(isFirstTime && !cameraHelper.isCameraOpened()){
            Log.d("CameraFragment","stopPreview");
            cameraHelper.stopPreview();
            isPreview=false;
            isFirstTime=false;
        }
    }

    IVew iVew= rea -> Toast.makeText(App.getContext(),rea,Toast.LENGTH_SHORT).show();

}

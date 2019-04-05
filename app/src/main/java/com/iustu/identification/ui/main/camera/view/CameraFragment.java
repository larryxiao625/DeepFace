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

import com.iustu.identification.R;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.main.MainActivity;
import com.iustu.identification.ui.main.camera.prenster.CameraPrenster;
import com.iustu.identification.ui.widget.camera.CameraPreview;
import com.iustu.identification.ui.widget.dialog.SingleButtonDialog;
import com.iustu.identification.ui.widget.dialog.WaitProgressDialog;
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
        if(cameraHelper.getUSBMonitor()==null) {
            cameraHelper.setDefaultPreviewSize(1280, 720);
        }
        cameraTextureView.setCallback(this);
        cameraHelper.initUSBMonitor(getActivity(),cameraTextureView,cameraPrenster);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d("CameraFragment","hideChange");
        if(hidden) {

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        onHide();
        cameraHelper.unregisterUSB();
    }

    @Override
    public void onStop() {
        super.onStop();
        cameraHelper.unregisterUSB();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraHelper.unregisterUSB();
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
        if(!isPreview&&cameraHelper.isCameraOpened()){
            cameraHelper.startPreview(cameraTextureView);
            isPreview=true;
        }
    }

    @Override
    public void onSurfaceChanged(CameraViewInterface view, Surface surface, int width, int height) {

    }

    @Override
    public void onSurfaceDestroy(CameraViewInterface view, Surface surface) {
        if(isPreview&&cameraHelper.isCameraOpened()){
            cameraHelper.stopPreview();
            isPreview=false;
        }
    }

    IVew iVew=new IVew() {
        @Override
        public void showShortMsg(String rea) {
            Toast.makeText(getActivity(),rea,Toast.LENGTH_SHORT);
        }
    };

}

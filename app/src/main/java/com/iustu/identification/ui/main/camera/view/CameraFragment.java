package com.iustu.identification.ui.main.camera.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.iustu.identification.App;
import com.iustu.identification.R;
import com.iustu.identification.bean.ParameterConfig;
import com.iustu.identification.bean.PreviewSizeConfig;
import com.iustu.identification.entity.CompareRecord;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.main.camera.adapter.CatchFaceAdapter;
import com.iustu.identification.ui.main.camera.adapter.CompareItemAdapter;
import com.iustu.identification.ui.main.camera.prenster.CameraPrenster;
import com.iustu.identification.ui.main.camera.prenster.CapturePicService;
import com.iustu.identification.util.AlarmUtil;
import com.iustu.identification.util.IconFontUtil;
import com.jiangdg.usbcamera.UVCCameraHelper;
import com.serenegiant.usb.common.AbstractUVCCameraHandler;
import com.serenegiant.usb.widget.CameraViewInterface;
import com.serenegiant.usb.widget.UVCCameraTextureView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

/**
 * Created by Liu Yuchuan on 2017/11/4.
 */

public class CameraFragment extends BaseFragment implements CameraViewInterface.Callback, AbstractUVCCameraHandler.OnPreViewResultListener {
    boolean isPreview=false;
    boolean isFirstTime=true;
    UVCCameraHelper cameraHelper=UVCCameraHelper.getInstance();
    @BindView(R.id.photo_layout)
    FrameLayout frameLayout;
    @BindView(R.id.capture_uvc_camera)
    UVCCameraTextureView cameraTextureView;
    @BindView(R.id.item_compare_recycler_view)
    RecyclerView itemCompareRecyclerView;
    @BindView(R.id.item_capture_recycler_view)
    RecyclerView itemCaptureRecyclerView;
    CameraPrenster cameraPrenster=new CameraPrenster();
    Intent serviceIntent;
    CapturePicService.CaptureBind captureBind;

    private List<CompareRecord> dataSource;
    private CompareItemAdapter compareItemAdapter;
    private CatchFaceAdapter catchFaceAdapter;
    List<String> capturePathString;
    @Override
    protected int postContentView() {
        return R.layout.fragment_camera;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        Log.d("CameraFragment","initView");
        IconFontUtil util = IconFontUtil.getDefault();
        dataSource = new ArrayList<>();
        capturePathString=new ArrayList<>();
        compareItemAdapter = new CompareItemAdapter(dataSource);
        cameraPrenster.attchView(iVew);
        catchFaceAdapter=new CatchFaceAdapter(capturePathString);
        serviceIntent=new Intent(getActivity(), CapturePicService.class);
        cameraHelper.setOnPreviewFrameListener(this);
        if(cameraHelper.getUSBMonitor()==null) {
            Log.d("CameraFragment","initMonitor");
            cameraHelper.setDefaultPreviewSize(ParameterConfig.getFromSP().getDpiWidth(), ParameterConfig.getFromSP().getDpiHeight());
            cameraHelper.initUSBMonitor(getActivity(),cameraTextureView,cameraPrenster);
        }else {
            cameraHelper.updateResolution(ParameterConfig.getFromSP().getDpiWidth(), ParameterConfig.getFromSP().getDpiHeight());
        }
        cameraTextureView.setCallback(this);
        cameraHelper.registerUSB();
        itemCompareRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        itemCompareRecyclerView.setAdapter(compareItemAdapter);
//        cameraPrenster.capturePic();
        getActivity().bindService(serviceIntent,myServiceConnection,Context.BIND_AUTO_CREATE);
        RecyclerView.LayoutManager captureManager=new LinearLayoutManager(getActivity());
        ((LinearLayoutManager) captureManager).setOrientation(LinearLayoutManager.HORIZONTAL);
        itemCaptureRecyclerView.setLayoutManager(captureManager);
        itemCaptureRecyclerView.setAdapter(catchFaceAdapter);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d("CameraFragment","onHidden");
        if(hidden&&cameraTextureView!=null){
            cameraHelper.unregisterUSB();
            if(cameraHelper.getUsbDeviceCount()!=0){
                cameraPrenster.onDettachDev(cameraHelper.getUsbDeviceList().get(0));
                        getActivity().unbindService(myServiceConnection);
            }
            cameraHelper.release();
            cameraTextureView.onPause();
            Objects.requireNonNull(getActivity()).stopService(serviceIntent);
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
        if(isPreview&&cameraHelper.isCameraOpened()){
            cameraHelper.stopPreview();
            isPreview=false;
        }else if(isFirstTime && !cameraHelper.isCameraOpened()){
            cameraHelper.stopPreview();
            isPreview=false;
            isFirstTime=false;
        }
    }

    IVew iVew=new IVew() {
        @Override
        public void showShortMsg(String rea) {
            Toast.makeText(App.getContext(),rea,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void updateSingleResult(CompareRecord compareRecord) {
            dataSource.add(0,compareRecord);
            compareItemAdapter.notifyItemInserted(0);
            if(itemCompareRecyclerView == null)
                return;
            if(!itemCompareRecyclerView.canScrollVertically(-1)) {
                itemCompareRecyclerView.smoothScrollToPosition(0);
            }
            AlarmUtil.alarm();
        }

        @Override
        public void updateCapture(String capturePic) {
            capturePathString.add(0,capturePic);
            catchFaceAdapter.notifyItemInserted(0);
            if(itemCaptureRecyclerView==null){
                return;
            }else {
                itemCaptureRecyclerView.smoothScrollToPosition(0);
            }
        }
    };

    @Override
    public void onPreviewResult(byte[] data) {
        if(data==null){
            PreviewSizeConfig previewSizeConfig=PreviewSizeConfig.getFramSp();
            Toast.makeText(getActivity(),"选择分辨率无效,恢复到默认分辨率",Toast.LENGTH_LONG).show();
            cameraHelper.updateResolution(previewSizeConfig.getPreviewWidth().get(previewSizeConfig.getPreviewWidth().size()-1),previewSizeConfig.getPreviewHeight().get(previewSizeConfig.getPreviewWidth().size()-1));
            ParameterConfig parameterConfig=ParameterConfig.getFromSP();
            parameterConfig.setDpiHeight(previewSizeConfig.getPreviewHeight().get(previewSizeConfig.getPreviewWidth().size())-1);
            parameterConfig.setDpiWidth(previewSizeConfig.getPreviewWidth().get(previewSizeConfig.getPreviewWidth().size())-1);
            parameterConfig.save();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("CameraFragment","onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("CameraFragment","onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("CameraFragment","onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("CameraFragment","onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("CameraFragment","onDestroy");
        Objects.requireNonNull(getActivity()).stopService(serviceIntent);
    }

    ServiceConnection myServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("CameraFragment","onConnected");
            CameraFragment.this.captureBind= (CapturePicService.CaptureBind) service;
            ((CapturePicService.CaptureBind) service).setOnMyDevConnectListener(cameraPrenster);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            CameraFragment.this.captureBind=null;
        }
    };


}

package com.iustu.identification.ui.main.camera;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.iustu.identification.R;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.main.MainActivity;
import com.iustu.identification.ui.widget.camera.CameraPreview;
import com.iustu.identification.ui.widget.dialog.SingleButtonDialog;
import com.iustu.identification.ui.widget.dialog.WaitProgressDialog;
import com.iustu.identification.util.ExceptionUtil;
import com.iustu.identification.util.FileCallBack;
import com.iustu.identification.util.IconFontUtil;
import com.iustu.identification.util.ImageUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Liu Yuchuan on 2017/11/4.
 */

public class CameraFragment extends BaseFragment implements CameraPreview.StartPreViewCallback{

    @BindView(R.id.photo_layout)
    FrameLayout frameLayout;
    @BindView(R.id.switch_tv)
    TextView switchButton;
    @BindView(R.id.history_tv)
    TextView historyTv;
    @BindView(R.id.album_tv)
    TextView albumTv;
    @BindView(R.id.ok_iv)
    ImageView okIv;
    @BindView(R.id.cancel_iv)
    ImageView cancelIv;
    @BindView(R.id.button_layout)
    FrameLayout buttonLayout;

    private CameraPreview cameraPreview;
    private int currentCameraId;

    private boolean onTakePhoto;

    private byte[] imgBytes;

    private WaitProgressDialog waitProgressDialog;

    @Override
    protected int postContentView() {
        return R.layout.fragment_camera;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        IconFontUtil util = IconFontUtil.getDefault();
        util.setText(switchButton, IconFontUtil.CAMERA_SWITCH);
        util.setText(historyTv, IconFontUtil.HISTORY);
        util.setText(albumTv, IconFontUtil.PHOTO);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(currentCameraId != 0 && currentCameraId != 1){
            currentCameraId = 0;
        }
        frameLayout.post(()->{
            cameraPreview = new CameraPreview(mActivity, currentCameraId);
            frameLayout.addView(cameraPreview, 0);
            cameraPreview.setStartPreViewCallback(this);
            addDisposable(cameraPreview.startPreView());
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        onHide();
        frameLayout.removeView(cameraPreview);
        cameraPreview = null;
    }

    @OnClick(R.id.switch_tv)
    public void switchCamera(){
        if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }else {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        frameLayout.removeView(cameraPreview);
        cameraPreview = new CameraPreview(mActivity, currentCameraId);
        cameraPreview.setStartPreViewCallback(this);
        frameLayout.addView(cameraPreview, 0);
    }

    private void cancelTakePhoto() {
        onTakePhoto = false;
        if(cameraPreview != null) {
            cameraPreview.startPreView();
        }
    }

    @OnClick(R.id.album_tv)
    public void openAlbum(){
        ImageUtils.startChoose(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ImageUtils.REQUEST_GALLERY && resultCode == Activity.RESULT_OK){
            ((MainActivity)mActivity).switchFragment(6);
            CompareFragment compareFragment = (CompareFragment)((MainActivity)mActivity).getFragment(6);
            String path = ImageUtils.getRealPathFromUri(mActivity, data.getData());
            if(path == null){
                new SingleButtonDialog.Builder()
                        .content("获取图片路径失败")
                        .title("错误")
                        .button("确定", null)
                        .show(mActivity.getFragmentManager());
                return;
            }


            Observable<File> observable;
            int degree = ImageUtils.readPictureDegree(path);
            if(degree == 0){
                observable = Observable.just(new File(path));
            }else {
                observable = ImageUtils.modifiedSavePhoto("人脸对比照片", path, ImageUtils.readPictureDegree(path), new FileCallBack() {
                    @Override
                    public void onStartSaveFile() {
//                        waitProgressDialog = ((MainActivity)mActivity).showWaitDialog("正在处理,请勿退出", v->dispose());
                    }
                });
            }

            if(observable == null){
                onError("读取失败", v->dispose());
                return;
            }

            observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(compareFragment::addDisposable)
                    .subscribe(compareFragment::loadImg, t-> ExceptionUtil.getThrowableMessage("CameraFragment", t));

        }
    }

    public void onError(String content, View.OnClickListener action){
        new SingleButtonDialog.Builder()
                .content(content)
                .title("错误")
                .button("确定", action)
                .show(mActivity.getFragmentManager());
    }

    @OnClick(R.id.history_tv)
    public void openHistory(){
        ((MainActivity)mActivity).switchFragment(3);
        ((MainActivity)mActivity).bottomBar.setSelectPosition(3);
    }

    @OnClick(R.id.take_photo_iv)
    public void takePhoto(){
        cameraPreview.takePicture((data, camera) -> {
            imgBytes = data;
            onTakePhoto = true;
            mActivity.runOnUiThread(this::startPreviewPhoto);
        });
    }

    private void startPreviewPhoto() {
        switchButton.setVisibility(View.GONE);
        buttonLayout.setVisibility(View.GONE);
        okIv.setVisibility(View.VISIBLE);
        cancelIv.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.cancel_iv)
    public void onCancel(){
        cancelTakePhoto();
    }

    @OnClick(R.id.ok_iv)
    public void onOk(){
        CompareFragment compareFragment = (CompareFragment)((MainActivity)mActivity).getFragment(6);
        int degree = currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK? 90 : 270;
        Observable<File> observable = ImageUtils.savePhoto("人脸对比照片", imgBytes, degree, new FileCallBack() {
            @Override
            public void onStartSaveFile() {
                ((MainActivity)mActivity).switchFragment(6);
                waitProgressDialog = ((MainActivity)mActivity).showWaitDialog("正在保存照片,请勿退出", v -> dispose());
            }

            @Override
            public void onSaveFileSuccess() {
                if(waitProgressDialog != null){
                    waitProgressDialog.dismiss();
                }
            }
        });

        if(observable == null){
            onError("创建文件夹失败", v-> onCancel());
        }else {
            observable.subscribe(compareFragment::loadImg);
        }
    }

    @Override
    public void onBackPressed() {
        if(!onTakePhoto) {
            super.onBackPressed();
        }else {
            cancelTakePhoto();
        }
    }

    @Override
    public void onHide() {}

    public boolean isOnTakePhoto() {
        return onTakePhoto;
    }

    @Override
    public void onStartPreview() {
        switchButton.setVisibility(View.VISIBLE);
        buttonLayout.setVisibility(View.VISIBLE);
        okIv.setVisibility(View.GONE);
        cancelIv.setVisibility(View.GONE);
    }
}

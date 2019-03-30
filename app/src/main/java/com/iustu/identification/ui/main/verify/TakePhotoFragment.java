package com.iustu.identification.ui.main.verify;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.iustu.identification.R;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.main.MainActivity;
import com.iustu.identification.ui.widget.camera.CameraPreview;
import com.iustu.identification.ui.widget.dialog.SingleButtonDialog;
import com.iustu.identification.util.ExceptionUtil;
import com.iustu.identification.util.FileCallBack;
import com.iustu.identification.util.ImageUtils;
import com.iustu.identification.util.ToastUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;

/**
 * Created by Liu Yuchuan on 2017/11/23.
 */

public class TakePhotoFragment extends BaseFragment implements CameraPreview.StartPreViewCallback{
    static final String KEY_TYPE = "type";

    private CameraPreview cameraPreview;

    @BindView(R.id.verify_camera_layout)
    FrameLayout frameLayout;
    @BindView(R.id.take_photo_out_line_iv)
    ImageView outLine;
    @BindView(R.id.take_photo_iv)
    ImageView takePhotoButton;
    @BindView(R.id.take_photo_prompt_tv)
    TextView promptTv;
    @BindView(R.id.ok_iv)
    ImageView okIv;
    @BindView(R.id.cancel_iv)
    ImageView cancelIv;

    private byte[] imgBytes;
    private boolean isIdCard;
    private boolean isOnTakePhoto;

    public static TakePhotoFragment getInstance(boolean takeForIdCard){
        TakePhotoFragment takePhotoFragment = new TakePhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_TYPE, takeForIdCard);
        takePhotoFragment.setArguments(bundle);
        return takePhotoFragment;
    }

    @Override
    protected int postContentView() {
        Bundle bundle = getArguments();
        isIdCard = true;
        if(bundle == null)
            return R.layout.fragment_take_photo_id_card;
        isIdCard = bundle.getBoolean(KEY_TYPE);
        if(isIdCard){
            return R.layout.fragment_take_photo_id_card;
        }

        return R.layout.fragment_take_photo_person;
    }

    @OnClick(R.id.take_photo_iv)
    public void takePhoto(){
        cameraPreview.takePicture((data, camera) -> {
            imgBytes = data;
            onTakePhoto();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        frameLayout.post(()->{
            cameraPreview = new CameraPreview(mActivity, Camera.CameraInfo.CAMERA_FACING_BACK);
            cameraPreview.setStartPreViewCallback(this);
            frameLayout.addView(cameraPreview, 0);
        });
    }



    @Override
    public void onPause() {
        super.onPause();
        frameLayout.removeView(cameraPreview);
        cameraPreview = null;
        isOnTakePhoto = false;
    }

    public void onTakePhoto(){
        mActivity.runOnUiThread(()->{
            outLine.setVisibility(View.GONE);
            promptTv.setVisibility(View.GONE);
            takePhotoButton.setVisibility(View.GONE);
            okIv.setVisibility(View.VISIBLE);
            cancelIv.setVisibility(View.VISIBLE);
            isOnTakePhoto = true;
        });
    }

    @Override
    public void onStartPreview() {
        outLine.setVisibility(View.VISIBLE);
        promptTv.setVisibility(View.VISIBLE);
        takePhotoButton.setVisibility(View.VISIBLE);
        okIv.setVisibility(View.GONE);
        cancelIv.setVisibility(View.GONE);
        isOnTakePhoto = false;
    }

    @OnClick(R.id.cancel_iv)
    public void onCancel(){
        if(cameraPreview != null){
            cameraPreview.startPreView();
        }
    }

    @OnClick(R.id.ok_iv)
    public void onOk(){
        VerifyFragment verifyFragment = (VerifyFragment) getParentFragment();
        String folder = isIdCard?"人证对比身份证照片":"人证对比人脸照片";
        Observable<File> observable = ImageUtils.savePhoto(folder, imgBytes, 90, new FileCallBack() {
            @Override
            public void onStartSaveFile() {
                ((MainActivity)mActivity).showWaitDialog("正在保存照片,请勿退出", v->{
                    dispose();
                    onCancel();
                });
            }
        });

        if(observable == null){
            onError("创建文件失败", v->{
                dispose();
                onCancel();
            });
        }else {
            observable
                    .doOnSubscribe(verifyFragment::addDisposable)
                    .subscribe(file -> {
                                ((MainActivity)mActivity).dismissWaiDialog();
                                isOnTakePhoto = false;
                                verifyFragment.setImage(isIdCard, file);
                            },
                            ExceptionUtil::getThrowableMessage);
        }
    }

    public void onError(String content, View.OnClickListener action){
        new SingleButtonDialog.Builder()
                .content(content)
                .title("错误")
                .button("确定", action)
                .show(mActivity.getFragmentManager());
    }

    public boolean isOnTakePhoto() {
        return isOnTakePhoto;
    }
}

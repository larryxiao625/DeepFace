package com.iustu.identification.ui.main.verify;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.iustu.identification.R;
import com.iustu.identification.api.Api;
import com.iustu.identification.api.message.Message;
import com.iustu.identification.config.ParametersConfig;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.widget.dialog.NormalDialog;
import com.iustu.identification.ui.widget.dialog.WaitProgressDialog;
import com.iustu.identification.util.ExceptionUtil;
import com.iustu.identification.util.ToastUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Liu Yuchuan on 2017/11/5.
 */

public class VerifyFragment extends BaseFragment {

    @BindView(R.id.photo_from_id_card_layout)
    FrameLayout idCardLayout;
    @BindView(R.id.photo_from_person_layout)
    FrameLayout photoLayout;
    @BindView(R.id.photo_from_id_card_iv)
    ImageView idCardPhoto;
    @BindView(R.id.photo_from_person_iv)
    ImageView personPhoto;
    @BindView(R.id.verify_fragment_layout)
    FrameLayout fragmentLayout;
    @BindView(R.id.outcome_layout)
    LinearLayout outComeLayout;
    @BindView(R.id.verify_state_iv)
    ImageView stateIv;

    private TakePhotoFragment takePhotoFragment;

    private boolean isInTakePhoto;
    private boolean isTakeIdCard;
    private boolean isTakePerson;

    private File photoFile;
    private File idCardFile;

    private WaitProgressDialog waitProgressDialog;

    @Override
    protected int postContentView() {
        return R.layout.fragment_verify;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        isInTakePhoto = false;
    }

    @Override
    public void onHide() {
        super.onHide();
        if(takePhotoFragment != null) {
            getChildFragmentManager().beginTransaction()
                    .remove(takePhotoFragment)
                    .commit();
            fragmentLayout.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.photo_from_person_iv, R.id.photo_from_person_layout})
    public void takePhotoFromPerson(){
        takePhotoFragment = TakePhotoFragment.getInstance(false);
        getChildFragmentManager().beginTransaction()
                .add(R.id.verify_fragment_layout, takePhotoFragment)
                .commit();
        fragmentLayout.setVisibility(View.VISIBLE);
        isInTakePhoto = true;
    }

    @OnClick({R.id.photo_from_id_card_iv, R.id.photo_from_id_card_layout})
    public void takePhotoFromIdCard(){
        takePhotoFragment = TakePhotoFragment.getInstance(true);
        getChildFragmentManager().beginTransaction()
                .add(R.id.verify_fragment_layout, takePhotoFragment)
                .commit();
        fragmentLayout.setVisibility(View.VISIBLE);
        isInTakePhoto = true;
    }

    @Override
    public void onBackPressed() {
        if(isInTakePhoto && !takePhotoFragment.isOnTakePhoto()){
            getChildFragmentManager()
                    .beginTransaction()
                    .remove(takePhotoFragment)
                    .commit();
            fragmentLayout.setVisibility(View.GONE);
            isInTakePhoto = false;
        }else if(isInTakePhoto){
            takePhotoFragment.onCancel();
        }else {
            super.onBackPressed();
        }
    }

    public void setImage(boolean idCard, File img){
        onBackPressed();
        ImageView target;
        stateIv.setVisibility(View.INVISIBLE);
        if(idCard){
            isTakeIdCard = true;
            idCardLayout.setVisibility(View.GONE);
            target = idCardPhoto;
            idCardFile = img;
        }else {
            isTakePerson = true;
            photoLayout.setVisibility(View.GONE);
            target = personPhoto;
            photoFile = img;
        }
        Glide.with(mActivity)
                .setDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.photo_holder))
                .load(img)
                .into(target);
    }

    private void startVerify(){
        Api.imageCompare(idCardFile, photoFile)
                .doOnSubscribe(d->{
                    waitProgressDialog = new WaitProgressDialog.Builder()
                            .title("正在比对")
                            .button("取消", v->{})
                            .build();
                    waitProgressDialog.show(mActivity.getFragmentManager(), "wait");
                    addDisposable(d);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(compare2ResponseMessage -> {
                    stateIv.setVisibility(View.VISIBLE);
                    if(compare2ResponseMessage.getCode() == Message.CODE_SUCCESS
                            && compare2ResponseMessage.getBody().getScore() > ParametersConfig.getmInstance().getThresholdValuePaper()){
                        stateIv.setImageResource(R.drawable.chenggong);
                    }else {
                        stateIv.setImageResource(R.drawable.shibai);
                    }
                }, t->{
                    ExceptionUtil.getThrowableMessage(VerifyFragment.this.getClass().getSimpleName(), t);
                    onFail("无法连接服务器");
                }, ()->{
                    ToastUtil.show("比对完成!");
                    if(waitProgressDialog != null){
                        waitProgressDialog.dismiss();
                    }
                });

    }

    @OnClick(R.id.start_verify_tv)
    public void onClickStartVerify(){
        if(!isTakePerson){
            ToastUtil.show("请先上传人脸照片!");
            return;
        }

        if(!isTakeIdCard){
            ToastUtil.show("请先上传身份证照片!");
            return;
        }

        startVerify();
    }

    public void onFail(String extra){
        if(waitProgressDialog != null){
            waitProgressDialog.dismiss();
        }
        new NormalDialog.Builder()
                .title("错误")
                .content("比对失败," + extra)
                .negative("确定", v->{})
                .positive("重试", v-> startVerify())
                .show(mActivity.getFragmentManager());
    }
}

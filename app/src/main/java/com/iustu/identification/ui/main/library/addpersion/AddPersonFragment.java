package com.iustu.identification.ui.main.library.addpersion;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iustu.identification.R;
import com.iustu.identification.api.Api;
import com.iustu.identification.api.message.Message;
import com.iustu.identification.bean.Library;
import com.iustu.identification.bean.PersonInfo;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.main.MainActivity;
import com.iustu.identification.ui.main.library.LibraryFragment;
import com.iustu.identification.ui.main.library.addpersion.mvp.AddPersionPresenter;
import com.iustu.identification.ui.main.library.addpersion.mvp.AddPersionView;
import com.iustu.identification.ui.widget.dialog.SingleButtonDialog;
import com.iustu.identification.util.ExceptionUtil;
import com.iustu.identification.util.FileCallBack;
import com.iustu.identification.util.ImageUtils;
import com.iustu.identification.util.LibManager;
import com.iustu.identification.util.TextUtil;
import com.iustu.identification.util.ToastUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Liu Yuchuan on 2017/11/21.
 *
 */

public class AddPersonFragment extends BaseFragment implements AddPersionView {
    private static final String KEY_LIB_NAME = "lib name";
    private static final String KEY_FACE_SET_ID = "face set id";
    private static final String FORMAT_LIB_NAME = "人脸库名称——%s";
    private static final String KEY_PHOTO_PATH = "path";
    private static final String KEY_FACE_SET_INDEX = "index";


    private AddPersionPresenter presenter;

    private String libName;
    private String faceSetId;
    private int faceSetIndex;
    private String photoPath;

    @BindView(R.id.lib_name_tv)
    TextView libNameTv;
    @BindView(R.id.name_edit)
    TextView nameEdit;
    @BindView(R.id.id_card_edit)
    TextView idCardEdit;
    @BindView(R.id.sex_edit)
    TextView sexEdit;
    @BindView(R.id.location_edit)
    TextView locationEdit;
    @BindView(R.id.race_edit)
    TextView raceEdit;
    @BindView(R.id.phone_number_edit)
    TextView phoneNumberEdit;
    @BindView(R.id.remark_edit)
    TextView remarkEdit;
    @BindView(R.id.photo_iv)
    ImageView photoIv;

    @Override
    protected int postContentView() {
        return R.layout.fragment_add_person;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        view.post(this::onShow);
    }

    public void onArgumentsError(){
        new SingleButtonDialog.Builder()
                .title("错误")
                .content("参数错误")
                .button("确定", v -> ((LibraryFragment)getParentFragment()).switchFragment(LibraryFragment.ID_LIBRARIES_MANAGE))
                .show(mActivity.getFragmentManager());
    }

    @Override
    public void onShow() {
        Bundle bundle = getArguments();
        if(bundle != null){
            libName = bundle.getString(KEY_LIB_NAME, null);
            faceSetId = bundle.getString(KEY_FACE_SET_ID, null);
            libNameTv.setText(TextUtil.format(FORMAT_LIB_NAME, libName));
            faceSetIndex = bundle.getInt(KEY_FACE_SET_INDEX, -1);
            Log.e(AddPersonFragment.class.getSimpleName(), libName + " " + faceSetId + " " + faceSetIndex);
            if(faceSetIndex == -1){
                onArgumentsError();
            }
        }else {
            onArgumentsError();
        }
    }

    public void setArguments(String libName, String faceSetId, int faceSetIndex){
        Bundle bundle = getArguments();
        if(bundle == null){
            bundle = new Bundle();
        }
        bundle.putString(KEY_LIB_NAME, libName);
        bundle.putString(KEY_FACE_SET_ID, faceSetId);
        bundle.putInt(KEY_FACE_SET_INDEX, faceSetIndex);
        setArguments(bundle);
    }

    @OnClick(R.id.submit_tv)
    public void onSubmit(){
        SingleButtonDialog.Builder builder = new SingleButtonDialog.Builder()
                .button("确定", null)
                .title("提示");
        String name = nameEdit.getText().toString().trim();
        if(name.equals("")){
            builder.content("请填写姓名")
                    .show(mActivity.getFragmentManager());
            return;
        }
        String sex = sexEdit.getText().toString().trim();
        if(!sex.equals("男")&&!sex.equals("女")&&!sex.equals("")){
            builder.content("性别请输入(男或女或空)")
                    .show(mActivity.getFragmentManager());
            return;
        }
        String idCardNumber = idCardEdit.getText().toString().trim();
        String location = locationEdit.getText().toString().trim();
        String race = raceEdit.getText().toString().trim();
        String tel = phoneNumberEdit.getText().toString().trim();
        String remark = remarkEdit.getText().toString().trim();
        PersonInfo personInfo = new PersonInfo();
        personInfo.setName(name);
        personInfo.setCode(idCardNumber);
        personInfo.setGender(sex);
        personInfo.setAddress(location);
        personInfo.setRace(race);
        personInfo.setTel(tel);
        personInfo.setRemark(remark);
        personInfo.setFaceSetId(faceSetId);
        Api.addPeople(personInfo)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(this::addDisposable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stringMessage -> {
                    if(stringMessage.getCode() == Message.CODE_SUCCESS){
                        ToastUtil.show("添加人员成功");
                        Library library = LibManager.getLibraryList().get(faceSetIndex);
                        LibManager.getIdNameMap().put(library.getIdOnServer(), library.getName());
                        personInfo.setId(stringMessage.getBody());
                        ((LibraryFragment)getParentFragment()).switchFragment(LibraryFragment.ID_LIBRARIES_MANAGE);
                        if(photoPath != null) {
                            Api.addFace(faceSetId, stringMessage.getBody(), new File(photoPath))
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(stringMessage1 -> {
                                        if(stringMessage1.getCode() == Message.CODE_SUCCESS) {
                                            library.setCount(library.getCount() + 1);
                                            ToastUtil.show("添加照片成功");
                                        }else {
                                            ToastUtil.show("添加照片失败,错误码(" + stringMessage1.getCode() + ")");
                                        }
                                    }, t->{
                                        ToastUtil.show("添加照片失败");
                                        ExceptionUtil.getThrowableMessage(AddPersonFragment.class.getSimpleName(), t);
                                    });
                        }
                    }else {
                        ToastUtil.show("添加失败!");
                    }
                }, t->{
                    ExceptionUtil.getThrowableMessage(t);
                    ExceptionUtil.toastServerError();
                });
    }

    @OnClick(R.id.photo_iv)
    public void onChoosePhoto(){
        ImageUtils.startChoose(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK && requestCode == ImageUtils.REQUEST_GALLERY){
            String photoPath = ImageUtils.getRealPathFromUri(mActivity, data.getData());
            if(photoPath == null){
                ToastUtil.show("无法读取到图片路径");
                return;
            }

            int degree = ImageUtils.readPictureDegree(photoPath);
            if(degree != 0) {
                Observable<File> observable = ImageUtils.modifiedSavePhoto("添加照片", photoPath, ImageUtils.readPictureDegree(photoPath), new FileCallBack() {
                    @Override
                    public void onStartSaveFile() {
                        ((MainActivity)mActivity).showWaitDialog("正在处理,请勿退出", v->dispose());
                    }
                });
                if(observable == null){
                    ToastUtil.show("照片处理失败");
                    return;
                }

                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(f -> {
                            ((MainActivity)mActivity).dismissWaiDialog();
                            this.photoPath = f.getAbsolutePath();
                            Glide.with(mActivity)
                                    .load(Uri.fromFile(f))
                                    .into(photoIv);
                        }, t->{
                            ((MainActivity)mActivity).dismissWaiDialog();
                            ToastUtil.show("照片处理失败");
                            ExceptionUtil.getThrowableMessage(AddPersonFragment.class.getSimpleName(), t);
                        });
            }else {
                this.photoPath = photoPath;
                Glide.with(mActivity)
                        .load(Uri.fromFile(new File(photoPath)))
                        .into(photoIv);
            }
        }
    }

    @Override
    public void setPresenter(AddPersionPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onAddPersion() {
        presenter.onAddPersion();
    }
}

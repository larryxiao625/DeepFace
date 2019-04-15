package com.iustu.identification.ui.main.library.addperson;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iustu.identification.R;
import com.iustu.identification.entity.PersionInfo;
import com.iustu.identification.entity.PersonInfo;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.main.MainActivity;
import com.iustu.identification.ui.main.library.LibraryFragment;
import com.iustu.identification.ui.main.library.addperson.mvp.AddPersionPresenter;
import com.iustu.identification.ui.main.library.addperson.mvp.AddPersionView;
import com.iustu.identification.ui.widget.dialog.SingleButtonDialog;
import com.iustu.identification.ui.widget.dialog.WaitProgressDialog;
import com.iustu.identification.util.ExceptionUtil;
import com.iustu.identification.util.FileCallBack;
import com.iustu.identification.util.IdentityUtil;
import com.iustu.identification.util.ImageUtils;
import com.iustu.identification.util.NativePlace;
import com.iustu.identification.util.TextUtil;
import com.iustu.identification.util.ToastUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Liu Yuchuan on 2017/11/21.
 *
 */

public class AddPersonFragment extends BaseFragment implements AddPersionView {
    private static final String KEY_LIB_NAME = "libName";
    private static final String FORMAT_LIB_NAME = "人脸库名称——%s";
    private static final String KEY_LIB_ID= "libId";


    private AddPersionPresenter presenter;
    private WaitProgressDialog waitProgressDialog;

    private String libName;
    private int libId;
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
    @BindView(R.id.remark_edit)
    TextView remarkEdit;   //备注
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
            libNameTv.setText(TextUtil.format(FORMAT_LIB_NAME, libName));
        }else {
            onArgumentsError();
        }
    }

    public void setArguments(String libName){
        Bundle bundle = getArguments();
        if(bundle == null){
            bundle = new Bundle();
        }
        bundle.putString("libName", libName);
        setArguments(bundle);
    }

    @OnClick(R.id.submit_tv)
    public void onSubmit(){
        SingleButtonDialog.Builder builder = new SingleButtonDialog.Builder()
                .button("确定", null)
                .title("提示");
        String birthday = "未填写";
        String name = nameEdit.getText().toString().trim();
        if(name.equals("")){
            builder.content("请填写姓名")
                    .show(mActivity.getFragmentManager());
            return;
        }
        String idCardNumber = idCardEdit.getText().toString().trim();
        if (!idCardNumber.equals("") && idCardNumber != null) {
            if (!IdentityUtil.isValidatedIdentity(idCardNumber)) {
                ToastUtil.show("身份证不合法");
                return;
            }
            IdentityUtil.getInformation(idCardNumber);
            sexEdit.setText(IdentityUtil.gender);
            locationEdit.setText(NativePlace.getNativePlace(Integer.valueOf(idCardNumber.substring(0, 6))));
            birthday = IdentityUtil.birthday;
        }

        String sex = sexEdit.getText().toString().trim();
        if(!sex.equals("男")&&!sex.equals("女")&&!sex.equals("")){
            builder.content("性别请输入(男或女或空)")
                    .show(mActivity.getFragmentManager());
            return;
        }
        if(photoPath == null) {
            ToastUtil.show("请选择图片");
            return;
        }
        String location = locationEdit.getText().toString().trim();
        String remark = remarkEdit.getText().toString().trim();
        PersionInfo persionInfo = new PersionInfo();
        persionInfo.feature = null;
        persionInfo.gender = sex;
        persionInfo.name = name;
        persionInfo.home = location;
        persionInfo.identity = idCardNumber;
        persionInfo.other = remark;
        persionInfo.photoPath = photoPath;
        persionInfo.libName = libName;
        persionInfo.birthday = birthday;
        presenter.onAddPersion(persionInfo);
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
    public void onAddPersion(PersionInfo p) {
        presenter.onAddPersion(p);
    }

    @Override
    public void showWaitDialog(String content) {
        waitProgressDialog = new WaitProgressDialog.Builder()
                .title(content)
                .cancelable(false)
                .build();
        waitProgressDialog.show(mActivity.getFragmentManager(), "Loading");
    }

    @Override
    public void dissmissDialog() {
        waitProgressDialog.dismiss();
        waitProgressDialog = null;
    }

    @Override
    public void onAddError(String information) {
        ToastUtil.show("添加失败:" + information);
    }

    @Override
    public void onAddSuccess() {
        ToastUtil.show("添加成功");
    }

    // 将所有EditText清空
    public void clear() {
        nameEdit.setText("");
        idCardEdit.setText("");
        sexEdit.setText("");
        locationEdit.setText("");
        remarkEdit.setText("");
    }
}

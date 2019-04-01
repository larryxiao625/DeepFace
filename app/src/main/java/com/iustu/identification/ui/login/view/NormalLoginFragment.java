package com.iustu.identification.ui.login.view;

import android.util.Log;
import android.widget.EditText;

import com.iustu.identification.R;
import com.iustu.identification.api.Api;
import com.iustu.identification.api.message.Message;
import com.iustu.identification.bean.User;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.login.prenster.LoginPrenster;
import com.iustu.identification.util.ExceptionUtil;
import com.iustu.identification.util.LibManager;
import com.iustu.identification.util.ToastUtil;
import com.iustu.identification.util.UserCache;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Liu Yuchuan on 2017/11/15.
 */

public class NormalLoginFragment extends BaseFragment {
    LoginPrenster loginPrenster=new LoginPrenster();
    @BindView(R.id.login_username_et)
    EditText usernameEdit;
    @BindView(R.id.login_password_et)
    EditText passwordEdit;


    @Override
    protected int postContentView() {
        return R.layout.fragment_normal_login;
    }

    @OnClick(R.id.login_normal_switch)
    public void switchToFace(){
        ((LoginActivity)mActivity).switchFragment(1);
    }

    @OnClick(R.id.set_server_tv)
    public void setServer(){
        loginPrenster.setServer();
    }

//    @OnClick(R.id.btn_test)
//    public void test(){
//        CrashReport.testJavaCrash();
//    }

    @OnClick(R.id.login_normal_tv)
    public void login(){
        String username = usernameEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();
        if(username.equals("")){
            ToastUtil.show("请输入用户名");
            return;
        }
        LoginActivity loginActivity = (LoginActivity) mActivity;
        if(password.length() == 0){
            ToastUtil.show("请输入密码");
            return;
        }
        Api.login(username, password)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    mActivity.addDisposable(disposable);
                    loginActivity.startLogin();
                })
                .subscribe(stringMessage -> {
                    if (stringMessage.getCode() == Message.CODE_SUCCESS) {
                        User user = new User();
                        user.setUsername(username);
                        user.setId(stringMessage.getId());
                        user.setSession(stringMessage.getBody().getSession());
                        user.setVerify(true);
                        UserCache.setUser(user);
                        LibManager.loadData();
                    } else if(stringMessage.getCode() == Message.VERIFY_ERROR){
                        loginActivity.dismiss();
                        loginPrenster.getLoginFailDialog("用户名或密码不正确");
                    }else {
                        loginActivity.dismiss();
                        loginPrenster.getLoginFailDialog("未知错误(" + stringMessage.getCode() + ")");
                    }
                }, throwable -> {
                    dispose();
                    ExceptionUtil.getThrowableMessage(NormalLoginFragment.class.getSimpleName(), throwable);
                    loginActivity.dismiss();
                    loginPrenster.getLoginFailDialog("无法连接服务器");
                });
    }
}
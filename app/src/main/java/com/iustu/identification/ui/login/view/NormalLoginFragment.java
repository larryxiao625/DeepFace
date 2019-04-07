package com.iustu.identification.ui.login.view;

import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;

import com.iustu.identification.R;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.login.prenster.LoginPrenster;

import com.iustu.identification.util.SqliteHelper;
import com.iustu.identification.util.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Liu Yuchuan on 2017/11/15.
 */

public class NormalLoginFragment extends BaseFragment {
    LoginPrenster loginPrenster=LoginPrenster.getInstance();
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
        loginPrenster.normalLogin(username,password);
    }
}
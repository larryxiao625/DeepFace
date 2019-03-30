package com.iustu.identification.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.iustu.identification.R;
import com.iustu.identification.config.SystemConfig;
import com.iustu.identification.ui.base.BaseActivity;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.main.MainActivity;
import com.iustu.identification.ui.widget.dialog.EditDialog;
import com.iustu.identification.ui.widget.dialog.NormalDialog;
import com.iustu.identification.ui.widget.dialog.SingleButtonDialog;
import com.iustu.identification.ui.widget.dialog.WaitProgressDialog;
import com.iustu.identification.util.LibManager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;

/**
 * Created by Liu Yuchuan on 2017/11/4.
 */

public class LoginActivity extends BaseActivity implements LibManager.OnLibLoadListener{
    private List<BaseFragment> mFragmentList;

    private FragmentManager mFragmentManager;

    private static final String[] TAGS = {"normal", "login"};

    private int fragmentNow;

    private WaitProgressDialog waitProgressDialog;

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        LibManager.setOnLoadListener(this);
        mFragmentList = new ArrayList<>();
        mFragmentManager = getSupportFragmentManager();
        mFragmentList.add((BaseFragment) mFragmentManager.findFragmentByTag(TAGS[0]));
        Fragment fragment = mFragmentManager.findFragmentByTag(TAGS[1]);
        if(fragment == null){
            fragment = new FaceLoginFragment();
        }
        mFragmentList.add((BaseFragment) fragment);
    }

    @Override
    protected int postContentView() {
        return R.layout.activity_login;
    }

    public static void start(Activity activity){
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    void switchFragment(int toId){
        if(toId == fragmentNow){
            return;
        }
        Fragment ff = mFragmentList.get(fragmentNow);
        Fragment tf = mFragmentList.get(toId);
        if(!tf.isAdded()){
            mFragmentManager.beginTransaction()
                    .hide(ff)
                    .add(R.id.login_fragment_layout, tf)
                    .show(tf)
                    .commit();
        }else {
            mFragmentManager.beginTransaction()
                    .hide(ff)
                    .show(tf)
                    .commit();
        }

        if(toId == 0){
            mFragmentManager.beginTransaction().remove(ff).commit();
        }
        fragmentNow = toId;
    }

    public void startLogin(){
        waitProgressDialog = new WaitProgressDialog.Builder()
                .title("正在登陆")
                .button("取消", v->{
                    dispose();
                    LibManager.dispose();
                })
                .cancelable(false)
                .build();
        waitProgressDialog.show(getFragmentManager(), "wait");
    }

    @Override
    public void onStartLoad() {
        if(waitProgressDialog != null){
            waitProgressDialog.dismiss();
        }
        waitProgressDialog = new WaitProgressDialog.Builder()
                .title("正在初始化数据")
                .button("取消", v->{
                    dispose();
                    LibManager.dispose();
                })
                .cancelable(false)
                .build();
        waitProgressDialog.show(getFragmentManager(), "wait");
    }


    public void dismiss(){
        if(waitProgressDialog != null){
            waitProgressDialog.dismiss();
        }
    }


    @Override
    public void onSuccessLoad() {
        MainActivity.start(this);
    }

    @Override
    public void onFailLoad() {
        if(waitProgressDialog != null){
            waitProgressDialog.dismiss();
        }
        new NormalDialog.Builder()
                .title("错误")
                .content("初始化数据失败，请检查设置后重试")
                .negative("取消", null)
                .positive("重试", v->LibManager.loadData())
                .show(getFragmentManager());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LibManager.setOnLoadListener(null);
    }

    public void setServer(){
        new EditDialog.Builder()
                .content(SystemConfig.getInstance().getIpAddress())
                .title("更改域名地址")
                .hint("新域名")
                .positive("确定", (v, content, layout) -> {
                    if(!content.endsWith("/")){
                        content  += "/";
                    }
                    HttpUrl httpUrl = HttpUrl.parse(content);
                    if(httpUrl == null) {
                        layout.setError("地址不合法");
                        return false;
                    }
                    SystemConfig.getInstance().setIpAddress(content);
                    return true;
                })
                .negative("取消", null)
                .show(getFragmentManager());
    }

    public void showLoginFail(String cause){
        new SingleButtonDialog.Builder()
                .title("登陆失败")
                .content(cause)
                .button("确定", null)
                .show(getFragmentManager());
    }
}


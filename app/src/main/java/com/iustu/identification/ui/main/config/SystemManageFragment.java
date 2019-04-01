package com.iustu.identification.ui.main.config;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.iustu.identification.BuildConfig;
import com.iustu.identification.R;
import com.iustu.identification.bean.BatchCompareImg;
import com.iustu.identification.config.SystemConfig;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.login.view.LoginActivity;
import com.iustu.identification.ui.widget.SwitchButton;
import com.iustu.identification.ui.widget.dialog.EditDialog;
import com.iustu.identification.ui.widget.dialog.NormalDialog;
import com.iustu.identification.util.UserCache;
import com.tencent.bugly.beta.Beta;

import org.litepal.crud.DataSupport;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.HttpUrl;

/**
 * Created by Liu Yuchuan on 2017/11/5.
 */

public class SystemManageFragment extends BaseFragment implements SwitchButton.OnSwitchListener{
    private SystemConfig systemConfig;

    @BindView(R.id.wlan_4g_sb)
    SwitchButton switchButton;
    @BindView(R.id.tv_username)
    TextView usernameTv;
    @BindView(R.id.tv_version)
    TextView versionTv;

    @Override
    protected int postContentView() {
        return R.layout.fragment_system_manage;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        systemConfig = SystemConfig.getInstance();
        switchButton.setSwitch(systemConfig.isWlan4gSwitchOn());
        switchButton.setOnSwitchListener(this);
        usernameTv.setText(("用户：" + UserCache.getUser().getUsername()));
        versionTv.setText(("当前版本：" + BuildConfig.VERSION_NAME));
    }

    @Override
    public void onPause() {
        if(systemConfig != null){
            systemConfig.save();
        }
        super.onPause();
    }

    @Override
    public void onHide() {
        super.onHide();
        if(systemConfig != null){
            systemConfig.save();
        }
    }

    @OnClick(R.id.tv_username)
    public void logout(){
        new NormalDialog.Builder()
                .title("注销")
                .content("确定退出当前登录吗？")
                .positive("确定", v -> {
                    DataSupport.deleteAll(BatchCompareImg.class);
                    LoginActivity.start(mActivity);
                })
                .negative("取消", null)
                .show(mActivity.getFragmentManager());
    }

    @OnClick(R.id.ip_alter_tv)
    public void alterIp(){
        new EditDialog.Builder()
                .content(systemConfig.getIpAddress())
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
                    systemConfig.setIpAddress(content);
                    return true;
                })
                .negative("取消", null)
                .show(mActivity.getFragmentManager());
    }

    @OnClick(R.id.tv_version)
    public void onCheckUpdate(){
        Beta.checkUpgrade(true, false);
    }

    @Override
    public void onSwitch(View view, boolean on) {
        systemConfig.setWlan4gSwitchOn(on);
    }
}
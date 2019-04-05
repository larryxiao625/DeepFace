package com.iustu.identification.ui.login.prenster;

import android.text.TextUtils;

import com.iustu.identification.config.SystemConfig;
import com.iustu.identification.ui.login.view.IVew;
import com.iustu.identification.ui.widget.dialog.EditDialog;
import com.iustu.identification.ui.widget.dialog.NormalDialog;
import com.iustu.identification.ui.widget.dialog.SingleButtonDialog;
import com.iustu.identification.ui.widget.dialog.WaitProgressDialog;
import com.iustu.identification.util.DataCache;
import com.iustu.identification.util.LibManager;

import okhttp3.HttpUrl;

public class LoginPrenster implements IPrenster{
    IVew iVew;
    WaitProgressDialog waitProgressDialog;
    static LoginPrenster instance;
    public static LoginPrenster getInstance(){
        if(instance==null){
            LoginPrenster loginPrenster=new LoginPrenster();
            instance=loginPrenster;
        }
        return instance;
    }

    @Override
    public void attchView(IVew iVew) {
        this.iVew=iVew;
    }

    @Override
    public void setServer() {
    EditDialog editDialog=new EditDialog.Builder()
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
                .build();
    iVew.showServerDialog(editDialog);
    }

    @Override
    public void getLoginFailDialog(String cause) {
        SingleButtonDialog singleButtonDialog=new SingleButtonDialog.Builder()
                .title("登陆失败")
                .content(cause)
                .button("确定", null)
                .build();
        iVew.showLoginFail(singleButtonDialog);
    }

    @Override
    public void getDataLoadFail() {
        NormalDialog normalDialog=new NormalDialog.Builder()
                .title("错误")
                .content("初始化数据失败，请检查设置后重试")
                .negative("取消", null)
                .positive("重试", v->LibManager.loadData())
                .build();
        iVew.showDataFailLoad(normalDialog);
    }

    @Override
    public void getWaitProgressDialog(String title) {
        waitProgressDialog=new WaitProgressDialog.Builder()
                .title(title)
                .button("取消", v->{
                    iVew.disposeRxjava();
                    LibManager.dispose();
                })
                .cancelable(false)
                .build();
        iVew.showWaitDialog(waitProgressDialog);
    }

    @Override
    public void normalLogin(String username,String password) {
        if(TextUtils.equals(username,"admin")&&TextUtils.equals(password,"123456")) {
            LibManager.loadData();
            DataCache.initCache();
        }else{
            getLoginFailDialog("用户名或密码错误");
        }
    }
}

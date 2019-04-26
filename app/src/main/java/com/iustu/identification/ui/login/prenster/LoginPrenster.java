package com.iustu.identification.ui.login.prenster;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.iustu.identification.config.SystemConfig;
import com.iustu.identification.entity.Account;
import com.iustu.identification.ui.login.view.IVew;
import com.iustu.identification.ui.widget.dialog.EditDialog;
import com.iustu.identification.ui.widget.dialog.NormalDialog;
import com.iustu.identification.ui.widget.dialog.SingleButtonDialog;
import com.iustu.identification.ui.widget.dialog.WaitProgressDialog;
import com.iustu.identification.util.AlarmUtil;
import com.iustu.identification.util.DataCache;
import com.iustu.identification.util.LibManager;
import com.iustu.identification.util.MSP;
import com.iustu.identification.util.RxUtil;
import com.iustu.identification.util.SDKUtil;
import com.iustu.identification.util.SqliteHelper;
import com.iustu.identification.util.ToastUtil;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
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
        getWaitProgressDialog("正在登陆");
        final Disposable[] disposable = new Disposable[1];
        Observable observable = RxUtil.getLoginObservalbe(false, RxUtil.DB_ACCOUNT, new String[]{"name", "password"}, "name = ?", new String[]{username}, null, null, null, null);
        observable.subscribe(new Observer<Cursor>() {
            int count = 0;    // 为0代表第一次接受，获取Admin账户信息；为1代表登录的账户信息
            @Override
            public void onSubscribe(Disposable d) {
                disposable[0] = d;
            }

            @Override
            public void onNext(Cursor o) {
                // 首先获取admin账户
                if (count == 0) {
                    count ++;
                    while (o.moveToNext()) {
                        String name = o.getString(o.getColumnIndex("name"));
                        String pass = o.getString(o.getColumnIndex("password"));
                        Account admin = new Account(name, pass);
                        DataCache.setAdmin(admin);
                    }
                    return;
                }
                // 说明没有改账户
                if (o.getCount() == 0){
                    getLoginFailDialog("无此账户");
                    return;
                }
                while (o.moveToNext()) {
                    if (o.getString(o.getColumnIndex("password")).equals(password)) {
                        LibManager.loadData();
                        Account account = new Account(username, password);
                        DataCache.init();
                        DataCache.setAccount(account);
                        return;
                    }
                }
                getLoginFailDialog("用户名或密码错误");
                return;
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                ToastUtil.showLong(e.getMessage());
                disposable[0].dispose();
            }

            @Override
            public void onComplete() {
                disposable[0].dispose();
            }
        });
    }

}

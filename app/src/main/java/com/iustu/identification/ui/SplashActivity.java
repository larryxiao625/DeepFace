package com.iustu.identification.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.iustu.identification.R;
import com.iustu.identification.ui.base.BaseActivity;
import com.iustu.identification.ui.login.view.LoginActivity;
import com.iustu.identification.ui.widget.dialog.NormalDialog;
import com.iustu.identification.ui.widget.dialog.SingleButtonDialog;
import com.iustu.identification.util.CopyModelUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SplashActivity extends BaseActivity {

    private final String [] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.BLUETOOTH
    };

    private List<String> permissionList = new ArrayList<>();

    private static final int REQUEST_CODE = 1;

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        if(isTabletDevice()){
            new SingleButtonDialog.Builder()
                    .title("提示")
                    .content("抱歉，该款应用暂未支持平板")
                    .cancelable(false)
                    .button("确定", v-> finish())
                    .show(getFragmentManager());
            return;
        }
        Observable.timer(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> checkPermissions());
        CopyModelUtil.copyAssetsToSD(this);
    }

    private void checkPermissions(){
        for(String permission: permissions){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                permissionList.add(permission);
            }
        }
        if(!permissionList.isEmpty()){
            ActivityCompat.requestPermissions(this,
                    permissionList.toArray(new String[permissionList.size()]),
                    REQUEST_CODE);
        } else {
            startApp();
        }
    }

    private void startApp() {
        LoginActivity.start(this);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode != REQUEST_CODE)
            return;

        boolean isOk = false;
        if(grantResults.length > 0){
            isOk = true;
            for(int result: grantResults){
                if(result == PackageManager.PERMISSION_DENIED){
                    isOk = false;
                    break;
                }
            }
        }

        if(isOk){
            startApp();
        }else if(permissions.length > 0){
            new NormalDialog.Builder()
                    .title("错误")
                    .content("您拒绝了部分权限，请在设置中允许后重新打开应用")
                    .positive("确定", v -> {
                        finish();
                        Intent intent =  new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                        startActivity(intent);
                    })
                    .negative("取消", v -> finish())
                    .show(getFragmentManager());
        }
    }

    private boolean isTabletDevice() {
        return (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >=
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    @Override
    protected int postContentView() {
        return R.layout.activity_splash;
    }

    public static void start(Activity activity){
        Intent intent = new Intent(activity, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

}

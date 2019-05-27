package com.iustu.identification.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;

import com.iustu.identification.R;
import com.iustu.identification.ui.SplashActivity;
import com.iustu.identification.ui.base.BaseActivity;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.login.view.LoginActivity;
import com.iustu.identification.ui.main.camera.view.CameraFragment;
import com.iustu.identification.ui.main.config.ConfigFragment;
import com.iustu.identification.ui.main.history.view.HistoryFragment;
import com.iustu.identification.ui.main.library.LibraryFragment;
import com.iustu.identification.ui.widget.BottomBar;
import com.iustu.identification.ui.widget.dialog.NormalDialog;
import com.iustu.identification.ui.widget.dialog.WaitProgressDialog;
import com.iustu.identification.util.DataCache;
import com.iustu.identification.util.FileUtil;
import com.iustu.identification.util.LibManager;
import com.iustu.identification.util.SqliteUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;

public class MainActivity extends BaseActivity implements BottomBar.BottomBarSelectListener{

    @BindView(R.id.bottom_bar)
    public BottomBar bottomBar;

    private static final String [] TAGS = {"camera","history","library","config"};

    private List<BaseFragment> mFragmentList;
    private FragmentManager mFragmentManager;
    private int fragmentNow;

    private WaitProgressDialog waitDialog;

    private boolean isSessionOutOfDate;
    private boolean isActivityAlive;

    private Disposable keepAliveDisposable;

    public void onSessionOutOfDate(){
        new NormalDialog.Builder()
                .title("登录过期")
                .cancelable(false)
                .content("登录已过期，请重新登录")
                .positive("确定", v-> LoginActivity.start(this))
                .negative("取消", v->finish())
                .show(getSupportFragmentManager());
    }

    public WaitProgressDialog showWaitDialog(String message, View.OnClickListener action){
        waitDialog = new WaitProgressDialog.Builder()
                .button("取消", action)
                .cancelable(false)
                .title(message)
                .build();
        waitDialog.show(getSupportFragmentManager(), "wait");
        return waitDialog;
    }

    @Override
    protected void onStart() {
        try {
            DataCache.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onStart();
    }

    public void dismissWaiDialog(){
        if(waitDialog != null){
            waitDialog.dismiss();
        }
    }

    @Override
    protected int postContentView() {
        return R.layout.activity_main;
    }



    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        isActivityAlive = true;
        if(!LibManager.isLoadData()){
            dispose();
            SplashActivity.start(this);
            return;
        }
        bottomBar.post(() -> {
            mFragmentManager = getSupportFragmentManager();
            mFragmentList = new ArrayList<>();
            mFragmentList.add(new HistoryFragment());
            mFragmentList.add(new LibraryFragment());
            mFragmentList.add(new ConfigFragment());
            Fragment fragment;
            // 保证在内存重启的时候，使用已有的Fragment(通过构造器新建的会重新执行生命周期，从而导致资源占用)
            for(int i = 0; i < mFragmentList.size(); i++){
                fragment = mFragmentManager.findFragmentByTag(TAGS[i + 1]);
                if(fragment != null){
                    mFragmentList.set(i, (BaseFragment) fragment);
                }
            }
            fragmentNow = 0;
            bottomBar.setBottomBarSelectListener(this);
            BaseFragment baseFragment;
            mFragmentManager.beginTransaction()
                    .replace(R.id.fragment_fm, baseFragment =  new CameraFragment())
                    .commit();
            mFragmentList.add(0, baseFragment);
        });
    }

    public static void start(Activity activity){
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    public void switchFragment(int to){
        BaseFragment ff = mFragmentList.get(fragmentNow);
        BaseFragment tf = mFragmentList.get(to);
        FragmentTransaction transaction = mFragmentManager.beginTransaction()
                .hide(ff);
        if(!tf.isAdded()){
            transaction.add(R.id.fragment_fm, tf, TAGS[to]);
        }
        if(fragmentNow == 0 || fragmentNow == 3 || fragmentNow == 2) {
            transaction.remove(ff);
        }
        transaction.show(tf).commit();
        fragmentNow = to;
    }

    public BaseFragment getFragment(int id){
        return mFragmentList.get(id);
    }

    @Override
    public void onBackPressed() {
        mFragmentList.get(fragmentNow).onBackPressed();
    }

    @Override
    public void onSelect(int id) {
        switchFragment(id);
    }

    @Override
    protected void onStop() {
        DataCache.saveCache();
        FileUtil.deleteTemp();
        SqliteUtil.updataLibrariedInUsed();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        isActivityAlive = false;
        LibManager.dispose();
        super.onDestroy();
//        stopService(new Intent(MainActivity.this, CapturePicService.class));
    }

}

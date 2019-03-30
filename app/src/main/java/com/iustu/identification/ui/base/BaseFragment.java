package com.iustu.identification.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iustu.identification.ui.main.MainActivity;
import com.iustu.identification.util.LibManager;
import com.iustu.identification.util.ToastUtil;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Liu Yuchuan on 2017/11/4.
 */

public abstract class BaseFragment extends Fragment {
    private Unbinder mUnbinder;
    private CompositeDisposable mCompositeDisposable;

    private final String TAG = getClass().getSimpleName();

    private static final String KEY_INIT_DATA = "init data";

    protected BaseActivity mActivity;

    private static boolean isExit;

    protected boolean isInitData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInitData = savedInstanceState != null && savedInstanceState.getBoolean(KEY_INIT_DATA);
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof BaseActivity){
            mActivity = (BaseActivity) context;
        }else {
            throw new ClassCastException("Please use BaseActivity");
        }
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(postContentView(), container, false);
        mUnbinder = ButterKnife.bind(this, view);
        isInitData = false;
        initView(savedInstanceState, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        if(mUnbinder != null){
            mUnbinder.unbind();
        }
        dispose();
        super.onDestroyView();
    }

    public void onBackPressed(){
        if(isExit) {
            mActivity.finish();
        }else {
            ToastUtil.show("再按一次返回键退出");
            isExit = true;
            Observable.timer(3, TimeUnit.SECONDS)
                    .subscribe(l->isExit = false);
        }
    }


    protected void initView(@Nullable Bundle savedInstanceState, View view) {}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_INIT_DATA, isInitData);
    }

    public void addDisposable(Disposable disposable){
        if(mCompositeDisposable == null){
            mCompositeDisposable = new CompositeDisposable();
        }

        if(disposable != null){
            mCompositeDisposable.add(disposable);
        }
    }

    public void onShow(){
        Log.e(getClass().getSimpleName(), "onShow");
    }

    public void onHide(){
        if(mCompositeDisposable != null){
            mCompositeDisposable.clear();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden){
            onShow();
        }else {
            onHide();
        }
    }

    abstract protected @LayoutRes int postContentView();

    public boolean isInitData() {
        return isInitData;
    }

    public void setInitData(boolean initData) {
        isInitData = initData;
    }

    protected void dispose(){
        if(mCompositeDisposable != null){
            mCompositeDisposable.clear();
        }
    }
}

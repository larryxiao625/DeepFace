package com.iustu.identification.ui.base;

import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Liu Yuchuan on 2017/11/4.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private Unbinder mUnbinder;
    private CompositeDisposable mCompositeDisposable;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(postContentView());
        mUnbinder = ButterKnife.bind(this);
        initView(savedInstanceState);
    }

    protected void initView(@Nullable Bundle savedInstanceState){}

    public void addDisposable(Disposable disposable){
        if(mCompositeDisposable == null){
            mCompositeDisposable = new CompositeDisposable();
        }

        if(disposable != null){
            mCompositeDisposable.add(disposable);
        }
    }

    protected void dispose(){
        if(mCompositeDisposable != null){
            mCompositeDisposable.clear();
        }
    }

    abstract protected @LayoutRes int postContentView();

    @Override
    protected void onDestroy() {
        if(mUnbinder != null){
            mUnbinder.unbind();
        }
        dispose();
        super.onDestroy();
    }
}

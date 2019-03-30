package com.iustu.identification.ui.main.history;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;

import com.iustu.identification.R;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.main.history.compare.CompareHistoryFragment;
import com.iustu.identification.ui.widget.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Liu Yuchuan on 2017/11/5.
 */

public class HistoryFragment extends BaseFragment implements TitleBar.TitleBarListener{
    @BindView(R.id.histoy_fragment_layout)
    FrameLayout layout;
    @BindView(R.id.history_title_bar)
    TitleBar titleBar;

    private List<BaseFragment> mFragmentList;

    private int fragmentNow;

    public static final int ID_FACE = 0;
    public static final int ID_COMPARE = 1;
    private static final String [] TAGS = {"face_history", "compare_history"};

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        titleBar.setTitleBarListener(this);
        FragmentManager fm = getChildFragmentManager();
        mFragmentList = new ArrayList<>();
        mFragmentList.add((BaseFragment) fm.findFragmentByTag(TAGS[0]));
        Fragment fragment = fm.findFragmentByTag(TAGS[1]);
        if(fragment != null){
            fm.beginTransaction().remove(fragment).commit();
            mFragmentList.add((BaseFragment) fragment);
        }else {
            mFragmentList.add(new CompareHistoryFragment());
        }
    }

    @SuppressWarnings("unchecked")
    public<T extends BaseFragment> T getFragment(int id){
        return (T)mFragmentList.get(id);
    }

    public void switchFragment(int id){
        if(id == fragmentNow){
            return;
        }
        BaseFragment ff = mFragmentList.get(fragmentNow);
        BaseFragment tf = mFragmentList.get(id);
        if(!tf.isAdded()){
            getChildFragmentManager().beginTransaction()
                    .hide(ff)
                    .add(R.id.histoy_fragment_layout, tf, TAGS[id])
                    .show(tf)
                    .commit();
        }else {
            getChildFragmentManager().beginTransaction()
                    .hide(ff)
                    .show(tf)
                    .commit();
        }
        if(id == ID_FACE){
            titleBar.setBackEnable(false);
            titleBar.setTitle("历史记录");
        }else {
            titleBar.setBackEnable(true);
            titleBar.setTitle("比对结果记录");
        }
        fragmentNow = id;
    }

    @Override
    protected int postContentView() {
        return R.layout.fragment_history;
    }

    @Override
    public void onTitleButtonClick(int id) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if(fragmentNow == ID_FACE){
            super.onBackPressed();
        }else {
            switchFragment(ID_FACE);
        }
    }
}

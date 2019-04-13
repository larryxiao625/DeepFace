package com.iustu.identification.ui.main.config;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.iustu.identification.R;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.util.DataCache;

import butterknife.BindView;

/**
 * Created by Liu Yuchuan on 2017/11/5.
 */

public class ConfigFragment extends BaseFragment {

    @BindView(R.id.tab_config)
    TabLayout tabLayout;

    @BindView(R.id.config_pager)
    ViewPager viewPager;

    private ConfigPagerAdapter mAdapter;


    @Override
    protected int postContentView() {
        return R.layout.fragment_config;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        mAdapter = new ConfigPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public BaseFragment getFragment(int id){
        if(mAdapter == null){
            return null;
        }
        return (BaseFragment) mAdapter.getItem(id);
    }

    @Override
    public void onHide() {
        super.onHide();
        if(mAdapter != null) {
            final int count = mAdapter.getCount();
            for (int i = 0; i < count; i++) {
                BaseFragment baseFragment = (BaseFragment) mAdapter.getItem(i);
                baseFragment.onHide();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        DataCache.saveCache();
    }
}

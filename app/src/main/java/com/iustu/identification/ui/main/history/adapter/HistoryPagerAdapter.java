package com.iustu.identification.ui.main.history.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.main.history.view.compare.CompareHistoryFragment;
import com.iustu.identification.ui.main.history.view.face.FaceHistoryFragment;

import java.util.List;

/**
 * Created by Liu Yuchuan on 2017/11/5.
 */

public class HistoryPagerAdapter extends FragmentPagerAdapter {
    private String [] title = {"人脸采集记录", "人脸比对记录"};

    private List<BaseFragment> fragments;

    public HistoryPagerAdapter(FragmentManager fm,List<BaseFragment> baseFragments) {
        super(fm);
        this.fragments=baseFragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }


}

package com.iustu.identification.ui.main.history;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.iustu.identification.ui.main.config.LibraryManageFragment;
import com.iustu.identification.ui.main.config.ParameterConfigFragment;
import com.iustu.identification.ui.main.config.SystemManageFragment;
import com.iustu.identification.ui.main.history.compare.CompareHistoryFragment;
import com.iustu.identification.ui.main.history.face.FaceHistoryFragment;

/**
 * Created by Liu Yuchuan on 2017/11/5.
 */

public class HistoryPagerAdapter extends FragmentPagerAdapter {
    private String [] title = {"人脸采集记录", "人脸比对记录"};

    private Fragment [] fragments = {new FaceHistoryFragment(), new CompareHistoryFragment()};

    public HistoryPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
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

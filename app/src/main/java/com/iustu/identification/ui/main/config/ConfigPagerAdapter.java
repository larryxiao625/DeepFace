package com.iustu.identification.ui.main.config;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Liu Yuchuan on 2017/11/5.
 */

public class ConfigPagerAdapter extends FragmentPagerAdapter {
    private String [] title = {"参数配置", "比对库选择", "系统管理"};

    private Fragment [] fragments = {new ParameterConfigFragment(), new LibraryManageFragment(), new SystemManageFragment()};

    public ConfigPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}

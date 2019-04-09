package com.iustu.identification.ui.main.config;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.iustu.identification.ui.main.config.libmanager.LibraryManageFragment;
import com.iustu.identification.ui.main.config.libmanager.mvp.LibManagerPesenter;
import com.iustu.identification.ui.main.library.librariesmanage.LibrariesManageFragment;

/**
 * Created by Liu Yuchuan on 2017/11/5.
 */

public class ConfigPagerAdapter extends FragmentPagerAdapter {
    private String [] title = {"参数配置", "比对库选择", "系统管理"};
    private LibraryManageFragment librariesManageFragment = new LibraryManageFragment();
    private LibManagerPesenter presenter = new LibManagerPesenter();
    {
        presenter.setView(librariesManageFragment);
        librariesManageFragment.setPresenter(presenter);
    }

    private Fragment [] fragments = {new ParameterConfigFragment(), librariesManageFragment, new SystemManageFragment()};

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

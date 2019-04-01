package com.iustu.identification.ui.main.library;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.iustu.identification.R;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.main.library.librariesmanage.LibrariesManageFragment;
import com.iustu.identification.ui.main.library.peoplemagnage.PeopleManageFragment;
import com.iustu.identification.ui.widget.TitleBar;
import com.iustu.identification.ui.widget.dialog.NormalDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Liu Yuchuan on 2017/11/5.
 * 该Fragment的作用就是管理其所有的ChildFragment
 */

public class LibraryFragment extends BaseFragment implements TitleBar.TitleBarListener{
    public static final int ID_LIBRARIES_MANAGE = 0;
    public static final int ID_ADD_PERSON = 1;
    public static final int ID_PEOPLE_MANAGE = 2;

    @BindView(R.id.title_bar_lib)
    TitleBar titleBar;

    private List<BaseFragment> mFragmentList;
    private int fragmentNow;
    private static final String [] TAGS = {"librariesManage", "peopleManage", "addPerson"};

    @Override
    protected int postContentView() {
        return R.layout.fragment_library;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        titleBar.setTitleBarListener(this);
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new LibrariesManageFragment());
        mFragmentList.add(new AddPersonFragment());
        mFragmentList.add(new PeopleManageFragment());
        FragmentManager fragmentManager = getChildFragmentManager();
        for(int i = 0; i < 3; i++){
            BaseFragment fragment = (BaseFragment) fragmentManager.findFragmentByTag(TAGS[i]);
            if(fragment != null){
                mFragmentList.set(i, fragment);
            }
        }
        switchFragment(fragmentNow);
    }

    public BaseFragment getFragment(int id){
        return mFragmentList.get(id);
    }

    public void switchFragment(int toId){
        Fragment ff = mFragmentList.get(fragmentNow);
        Fragment to = mFragmentList.get(toId);
        if(!to.isAdded()){
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_library_layout, to, TAGS[toId])
                    .hide(ff)
                    .show(to)
                    .commit();
        }else {
            getChildFragmentManager()
                    .beginTransaction()
                    .hide(ff)
                    .show(to)
                    .commit();
        }
        if(toId == ID_LIBRARIES_MANAGE){
            titleBar.setTitle("人脸库管理");
            titleBar.setBackEnable(false);
        }else if(toId == ID_ADD_PERSON){
            titleBar.setTitle("人脸信息登记");
            titleBar.setBackEnable(true);
        }else if(toId == ID_PEOPLE_MANAGE){
            titleBar.setTitle("人脸信息管理");
            titleBar.setBackEnable(true);
        }
        if(fragmentNow != ID_LIBRARIES_MANAGE){
            getChildFragmentManager()
                    .beginTransaction()
                    .remove(ff)
                    .commit();
        }
        fragmentNow = toId;
    }

    // 自定义TitleBar的点击事件
    @Override
    public void onTitleButtonClick(int id) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if(fragmentNow == ID_ADD_PERSON){
            new NormalDialog.Builder()
                    .title("提示")
                    .content("放弃添加？")
                    .positive("确定", v->switchFragment(ID_LIBRARIES_MANAGE))
                    .negative("取消", null)
                    .show(mActivity.getFragmentManager());
        }else if(fragmentNow == ID_PEOPLE_MANAGE){
            switchFragment(ID_LIBRARIES_MANAGE);
        }else {
            super.onBackPressed();
        }
    }

    public void notifyLibChange(){
        mFragmentList.get(ID_LIBRARIES_MANAGE).setInitData(false);
    }
}

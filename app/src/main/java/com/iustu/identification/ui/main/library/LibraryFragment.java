package com.iustu.identification.ui.main.library;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.iustu.identification.R;
import com.iustu.identification.entity.Account;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.main.library.addperson.AddPersonFragment;
import com.iustu.identification.ui.main.library.addperson.mvp.AddPersionModel;
import com.iustu.identification.ui.main.library.addperson.mvp.AddPersionPresenter;
import com.iustu.identification.ui.main.library.librariesmanage.LibrariesManageFragment;
import com.iustu.identification.ui.main.library.librariesmanage.mvp.LibModel;
import com.iustu.identification.ui.main.library.librariesmanage.mvp.LibPresenter;
import com.iustu.identification.ui.main.library.peoplemagnage.PeopleManageFragment;
import com.iustu.identification.ui.main.library.peoplemagnage.mvp.PersionModel;
import com.iustu.identification.ui.main.library.peoplemagnage.mvp.PersionPresenter;
import com.iustu.identification.ui.widget.TitleBar;
import com.iustu.identification.ui.widget.dialog.EditDialog;
import com.iustu.identification.ui.widget.dialog.NormalDialog;
import com.iustu.identification.util.AlarmUtil;
import com.iustu.identification.util.DataCache;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

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
    @BindView(R.id.tv_sure)
    TextView confirm;

    private boolean needConfirm = true;       // 用来标志是否需要输入管理员密码

    private LibPresenter libPresenter;
    private PersionPresenter persionPresenter;
    private AddPersionPresenter addPersionPresenter;

    LibrariesManageFragment libFragment;
    PeopleManageFragment peopleManageFragment;
    AddPersonFragment addPersonFragment;


    private List<BaseFragment> mFragmentList;
    private int fragmentNow;
    private Account currentAccount;      // 表示当前的账户
    private Account admin;

    public LibraryFragment() {
        mFragmentList = new ArrayList<>();

        libPresenter = new LibPresenter(new LibModel());
        libFragment = new LibrariesManageFragment();
        libPresenter.setView(libFragment);
        libFragment.setPresenter(libPresenter);

        persionPresenter = new PersionPresenter(new PersionModel());
        peopleManageFragment = new PeopleManageFragment();
        persionPresenter.setView(peopleManageFragment);
        peopleManageFragment.setPresenter(persionPresenter);

        addPersionPresenter = new AddPersionPresenter(new AddPersionModel());
        addPersonFragment = new AddPersonFragment();
        addPersionPresenter.setView(addPersonFragment);
        addPersonFragment.setPresenter(addPersionPresenter);

        mFragmentList.add(libFragment);
        mFragmentList.add(addPersonFragment);
        mFragmentList.add(peopleManageFragment);
    }
    private static final String [] TAGS = {"librariesManage", "addPerson", "peopleManage"};

    @Override
    protected int postContentView() {
        return R.layout.fragment_library;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        currentAccount = DataCache.getAccount();
        admin = DataCache.getAdmin();
        titleBar.setTitleBarListener(this);
        FragmentManager fragmentManager = getChildFragmentManager();
        for(int i = 0; i < 3; i++){
            BaseFragment fragment = (BaseFragment) fragmentManager.findFragmentByTag(TAGS[i]);
            if(fragment != null){
                mFragmentList.set(i, fragment);
            }
        }
        if (currentAccount != null || currentAccount.name.equals("admin")) {
            confirm.setVisibility(View.GONE);
            needConfirm = false;
            switchFragment(fragmentNow);
            return;
        }
        confirm.setVisibility(View.INVISIBLE);
        new EditDialog.Builder()
                .title("获取管理员权限")
                .hint("请输入管理员账户密码")
                .positive("确定", (v, content, layout) -> {
                    if (content.equals(admin.password)) {
                        needConfirm = false;
                        confirm.setVisibility(View.GONE);
                        switchFragment(fragmentNow);
                    } else
                        confirm.setVisibility(View.VISIBLE);
                    return true;
                })
                .negative("取消", (v, content, layout) -> {
                    confirm.setVisibility(View.VISIBLE);
                    return true;
                })
                .show(mActivity.getFragmentManager());

    }

    @OnClick(R.id.tv_sure)
    public void showDialog () {
        if (!needConfirm)
            return;
        new EditDialog.Builder()
                .title("获取管理员权限")
                .hint("请输入管理员账户密码")
                .positive("确定", (v, content, layout) -> {
                    if (content.equals("123456")) {
                        needConfirm = false;
                        confirm.setVisibility(View.GONE);
                        switchFragment(fragmentNow);
                    }else
                        confirm.setVisibility(View.VISIBLE);
                    return true;
                })
                .negative("取消", (v, content, layout) -> {
                    confirm.setVisibility(View.VISIBLE);
                    return true;
                })
                .show(mActivity.getFragmentManager());
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
            titleBar.setSearchEnable(false);
        }else if(toId == ID_ADD_PERSON){
            titleBar.setTitle("人脸信息登记");
            titleBar.setBackEnable(true);
            titleBar.setSearchEnable(false);
        }else if(toId == ID_PEOPLE_MANAGE){
            titleBar.setTitle("人脸信息管理");
            titleBar.setBackEnable(true);
            titleBar.setSearchEnable(true);
        }
        if(fragmentNow != ID_LIBRARIES_MANAGE){
            getChildFragmentManager()
                    .beginTransaction()
                    .remove(ff)
                    .commit();
        }
        fragmentNow = toId;
    }

    @Override
    public void onShow() {
        super.onShow();
        if (!needConfirm)
            return;
        new EditDialog.Builder()
                .title("获取管理员权限")
                .hint("请输入管理员账户密码")
                .positive("确定", (v, content, layout) -> {
                    if (content.equals("123456")) {
                        needConfirm = false;
                        confirm.setVisibility(View.GONE);
                        switchFragment(fragmentNow);
                    } else
                        confirm.setVisibility(View.VISIBLE);
                    return true;
                })
                .negative("取消", (v, content, layout) -> {
                    confirm.setVisibility(View.VISIBLE);
                    return true;
                })
                .show(mActivity.getFragmentManager());

    }

    // 自定义TitleBar的点击事件
    @Override
    public void onTitleButtonClick(int id) {
        if (id == TitleBar.ID_BACK)
            onBackPressed();
        else if(id == TitleBar.ID_SEARCH)
            peopleManageFragment.searchPerson();
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

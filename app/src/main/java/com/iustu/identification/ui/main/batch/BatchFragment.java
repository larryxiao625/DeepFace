package com.iustu.identification.ui.main.batch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.iustu.identification.R;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.main.batch.folder.FolderChooseFragment;
import com.iustu.identification.ui.main.batch.img.ImgChooseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liu Yuchuan on 2017/11/17.
 */

public class BatchFragment extends BaseFragment{

    private List<BaseFragment> mFragmentList;

    private int fragmentNow;

    public static final int ID_BATCH_COMPARE = 0;
    public static final int ID_FOLDER_CHOOSE = 1;
    public static final int ID_IMG_CHOOSE = 2;

    private static final String[] TAGS = {"batchCompare", "folderChoose", "imgChoose"};


    @Override
    protected int postContentView() {
        return R.layout.fragment_batch;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new BatchCompareFragment());
        mFragmentList.add(new FolderChooseFragment());
        mFragmentList.add(new ImgChooseFragment());
        FragmentManager fragmentManager = getChildFragmentManager();
        for(int i = 0; i < 3; i++){
            Fragment fragment = fragmentManager.findFragmentByTag(TAGS[i]);
            if(fragment != null){
                mFragmentList.set(i, (BaseFragment) fragment);
            }else {
                fragmentManager.beginTransaction()
                        .add(R.id.batch_fragment_layout, mFragmentList.get(i), TAGS[i])
                        .hide(mFragmentList.get(i))
                        .commit();
            }
        }
        fragmentNow = 0;
        fragmentManager.beginTransaction().show(mFragmentList.get(fragmentNow)).commit();
    }

    public void switchFragment(int toId){
        Fragment ff = mFragmentList.get(fragmentNow);
        Fragment to = mFragmentList.get(toId);
        if(!to.isAdded()){
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.batch_fragment_layout, to, TAGS[toId])
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
        fragmentNow = toId;
    }

    public BaseFragment getFragment(int id){
        return mFragmentList.get(id);
    }

    @Override
    public void onBackPressed() {
        mFragmentList.get(fragmentNow).onBackPressed();
    }
}

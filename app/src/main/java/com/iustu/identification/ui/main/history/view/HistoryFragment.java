package com.iustu.identification.ui.main.history.view;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.bigkoo.pickerview.TimePickerView;
import com.iustu.identification.R;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.main.history.adapter.HistoryPagerAdapter;
import com.iustu.identification.ui.main.history.prenster.HistoryPrenster;
import com.iustu.identification.ui.widget.TitleBar;
import com.iustu.identification.ui.widget.dialog.NormalDialog;
import com.iustu.identification.ui.widget.dialog.SingleButtonDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Liu Yuchuan on 2017/11/5.
 */

public class HistoryFragment extends BaseFragment implements TitleBar.TitleBarListener{
    @BindView(R.id.history_viewpager)
    ViewPager viewPager;
    @BindView(R.id.history_tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.history_title_bar)
    TitleBar titleBar;

    private List<BaseFragment> mFragmentList;

    private int fragmentNow;

    private HistoryPagerAdapter historyPagerAdapter;

    public static final int ID_FACE = 0;
    public static final int ID_COMPARE = 1;
    private static final String [] TAGS = {"face_history", "compare_history"};

    HistoryPrenster historyPrenster;

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        titleBar.setTitleBarListener(this);
        historyPrenster=HistoryPrenster.getInstance(getActivity());
        FragmentManager fm = getChildFragmentManager();
        mFragmentList = new ArrayList<>();
        CompareHistoryFragment compareHistoryFragment=new CompareHistoryFragment();
        FaceHistoryFragment faceHistoryFragment=new FaceHistoryFragment();
        mFragmentList.add(faceHistoryFragment);
        mFragmentList.add(compareHistoryFragment);
        historyPagerAdapter=new HistoryPagerAdapter(fm,mFragmentList);
        viewPager.setAdapter(historyPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
        tabLayout.setBackgroundColor(Color.parseColor("#222986"));
        historyPrenster.attchSwitchFragment(switchFragmentLister);
    }

    @SuppressWarnings("unchecked")
    public<T extends BaseFragment> T getFragment(int id){
        return (T)mFragmentList.get(id);
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
            switchFragmentLister.switchFragment(0);
        }
    }

    public interface SwitchFragmentLister{
        void switchFragment(int id);
    }

    SwitchFragmentLister switchFragmentLister=new SwitchFragmentLister() {
        @Override
        public void switchFragment(int id) {
            viewPager.setCurrentItem(id);
        }
    };

    public void switchFragment(int id){
        viewPager.setCurrentItem(id);
    }
}

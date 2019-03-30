package com.iustu.identification.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iustu.identification.R;

/**
 * Created by Liu Yuchuan on 2017/11/4.
 */

public class TabBar extends LinearLayout{

    private static final String TAG = "tab bar";

    private int selectFontColor;
    private int unSelectFontColor;

    private int selectPosition;

    private TextView items[];

    private String [] tabs = {"全部", "新增", "历史记录"};

    private OnTabBarSelectListener onTabBarSelectListener;

    public TabBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        selectFontColor = context.getResources().getColor(R.color.gold);
        unSelectFontColor = context.getResources().getColor(R.color.white);
        int length = tabs.length;
        items = new TextView[length];
        selectPosition = 0;
        for(int i = 0; i < length; i++){
            items[i] = new TextView(context);
            items[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.y36));
            items[i].setText(tabs[i]);
            items[i].setTextColor(unSelectFontColor);
            items[i].setGravity(Gravity.CENTER);
            addView(items[i]);
            LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            items[i].setLayoutParams(params);
            final int index = i;
            items[i].setOnClickListener(v -> {
                if(index == selectPosition){
                    return;
                }
                items[selectPosition].setTextColor(unSelectFontColor);
                items[index].setTextColor(selectFontColor);
                selectPosition = index;
                Log.d(TAG, "select " + index);
                if(onTabBarSelectListener != null){
                    onTabBarSelectListener.onTabSelect(index);
                }
            });
        }
        items[0].setTextColor(selectFontColor);
    }

    public void setTabs(String [] tabs){
        this.tabs = tabs;
    }

    public void setSelected(int index){
        items[selectPosition].setTextColor(unSelectFontColor);
        items[index].setTextColor(selectFontColor);
        selectPosition = index;
    }

    public void setOnTabBarSelectListener(OnTabBarSelectListener onTabBarSelectListener) {
        this.onTabBarSelectListener = onTabBarSelectListener;
    }

    public interface OnTabBarSelectListener {
        void onTabSelect(int id);
    }
}
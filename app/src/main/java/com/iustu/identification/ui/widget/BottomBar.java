package com.iustu.identification.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iustu.identification.R;
import com.iustu.identification.util.IconFontUtil;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by Liu Yuchuan on 2017/11/4.
 */

public class BottomBar extends LinearLayout implements View.OnClickListener{

    private int selectBackColor;
    private int unSelectBackColor;
    private int selectFontColor;
    private int unSelectFontColor;
    private int selectPosition;

    private TextView items[];

    private final String [] icons = {"\ue600", "\ue60c", "\ue69a", "\ue60d"};

    private BottomBarSelectListener bottomBarSelectListener;

    public BottomBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        selectBackColor = context.getResources().getColor(R.color.bottom_bar_selected);
        unSelectBackColor = context.getResources().getColor(android.R.color.transparent);
        selectFontColor = context.getResources().getColor(R.color.white);
        unSelectFontColor = context.getResources().getColor(R.color.bottom_bar_item_unselected);
        int length = icons.length;
        items = new TextView[length];
        selectPosition = 0;
        IconFontUtil iconFontUtil = IconFontUtil.createFromAsset("fonts/iconfont.ttf");
        for(int i = 0; i < length; i++){
            newTextView(context, iconFontUtil, icons[i], i);
        }

        items[0].setBackgroundColor(selectBackColor);
        items[0].setTextColor(selectFontColor);
    }

    private void newTextView(Context context, IconFontUtil iconFontUtil, String text, int index){
        TextView tv = items[index] = new TextView(context);
        iconFontUtil.setText(tv, text);
        addView(tv);

        LinearLayout.LayoutParams params = (LayoutParams) tv.getLayoutParams();
        params.weight = 1;
        params.width = 0;
        params.height = MATCH_PARENT;
        params.gravity = Gravity.CENTER_VERTICAL;
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(params);
        tv.setTag(index);
        tv.setTextSize(24);
        tv.setTextColor(unSelectFontColor);
        tv.setBackgroundColor(unSelectBackColor);
        tv.setOnClickListener(this);
    }

    public void setBottomBarSelectListener(BottomBarSelectListener bottomBarSelectListener) {
        this.bottomBarSelectListener = bottomBarSelectListener;
    }

    @Override
    public void onClick(View v) {
        int index = (int) v.getTag();
        if(index == selectPosition){
            return;
        }
        if(bottomBarSelectListener != null){
            bottomBarSelectListener.onSelect(index);
        }

        items[selectPosition].setBackgroundColor(unSelectBackColor);
        items[selectPosition].setTextColor(unSelectFontColor);
        items[index].setBackgroundColor(selectBackColor);
        items[index].setTextColor(selectFontColor);
        selectPosition = index;
    }

    public interface BottomBarSelectListener {
        void onSelect(int id);
    }

    @SuppressWarnings("SameParameterValue")
    public void setSelectPosition(int index) {
        items[selectPosition].setBackgroundColor(unSelectBackColor);
        items[selectPosition].setTextColor(unSelectFontColor);
        items[index].setBackgroundColor(selectBackColor);
        items[index].setTextColor(selectFontColor);
        selectPosition = index;
    }
}
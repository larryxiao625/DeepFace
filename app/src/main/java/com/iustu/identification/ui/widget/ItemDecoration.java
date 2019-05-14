package com.iustu.identification.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.iustu.identification.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.iustu.identification.ui.widget.ItemDecoration.Orientation.HORIZONTAL;

/**
 * 一个简单的recyclerView的item分割线
 * 支持横向纵向，支持自定义分割线（Drawable）
 * 默认使用listView的分割线，默认分割线宽度为1/1920 * {screenHeight}
 */

public class ItemDecoration extends RecyclerView.ItemDecoration{
    @Retention(RetentionPolicy.SOURCE)
    public @interface Orientation{
        int HORIZONTAL = RecyclerView.HORIZONTAL;
        int VERTICAL = RecyclerView.VERTICAL;
    }


    private @Orientation int mOrientation;

    private int mDividerHeight = 1;

    private int mDividerWidth = 1;

    private String TAG = "itemDecoration";

    private Paint mDividerPaint;

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(mOrientation == HORIZONTAL) {
            outRect.set(0, 0, 0, mDividerHeight);
        }else {
            outRect.set(0, 0, mDividerWidth, 0);
        }
    }

    public ItemDecoration(Context context, int orientation) {
        setOrientation(orientation);
        mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDividerPaint.setColor(context.getResources().getColor(R.color.lite_blue));
        mDividerHeight = context.getResources().getDimensionPixelOffset(R.dimen.y1);
        if(mDividerHeight <= 0){
            mDividerHeight = 1;
        }
    }

    private void setOrientation(@Orientation int orientation){
        mOrientation = orientation;
    }

    public void setDividerHeight(int height){
        if(height > 0) {
            mDividerHeight = height;
        }else {
            Log.w(TAG, "invalid divider height");
        }
    }

    public void setDividerWidth(int width){
        if(width > 0){
            mDividerWidth = width;
        }else {
            Log.w(TAG, "invalid divider width");
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if(mOrientation == HORIZONTAL) {
            drawHorizontal(c, parent);
        }else {
            drawVertical(c, parent);
        }
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getMeasuredWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + layoutParams.bottomMargin;
            final int bottom = top + mDividerHeight;
            c.drawRect(left, top, right, bottom, mDividerPaint);
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent){
        final int top = parent.getPaddingTop();
        final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom();
        final int childCount = parent.getChildCount();
        for(int i = 0; i < childCount; i++){
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams  =(RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + layoutParams.rightMargin;
            final int right = left + mDividerWidth;
            c.drawRect(left, top, right, bottom, mDividerPaint);
        }
    }
}

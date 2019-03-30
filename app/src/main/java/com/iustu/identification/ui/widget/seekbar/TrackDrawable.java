package com.iustu.identification.ui.widget.seekbar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Liu Yuchuan on 2017/11/7.
 */

public class TrackDrawable extends MyDrawable{
    private int mCornerRadius;


    TrackDrawable(int color){
        super(color);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        rectF.set(bounds);
        mCornerRadius = (int) ((rectF.bottom - rectF.top) / 2);
    }


    @Override
    protected void draw(Canvas canvas, Paint paint) {
        canvas.drawRoundRect(rectF, mCornerRadius, mCornerRadius, paint);
    }
}

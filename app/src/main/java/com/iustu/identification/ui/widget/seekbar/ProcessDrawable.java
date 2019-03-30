package com.iustu.identification.ui.widget.seekbar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.IntRange;

import java.util.Locale;

/**
 * Created by Liu Yuchuan on 2017/11/7.
 */

public class ProcessDrawable extends MyDrawable {
    private int mProgress;
    private int mWidth;
    private int mCornerRadius;

    @Override
    protected void onBoundsChange(Rect bounds) {
        rectF.set(bounds);
        mCornerRadius = (int) ((rectF.bottom - rectF.top) / 2);
        mWidth = (int) (rectF.right - rectF.left) - mCornerRadius * 2;
    }

    public ProcessDrawable(int color) {
        super(color);
    }

    public void setProgress(@IntRange(from = 0, to = 1000) int progress){
        mProgress = progress;
    }

    public void setThumb(ThumbDrawable thumb){
        thumb.setX((int) (mProgress / 1000.0 * mWidth) + getBounds().left + mCornerRadius);
        thumb.setText(String.format(Locale.ENGLISH, "%.2f", mProgress / 1000.0));
    }

    @Override
    protected void draw(Canvas canvas, Paint paint) {
        rectF.set(rectF.left, rectF.top, (float) (rectF.left + mProgress / 1000.0 * mWidth) + mCornerRadius * 2, rectF.bottom);
        canvas.drawRoundRect(rectF, mCornerRadius, mCornerRadius, paint);
    }
}

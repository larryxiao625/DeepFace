package com.iustu.identification.ui.widget.seekbar;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Liu Yuchuan on 2017/11/7.
 */

public abstract class MyDrawable extends Drawable {
    private Paint mPaint;
    protected RectF rectF = new RectF();

    public MyDrawable(int color){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(color);
    }

    @Override
    public final void draw(@NonNull Canvas canvas){
        draw(canvas, mPaint);
    }

    protected abstract void draw(Canvas canvas, Paint paint);

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {}

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}

package com.iustu.identification.ui.widget.seekbar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.iustu.identification.App;
import com.iustu.identification.R;

/**
 * Created by Liu Yuchuan on 2017/11/8.
 */

public class ThumbDrawable extends MyDrawable {
    private int mX, mY;
    private int mRadius;

    private float mRectWidth;
    private float mRectHeight;
    private float mRoundRadius;

    private float mRectMargin;

    private float mTriangleWidth;
    private float mTriangleHeight;

    private RectF rectF;

    private Path path;

    private String text;

    private float mTextBaseLineY;

    private Paint mFontPaint;

    public ThumbDrawable(int color, int radius) {
        super(color);
        mRadius = radius;
        mRectWidth = App.getContext().getResources().getDimension(R.dimen.x54);
        mRectHeight = App.getContext().getResources().getDimension(R.dimen.y34);
        mRoundRadius = App.getContext().getResources().getDimension(R.dimen.x2);
        mRectMargin = App.getContext().getResources().getDimension(R.dimen.x10);
        mTriangleWidth = App.getContext().getResources().getDimension(R.dimen.x12);
        mTriangleHeight = App.getContext().getResources().getDimension(R.dimen.y6);
        rectF = new RectF();
        path = new Path();
        mFontPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFontPaint.setColor(Color.BLACK);
        mFontPaint.setTextSize(App.getContext().getResources().getDimensionPixelSize(R.dimen.x24));
        mFontPaint.setTextAlign(Paint.Align.CENTER);

    }

    public void onChange(){
        float left = mX - mRectWidth / 2;
        float top = mY - mRadius - mRectMargin - mRectHeight;
        rectF.set(left, top, left + mRectWidth, top + mRectHeight);
        path.reset();
        path.moveTo(rectF.centerX() - mTriangleWidth / 2, rectF.bottom);
        path.lineTo(rectF.centerX() + mTriangleWidth / 2, rectF.bottom);
        path.lineTo(rectF.centerX(), rectF.bottom+ mTriangleHeight);
        path.close();
        Paint.FontMetrics fontMetrics = mFontPaint.getFontMetrics();
        mTextBaseLineY = rectF.centerY() - fontMetrics.top/2 - fontMetrics.bottom/2;
    }

    public void setX(int x){
        this.mX = x;
        onChange();
    }

    public void setY(int y){
        this.mY = y;
        onChange();
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    protected void draw(Canvas canvas, Paint paint) {
        canvas.drawCircle(mX, mY, mRadius, paint);
        canvas.drawRoundRect(rectF, mRoundRadius, mRoundRadius, paint);
        canvas.drawPath(path, paint);
        canvas.drawText(text, mX, mTextBaseLineY, mFontPaint);
    }

    public double getDistance(float x, float y){
        return Math.sqrt((x - mX) * (x - mX) +  (y - mY) * (y - mY));
    }
}

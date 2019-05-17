package com.iustu.identification.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.iustu.identification.App;
import com.iustu.identification.R;

/**
 * Created by Liu Yuchuan on 2017/11/8.
 */

public class CameraDecor extends View{
    private static final int COLOR_LITE_BLUE = App.getContext().getResources().getColor(R.color.lite_blue);

    private int mCornerWidth;
    private int mCornerHeight;

    private int mStrokeWidth;

    private Paint mLinePaint;
    private Paint mCornerPaint;

    private int mWidth;
    private int mHeight;

    private Path [] paths;

    private Drawable mScanDrawable;
    private int mScanHeight;
    private int minTop, maxTop, scanTop;
    private int scanDelay = 33;
    private int mScanSpeed;

    private boolean mInScan;

    public CameraDecor(Context context) {
        this(context, null);
    }

    public CameraDecor(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraDecor(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mStrokeWidth = (int) context.getResources().getDimension(R.dimen.x4);
        mCornerWidth = context.getResources().getDimensionPixelOffset(R.dimen.x80);
        mCornerHeight = context.getResources().getDimensionPixelOffset(R.dimen.x12);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(mStrokeWidth);
        mLinePaint.setColor(COLOR_LITE_BLUE);
        paths = new Path[4];
        for(int i = 0; i < paths.length; i++){
            paths[i] = new Path();
        }
        mCornerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCornerPaint.setColor(COLOR_LITE_BLUE);
        if(attrs != null){
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CameraDecor);
            mStrokeWidth = ta.getDimensionPixelOffset(R.styleable.CameraDecor_outLineStrokeWidth, mStrokeWidth);
            mCornerWidth = ta.getDimensionPixelOffset(R.styleable.CameraDecor_cornerWidth, mCornerWidth);
            mCornerHeight = ta.getDimensionPixelOffset(R.styleable.CameraDecor_cornerHeight, mCornerHeight);
            mScanDrawable = ta.getDrawable(R.styleable.CameraDecor_scanDrawable);
            mScanHeight = ta.getDimensionPixelOffset(R.styleable.CameraDecor_scanHeight, 0);
            mScanSpeed = ta.getDimensionPixelOffset(R.styleable.CameraDecor_scanSpeed, context.getResources().getDimensionPixelOffset(R.dimen.y10));
            scanDelay = ta.getInteger(R.styleable.CameraDecor_scanDelay, 33);
            ta.recycle();
        }
        mInScan = false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mStrokeWidth = (int) App.getContext().getResources().getDimension(R.dimen.x4);

        for (Path path : paths) {
            path.reset();
        }

        int x = 0;
        int y = 0;
        paths[0].moveTo(x, y);
        paths[0].lineTo(x + mCornerWidth, y);
        paths[0].lineTo(x + mCornerWidth, y + mCornerHeight);
        paths[0].lineTo(x + mCornerHeight, y + mCornerHeight);
        paths[0].lineTo(x + mCornerHeight, y + mCornerWidth);
        paths[0].lineTo(x, y + mCornerWidth);
        x += mWidth;
        paths[1].moveTo(x, y);
        paths[1].lineTo(x - mCornerWidth, y);
        paths[1].lineTo(x - mCornerWidth, y + mCornerHeight);
        paths[1].lineTo(x - mCornerHeight, y + mCornerHeight);
        paths[1].lineTo(x - mCornerHeight, y + mCornerWidth);
        paths[1].lineTo(x, y + mCornerWidth);
        y += mHeight;
        paths[2].moveTo(x, y);
        paths[2].lineTo(x, y - mCornerWidth);
        paths[2].lineTo(x - mCornerHeight, y - mCornerWidth);
        paths[2].lineTo(x - mCornerHeight, y - mCornerHeight);
        paths[2].lineTo(x - mCornerWidth, y - mCornerHeight);
        paths[2].lineTo(x - mCornerWidth, y);
        x -= mWidth;
        paths[3].moveTo(x, y);
        paths[3].lineTo(x, y - mCornerWidth);
        paths[3].lineTo(x + mCornerHeight, y - mCornerWidth);
        paths[3].lineTo(x + mCornerHeight, y - mCornerHeight);
        paths[3].lineTo(x + mCornerWidth, y - mCornerHeight);
        paths[3].lineTo(x + mCornerWidth, y);
        for (Path path : paths) {
            path.close();
        }
        minTop = -mScanHeight / 2;
        maxTop = mHeight - mScanHeight / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, mWidth, mHeight, mLinePaint);
        for (Path path : paths) {
            canvas.drawPath(path, mCornerPaint);
        }
        if(mInScan && mScanDrawable != null){
            mScanDrawable.setBounds(0, scanTop, mWidth, scanTop + mScanHeight);
            mScanDrawable.draw(canvas);
            scanTop += mScanSpeed;
            if(scanTop > maxTop){
                scanTop = minTop;
            }
            postInvalidateDelayed(scanDelay);
        }
    }

    public void startScan(){
        mInScan = true;
        invalidate();
    }

    public void stopScan(){
        mInScan = false;
        scanTop = 0;
        invalidate();
    }
}

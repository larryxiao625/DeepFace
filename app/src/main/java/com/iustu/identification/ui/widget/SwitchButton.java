package com.iustu.identification.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.iustu.identification.App;
import com.iustu.identification.R;

/**
 * Created by Liu Yuchuan on 2017/11/10.
 */

public class SwitchButton extends View implements View.OnClickListener{
    private boolean on;

    private static final int DEFAULT_WIDTH;
    private static final int DEFAULT_HEIGHT;
    private static final int MARGIN_ROUND;

    static {
        float ratio = App.getContext()
                .getResources()
                .getDisplayMetrics()
                .density;
        DEFAULT_WIDTH = (int) (54 * ratio);
        DEFAULT_HEIGHT = (int) (24 * ratio);
        MARGIN_ROUND = (int) (2 * ratio);
    }

    private int COLOR_ON = Color.parseColor("#f79204");
    private int COLOR_OFF = Color.parseColor("#8eb9f3");

    private int mWidth;
    private int mHeight;
    private float mTextBaseLineY;

    private Paint mOutLinePaint, mCirclePaint, mTextPaint;
    private RectF rectF;
    private int mCornerRadius;

    private int mCircleRadius;

    private String textOn = "开", textOff = "关";

    private OnSwitchListener onSwitchListener;

    @Override
    public void onClick(View v) {
        on = !on;
        invalidate();
        if(onSwitchListener != null){
            onSwitchListener.onSwitch(this, on);
        }
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {}

    public interface OnSwitchListener{
        void onSwitch(View view, boolean on);
    }

    public SwitchButton(Context context) {
        this(context, null);
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mOutLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOutLinePaint.setColor(Color.rgb(21, 143, 252));
        mOutLinePaint.setStyle(Paint.Style.STROKE);
        rectF = new RectF();
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(COLOR_ON);
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(context.getResources().getColor(R.color.lite_blue));
        mTextPaint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.y34));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTypeface(Typeface.MONOSPACE);
        super.setOnClickListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(rectF, mCornerRadius, mCornerRadius, mOutLinePaint);
        float cx, cy, x;
        if(on){
            mCirclePaint.setColor(COLOR_ON);
            cy = mCornerRadius;
            cx = mWidth - mCornerRadius;
            x = (cx - mCornerRadius) / 2;
        }else {
            mCirclePaint.setColor(COLOR_OFF);
            cx = cy =  mCornerRadius;
            x = (cx + mCornerRadius + mWidth) / 2;
        }
        canvas.drawCircle(cx, cy, mCircleRadius, mCirclePaint);
        String text = on?textOn:textOff;
        canvas.drawText(text, x, mTextBaseLineY, mTextPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rectF.set(0, 0, mWidth, mHeight);
        mCornerRadius = mHeight / 2;
        mCircleRadius = mCornerRadius - MARGIN_ROUND;
        Paint.FontMetrics metrics = mTextPaint.getFontMetrics();
        mTextBaseLineY = mHeight / 2 - metrics.bottom / 2 - metrics.top/2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int wm = MeasureSpec.getMode(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int hm = MeasureSpec.getMode(heightMeasureSpec);
        if(wm == MeasureSpec.EXACTLY){
            mWidth = w;
        }else {
            mWidth = DEFAULT_WIDTH;
        }

        if(hm == MeasureSpec.EXACTLY){
            mHeight = h;
        }else {
            mHeight = DEFAULT_HEIGHT;
        }

        setMeasuredDimension(MeasureSpec.makeMeasureSpec(mWidth, wm), MeasureSpec.makeMeasureSpec(mHeight, hm));
    }

    public void setOnSwitchListener(OnSwitchListener onSwitchListener) {
        this.onSwitchListener = onSwitchListener;
    }

    public void setSwitch(boolean on){
        this.on = on;
        invalidate();
    }
}

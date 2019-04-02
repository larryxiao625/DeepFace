package com.iustu.identification.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.iustu.identification.App;
import com.iustu.identification.R;

/**
 * Created by Liu Yuchuan on 2017/11/4.
 */

public class ScaleView extends View{
    private static final String TAG = "ScaleView";

    private static final String KEY_SCALE = "scale";

    private final int COLOR_BACK = Color.rgb(73, 86, 105);
    private final int COLOR_SCALE = Color.rgb(210, 159, 67);
    private final int COLOR_FONT = Color.rgb(255, 255, 255);

    private int mNumberTextSize;
    private int mSimilarityTextSize;
    private int mInnerRadius;
    private int mOutRadius;
    private int mRadius;

    private Paint mBackPaint;
    private Paint mScalePaint;
    private Paint mNumberPaint;
    private Paint mTextPaint;

    private RectF mRectF;

    private int mScale;

    private boolean textEnable;

    private Rect measureRect;
    private float numberBaseLine;
    private float textBaseLine;
    private int textMargin;

    public ScaleView(Context context) {
        super(context, null);
    }

    public ScaleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScaleView);
        mOutRadius = typedArray.getDimensionPixelOffset(R.styleable.ScaleView_outRadius, context.getResources().getDimensionPixelOffset(R.dimen.y95));
        mInnerRadius = typedArray.getDimensionPixelOffset(R.styleable.ScaleView_innerRadius, context.getResources().getDimensionPixelOffset(R.dimen.y80));
        mNumberTextSize = typedArray.getDimensionPixelSize(R.styleable.ScaleView_numberTextSize, context.getResources().getDimensionPixelOffset(R.dimen.y60));
        mSimilarityTextSize = typedArray.getDimensionPixelSize(R.styleable.ScaleView_similarityTextSize, context.getResources().getDimensionPixelOffset(R.dimen.y50));
        textEnable = typedArray.getBoolean(R.styleable.ScaleView_textEnable, false);
        if(mInnerRadius >= mOutRadius){
            throw new IllegalArgumentException("InnerRadius should < outRadius");
        }
        mScale = typedArray.getInt(R.styleable.ScaleView_scale, 50);
        if(mScale > 100) {
            mScale = 100;
            Log.w(TAG, "scale should <= 100");
        }
        typedArray.recycle();
        initView();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(mOutRadius, mOutRadius, mRadius, mBackPaint);
        canvas.drawArc( mRectF, 270, -mScale * 360 / 100, false, mScalePaint);

        if(!textEnable) {
            Paint.FontMetrics fontMetrics = mNumberPaint.getFontMetrics();
            float top = fontMetrics.top;
            float bottom = fontMetrics.bottom;
            int baseLineY = (int) (mOutRadius - top / 2 - bottom / 2);
            canvas.drawText(mScale + "%", mOutRadius, baseLineY, mNumberPaint);
        }else {
            canvas.drawText(mScale + "%", mOutRadius, numberBaseLine, mNumberPaint);
            canvas.drawText("相似度", mOutRadius, textBaseLine, mTextPaint);
        }
    }

    private void initView(){
        textMargin = App.getContext().getResources().getDimensionPixelOffset(R.dimen.y5);
        int width = mOutRadius - mInnerRadius;
        mRadius = (mOutRadius + mInnerRadius) / 2;
        mBackPaint = new Paint();
        mBackPaint.setStyle(Paint.Style.STROKE);
        mBackPaint.setStrokeWidth(width);
        mBackPaint.setColor(COLOR_BACK);
        mBackPaint.setAntiAlias(true);
        mScalePaint = new Paint();
        mScalePaint.setStyle(Paint.Style.STROKE);
        mScalePaint.setStrokeWidth(width);
        mScalePaint.setColor(COLOR_SCALE);
        mScalePaint.setAntiAlias(true);
        mNumberPaint = new Paint();
        mNumberPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mNumberPaint.setColor(COLOR_FONT);
        mNumberPaint.setTextAlign(Paint.Align.CENTER);
        mNumberPaint.setAntiAlias(true);
        mNumberPaint.setTextSize(mNumberTextSize);
        mTextPaint = new Paint();
        mTextPaint.setColor(App.getContext().getResources().getColor(R.color.lite_blue));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mSimilarityTextSize);
        mRectF = new RectF(mOutRadius - mRadius, mOutRadius - mRadius, mRadius + mOutRadius, mRadius + mOutRadius);
        measureRect = new Rect();
        mNumberPaint.getTextBounds("0", 0, 1, measureRect);
        Paint.FontMetrics fontMetrics = mNumberPaint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;
        numberBaseLine = mOutRadius - measureRect.height()/2 - textMargin - top / 2 - bottom / 2;
        mTextPaint.getTextBounds("相似度", 0, 1, measureRect);
        fontMetrics = mTextPaint.getFontMetrics();
        top = fontMetrics.top;
        bottom = fontMetrics.bottom;
        textBaseLine = mOutRadius + textMargin + measureRect.height() / 2  - top / 2 - bottom / 2;
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable =  super.onSaveInstanceState();
        IntegerState state = new IntegerState(parcelable);
        state.state = mScale;
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        IntegerState scaleState = (IntegerState) state;
        mScale = scaleState.state;
        invalidate();
    }

    public void setScale(int scale){
        if(scale < 0){
            scale = 0;
            Log.w(TAG, "scale should >= 100");
        }else if(scale > 100){
            scale = 100;
            Log.w(TAG, "scale should <= 100");
        }
        mScale = scale;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = mOutRadius * 2;
        int spec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        setMeasuredDimension(spec, spec);
    }
}

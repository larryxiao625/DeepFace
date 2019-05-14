package com.iustu.identification.ui.widget.seekbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.iustu.identification.R;
import com.iustu.identification.ui.widget.IntegerState;

/**
 * Created by Liu Yuchuan on 2017/11/7.
 */

public class BubbleSeekBar extends View{

    private int mTrackHeight;
    private int mTrackWidth;
    private int mWidth;

    private int mProgress = 300;

    private int mThumbRadius;

    private ProcessDrawable mProcessDrawable;
    private TrackDrawable mTrackDrawable;
    private ThumbDrawable mThumbDrawable;

    private Paint mFontPaint;

    private float mTextBaseLineY;

    private float zeroX;
    private float oneX;
    private float textSize;

    private float textMargin;

    private float mVerticalMargin;
    private int mTextWidth;

    private final int COLOR_LITE_BLUE;
    private final int COLOR_ORANGE = Color.parseColor("#efb23f");

    private boolean isDrag;
    private float mLastX;

    public interface OnProgressChangeListener{
        void onProgressChange(View view, int progress);
    }

    private OnProgressChangeListener onProgressChangeListener;

    public void setOnProgressChangeListener(OnProgressChangeListener onProgressChangeListener) {
        this.onProgressChangeListener = onProgressChangeListener;
    }

    public BubbleSeekBar(Context context) {
        this(context, null);
    }

    public BubbleSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        COLOR_LITE_BLUE = context.getResources().getColor(R.color.lite_blue);
        mProcessDrawable = new ProcessDrawable(COLOR_ORANGE);
        mThumbRadius = context.getResources().getDimensionPixelOffset(R.dimen.y20);
        mThumbDrawable = new ThumbDrawable(COLOR_ORANGE, mThumbRadius);
        mTrackDrawable = new TrackDrawable(COLOR_LITE_BLUE);
        mTrackHeight = context.getResources().getDimensionPixelOffset(R.dimen.y16);
        mFontPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFontPaint.setColor(Color.WHITE);
        textSize = context.getResources().getDimensionPixelSize(R.dimen.y36);
        mFontPaint.setTextSize(textSize);
        zeroX = context.getResources().getDimension(R.dimen.x60);
        textMargin = context.getResources().getDimension(R.dimen.x30);
        mVerticalMargin = context.getResources().getDimension(R.dimen.y60);
        Rect rect = new Rect();
        mFontPaint.getTextBounds("0", 0, 1, rect);
        mTextWidth = rect.width();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mTrackDrawable.draw(canvas);
        mProcessDrawable.setProgress(mProgress);
        mProcessDrawable.draw(canvas);
        mProcessDrawable.setThumb(mThumbDrawable);
        mThumbDrawable.draw(canvas);
        canvas.drawText("0", zeroX, mTextBaseLineY, mFontPaint);
        canvas.drawText("1", oneX, mTextBaseLineY, mFontPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int left  = (int) (zeroX + mTextWidth + textMargin);
        int top = (h - mTrackHeight) / 2;
        int right = w - left;
        int bottom = top + mTrackHeight;
        mTrackWidth = right - left - 2 * mThumbRadius;
        mTrackDrawable.setBounds(left, top, right, bottom);
        mThumbDrawable.setY(mTrackDrawable.getBounds().centerY());
        mProcessDrawable.setBounds(mTrackDrawable.getBounds());
        Paint.FontMetrics fontMetrics = mFontPaint.getFontMetrics();
        mTextBaseLineY = mTrackDrawable.getBounds().centerY() - fontMetrics.top/2 - fontMetrics.bottom/2;
        oneX = right + textMargin;
    }

    public void setProgress(int progress){
        this.mProgress = progress;
        mProcessDrawable.setProgress(progress);
        mProcessDrawable.setThumb(mThumbDrawable);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        int ws = MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY);
        int hs = MeasureSpec.makeMeasureSpec((int) ((mThumbRadius + mVerticalMargin) * 2), MeasureSpec.EXACTLY);
        setMeasuredDimension(ws, hs);
    }

    public double getNumber(){
        return mProgress / 1000.0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        final  int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action){
            case MotionEvent.ACTION_DOWN:
                if(mThumbDrawable.getDistance(x, y) <= mThumbRadius * 3){
                    isDrag = true;
                    mLastX = x;
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                if(isDrag) {
                    isDrag = false;
                    if(onProgressChangeListener != null){
                        onProgressChangeListener.onProgressChange(this, mProgress);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(isDrag) {
                    int deltaProgress = (int) ((x - mLastX) / mTrackWidth * 1000);
                    if (Math.abs(deltaProgress) > 0) {
                        int newProgress = mProgress + deltaProgress;
                        if (newProgress > 1000)
                            newProgress = 1000;
                        else if (newProgress < 0)
                            newProgress = 0;
                        setProgress(newProgress);
                        mLastX = x;
                    }
                }
                break;
        }

        return true;
    }


    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable =  super.onSaveInstanceState();
        IntegerState state = new IntegerState(parcelable);
        state.state = mProgress;
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        IntegerState progressState = (IntegerState) state;
        setProgress(progressState.state);
    }
}

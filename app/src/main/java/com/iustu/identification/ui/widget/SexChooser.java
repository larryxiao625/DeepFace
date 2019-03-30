package com.iustu.identification.ui.widget;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.iustu.identification.R;

/**
 * Created by Liu Yuchuan on 2017/11/5.
 */

public class SexChooser extends FrameLayout implements View.OnClickListener{

    public static final int ALL = 0;

    public static final int MALE = 1;

    public static final int FEMALE = 2;

    private int choose;

    private TextView[]  textViews = new TextView[3];

    public interface OnSexChosenListener{
        void onChooseSex(View view,int sex);
    }

    private OnSexChosenListener onSexChosenListener;

    public void setOnSexChosenListener(OnSexChosenListener onSexChosenListener) {
        this.onSexChosenListener = onSexChosenListener;
    }

    public SexChooser(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.widget_sex_chooser, this);
        textViews[ALL] = findViewById(R.id.all_tv);
        textViews[MALE] = findViewById(R.id.male_tv);
        textViews[FEMALE] = findViewById(R.id.female_tv);
        for(int i = 0; i < 3; i++){
            textViews[i].setOnClickListener(this);
        }
        choose = ALL;
        textViews[ALL].setBackgroundResource(R.drawable.quanbu1);
    }

    public void setChoose(int sex){
        if(sex == choose)
            return;
        textViews[choose].setBackgroundResource(R.drawable.quanbu2);
        textViews[sex].setBackgroundResource(R.drawable.quanbu1);
        this.choose = sex;
        if(onSexChosenListener != null){
            onSexChosenListener.onChooseSex(this, choose);
        }
    }

    public int getChoose() {
        return choose;
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable =  super.onSaveInstanceState();
        IntegerState state = new IntegerState(parcelable);
        state.state = choose;
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        IntegerState scaleState = (IntegerState) state;
        setChoose(scaleState.state);
    }

    @Override
    public void onClick(View v) {
        for(int i = 0; i < 3; i++){
            if(v == textViews[i]){
                setChoose(i);
                break;
            }
        }
    }
}

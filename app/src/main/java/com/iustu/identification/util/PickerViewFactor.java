package com.iustu.identification.util;

import android.content.Context;
import android.graphics.Color;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.iustu.identification.App;
import com.iustu.identification.R;

/**
 * Created by Liu Yuchuan on 2017/11/15.
 */

public class PickerViewFactor {
    private PickerViewFactor(){}

    public static OptionsPickerView.Builder newPickerViewBuilder(Context context, OptionsPickerView.OnOptionsSelectListener listener){
        int white = Color.parseColor("#ffffff");
        int liteBlue = App.getContext().getResources().getColor(R.color.lite_blue);
        return new OptionsPickerView.Builder(context, listener)
                .setTitleColor(white)
                .setSubmitColor(liteBlue)
                .setCancelColor(liteBlue)
                .setDividerColor(liteBlue)
                .setTitleBgColor(App.getContext().getResources().getColor(R.color.colorBackground))
                .setBgColor(App.getContext().getResources().getColor(R.color.colorBackgroundLite))
                .setTextColorCenter(white)
                .setTextColorOut(white);
    }

    public static TimePickerView.Builder newTimePickerViewBuilder(Context context, TimePickerView.OnTimeSelectListener listener){
        int white = Color.parseColor("#ffffff");
        int liteBlue = App.getContext().getResources().getColor(R.color.lite_blue);
        return new TimePickerView.Builder(context, listener)
                .setTitleColor(white)
                .setSubmitColor(liteBlue)
                .setCancelColor(liteBlue)
                .setDividerColor(liteBlue)
                .setTitleBgColor(App.getContext().getResources().getColor(R.color.colorBackground))
                .setBgColor(App.getContext().getResources().getColor(R.color.colorBackgroundLite))
                .setTextColorCenter(white)
                .setTextColorOut(white)
                .setType(new boolean[]{true, true, true, false, false, false});
    }
}

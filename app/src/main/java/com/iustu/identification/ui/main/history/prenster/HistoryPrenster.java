package com.iustu.identification.ui.main.history.prenster;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.iustu.identification.App;
import com.iustu.identification.ui.main.history.view.IVew;
import com.iustu.identification.util.PickerViewFactor;
import com.iustu.identification.util.TextUtil;

import java.util.Calendar;

public class HistoryPrenster implements IPrenster{
    IVew iVew;

    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();
    int START_CALENDER=0; //fromDatChoose
    int END_CALENDER=1; //toDateChoose
    Context context;
    public HistoryPrenster(Context context){
        this.context=context;
    }
    @Override
    public void attchView(IVew iVew) {
        this.iVew=iVew;
    }

    @Override
    public void initCalender() {
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);
        iVew.setFromDateTv(TextUtil.getDateString(startCalendar.getTime()));
        iVew.setToDateTv(TextUtil.getDateString(endCalendar.getTime()));
    }

    @Override
    public void initDateChoose(int type) {
        Calendar c = Calendar.getInstance();
        if(type==0) {
            c.roll(Calendar.YEAR, -100);
        }else if(type==1){
            c.roll(Calendar.YEAR,100);
        }
        TimePickerView timePickerView= PickerViewFactor.newTimePickerViewBuilder(context,(date, v)->{
                if(type==0){
                    startCalendar.setTime(date);
                    startCalendar.set(Calendar.HOUR_OF_DAY, 0);
                    startCalendar.set(Calendar.MINUTE, 0);
                    startCalendar.set(Calendar.SECOND, 0);
                    iVew.setFromDateTv(TextUtil.getDateString(date));
                }else if(type==1){
                    endCalendar.setTime(date);
                    endCalendar.set(Calendar.HOUR_OF_DAY, 23);
                    endCalendar.set(Calendar.MINUTE, 59);
                    endCalendar.set(Calendar.SECOND, 59);
                    iVew.setToDateTv(TextUtil.getDateString(date));
                }
            }
        ).setRangDate(type==0? c:startCalendar,type==0? endCalendar:c)
                .setDate(type==0? startCalendar:endCalendar)
                .build();
        iVew.showDateChoose(timePickerView);
    }
}

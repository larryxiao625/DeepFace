package com.iustu.identification.ui.main.history.view;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;

public interface IVew {
    void setToDateTv(String date);
    void setFromDateTv(String date);
    void showDateChoose(TimePickerView timePickerView);
}

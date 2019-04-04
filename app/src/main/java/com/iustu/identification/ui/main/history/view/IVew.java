package com.iustu.identification.ui.main.history.view;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.iustu.identification.ui.widget.dialog.NormalDialog;
import com.iustu.identification.ui.widget.dialog.SingleButtonDialog;
import com.iustu.identification.ui.widget.dialog.WaitProgressDialog;

public interface IVew {
    void setToDateTv(String date);
    void setFromDateTv(String date);
    void showDateChoose(TimePickerView timePickerView);
    void showQueryError(NormalDialog normalDialog);
    void showQueryProcessing(WaitProgressDialog waitProgressDialog);
    void showArgumentsError(SingleButtonDialog singleButtonDialog);
}

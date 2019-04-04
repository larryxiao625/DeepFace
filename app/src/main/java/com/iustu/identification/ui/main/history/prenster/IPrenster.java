package com.iustu.identification.ui.main.history.prenster;

import com.iustu.identification.ui.main.history.view.HistoryFragment;
import com.iustu.identification.ui.main.history.view.IVew;

public interface IPrenster {
    void attchCompareHistoryView(IVew iVew);
    void initCalender(int viewType);
    void initDateChoose(int viewType,int calenderType);
    void queryError(int viewType);
    void queryProcessing(int viewType);
    void argumentsError(int viewType);
    void startQuery();
    void attchFaceHistoryView(IVew iVew);
    void attchSwitchFragment(HistoryFragment.SwitchFragmentLister switchFragmentLister);
}

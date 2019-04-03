package com.iustu.identification.ui.main.history.prenster;

import com.iustu.identification.ui.main.history.view.IVew;

public interface IPrenster {
    void attchView(IVew iVew);
    void initCalender();
    void initDateChoose(int type);
}

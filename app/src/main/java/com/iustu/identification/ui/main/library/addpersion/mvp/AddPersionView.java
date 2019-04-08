package com.iustu.identification.ui.main.library.addpersion.mvp;

import com.iustu.identification.entity.PersionInfo;

/**
 * created by sgh, 2019-4-3
 * AddPersion
 */
public interface AddPersionView {
    void setPresenter(AddPersionPresenter presenter);

    void onAddPersion(PersionInfo persionInfo);
    void showWaitDialog(String content);
    void dissmissDialog();
    void onAddError(String information);
    void onAddSuccess();
}

package com.iustu.identification.ui.main.library.addperson.mvp;

import com.iustu.identification.entity.PersonInfo;

/**
 * created by sgh, 2019-4-3
 * AddPersion
 */
public interface AddPersionView {
    void setPresenter(AddPersionPresenter presenter);

    void onAddPersion(PersonInfo personInfo);
    void showWaitDialog(String content);
    void dissmissDialog();
    void onAddError(String information);
    void onAddSuccess();
}

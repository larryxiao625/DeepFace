package com.iustu.identification.ui.login.view;

import com.iustu.identification.ui.widget.dialog.EditDialog;
import com.iustu.identification.ui.widget.dialog.NormalDialog;
import com.iustu.identification.ui.widget.dialog.SingleButtonDialog;
import com.iustu.identification.ui.widget.dialog.WaitProgressDialog;

public interface IVew {
    void showServerDialog(EditDialog editDialog);
    void showLoginFail(SingleButtonDialog singleButtonDialog);
    void showDataFailLoad(NormalDialog normalDialog);
    void disposeRxjava();
    void showWaitDialog(WaitProgressDialog waitProgressDialog);
    void loginSuccessfully();
}

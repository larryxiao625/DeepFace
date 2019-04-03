package com.iustu.identification.ui.login.prenster;

import com.iustu.identification.ui.login.view.IVew;

public interface IPrenster {
    void attchView(IVew iVew);
    void setServer();
    void getLoginFailDialog(String cause);
    void getDataLoadFail();
    void getWaitProgressDialog(String title);
    void normalLogin(String username,String password);
}

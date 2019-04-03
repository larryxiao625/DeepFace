package com.iustu.identification.ui.main.library.addpersion.mvp;

/**
 * created by sgh， 2019-4-3
 */
public class AddPersionPresenter {

    AddPersionModel model;
    AddPersionView view;

    public AddPersionPresenter(AddPersionModel model) {
        this.model = model;
    }

    public void setView(AddPersionView view) {
        this.view = view;
    }
    /**
     * 点击“提交”按钮时触发
     */
    public void onAddPersion() {

    }
}

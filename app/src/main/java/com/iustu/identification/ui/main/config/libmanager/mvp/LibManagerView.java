package com.iustu.identification.ui.main.config.libmanager.mvp;

import com.iustu.identification.entity.Library;

import java.util.List;

public interface LibManagerView {
    void setPresenter(LibManagerPesenter presenter);
    void initData();
    void bindData(List<Library> data);
    void updateLibrary();
    void onFailed(String e);
    void onSuccess();
    void showWaitDialog(String content);
    void dissmissDialog();
}

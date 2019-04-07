package com.iustu.identification.ui.main.library.librariesmanage.mvp;

import com.iustu.identification.entity.Library;
import com.iustu.identification.ui.widget.dialog.WaitProgressDialog;

import java.util.List;

/**
 * created by sgh , 2019-4-3
 * 只是一个标记接口，由LibrariesManageFragment实现
 */
public interface LibView {
    void setPresenter (LibPresenter presenter);
    void bindData(List<Library> data);
    void showWaitDialog();
    void dissmissDialog();
}

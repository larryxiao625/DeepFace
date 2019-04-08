package com.iustu.identification.ui.main.library.librariesmanage.mvp;

import android.content.ContentValues;

import com.iustu.identification.entity.Library;
import com.iustu.identification.ui.widget.dialog.WaitProgressDialog;

import java.util.List;

/**
 * created by sgh , 2019-4-3
 * 只是一个标记接口，由LibrariesManageFragment实现
 */
public interface LibView {
    public static final int TYPE_ADD_LIB = 0;
    public static final int TYPE_DELETE_LIB = 1;
    public static final int TYPE_MODIFY_LIB = 2;

    void setPresenter (LibPresenter presenter);
    void bindData(List<Library> data);
    void showWaitDialog(String content);
    void dissmissDialog();
    void onError(String message);
    void onSuccess(int type, int position, ContentValues values);
}

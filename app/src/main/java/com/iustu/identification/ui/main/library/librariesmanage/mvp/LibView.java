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
    int TYPE_ADD_LIB = 0;
    int TYPE_DELETE_LIB = 1;
    int TYPE_MODIFY_LIB = 2;

    void setPresenter (LibPresenter presenter);

    /**
     * 获取人脸库成功时调用
     * 将获取到的数据绑定到列表View中
     * @param data 从数据库获取到的所有人脸库
     */
    void bindData(List<Library> data);

    /**
     * 在获取所有人脸库的时候显示等待框
     * @param content 等待框的文字内容
     */
    void showWaitDialog(String content);

    /**
     * 在获取所有人脸库成功或者失败时
     * 关闭等待框
     */
    void dissmissDialog();

    /**
     * 在获取人脸库失败时调用
     * @param message 失败的信息
     */
    void onError(String message);

    /**
     * 获取所有人脸库成功时回调
     * @param type 代表当前进行的是何种操作，其范围为该iterface中定义的三个int中
     *     int TYPE_ADD_LIB = 0;
     *     int TYPE_DELETE_LIB = 1;
     *     int TYPE_MODIFY_LIB = 2;
     * @param position type对应的操作发生在所有人脸库中第几个人脸库(位置是从0开始的)
     * @param values 如果是添加人脸库的话，新人脸库转化的ContentValues
     */
    void onSuccess(int type, int position, ContentValues values);
}

package com.iustu.identification.ui.main.library.addperson.mvp;

import com.iustu.identification.entity.PersionInfo;
import com.iustu.identification.entity.PersonInfo;

/**
 * created by sgh, 2019-4-3
 * AddPersion
 */
public interface AddPersionView {
    void setPresenter(AddPersionPresenter presenter);

    void onAddPersion(PersionInfo personInfo);

    /**
     * 往人脸库中添加人员信息时显示等待对话框
     * @param content 显示的文字
     */
    void showWaitDialog(String content);

    /**
     * 往人脸库中添加人员信息成功或者失败时调用，关闭等待对话框
     */
    void dissmissDialog();

    /**
     * 往人脸库中添加人员信息失败时调用，弹出Toast提示错误信息
     * @param information 错误信息
     */
    void onAddError(String information);

    /**
     * 往人脸库中添加人员信息时调用
     */
    void onAddSuccess();
}

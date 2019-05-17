package com.iustu.identification.ui.main.config.libmanager.mvp;

import com.iustu.identification.entity.Library;

import java.util.List;

public interface LibManagerView {
    void setPresenter(LibManagerPesenter presenter);

    /**
     * 初始化数据
     * 是发起从数据库获取人脸库的地方
     */
    void initData();

    /**
     * 获取人脸库成功时调用
     * 将获取到的数据绑定到列表View中
     * @param data 从数据库获取到的所有人脸库
     */
    void bindData(List<Library> data);

    /**
     * 在获取人脸库失败时调用
     * @param e 失败的信息
     */
    void onFailed(String e);

    /**
     * 获取所有人脸库成功时回调
     */
    void onSuccess();

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
}

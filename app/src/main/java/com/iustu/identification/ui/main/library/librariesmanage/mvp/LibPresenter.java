package com.iustu.identification.ui.main.library.librariesmanage.mvp;

import com.iustu.identification.entity.Library;

import java.util.List;

/**
 * created by sgh, 2019-4-3
 * 人脸库管理界面的Presenter
 */
public class LibPresenter {

    private LibModel mModel;
    private LibView mView;

    public LibPresenter(LibModel model) {
        this.mModel = model;
    }

    public void setView (LibView view) {
        this.mView = view;
    }

    /**
     * 初始加载界面的时候获取所有的人脸库
     */
    public void onInitData() {
    }

    /**
     * 点击“下一页”的时候加载更多人脸库
     */
    public void onLoadMore() {
    }

    /**
     * 添加新人脸库的逻辑代码
     */
    public void onCreateNewLib() {

    }

    /**
     * 删除人脸库的逻辑代码
     */
    public void onDeleteLib() {

    }

    /**
     * 更改人脸库信息的逻辑代码
     */
    public void onModifyLib() {

    }
}
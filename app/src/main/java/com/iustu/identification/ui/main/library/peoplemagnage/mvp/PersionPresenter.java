package com.iustu.identification.ui.main.library.peoplemagnage.mvp;

/**
 * created by sgh, 2019-4-3
 * 人脸库详情管理的Presenter
 */
public class PersionPresenter {

    PersionView mView;
    PersionModel mModel;

    public PersionPresenter (PersionModel model) {
        this.mModel = model;
    }

    public void setView(PersionView view){
        this.mView = view;
    }
    /**
     * 初始加载数据调用
     */
    public void onInitData() {

    }

    /**
     * 点击“下一页”加载更多的时候调用
     */
    public void onLoadMore() {

    }

    /**
     * 点击"添加照片"的时候调用
     */
    public void onAddPhoto() {

    }

    /**
     * 点击“删除照片”的时候调用
     */
    public void onDeletePhoto() {

    }

    /**
     * 点击“删除”的时候调用
     */
    public void onDeletePer() {

    }

    /**
     * 点击“保存”的时候调用
     */
    public void onSaveChange() {

    }
}

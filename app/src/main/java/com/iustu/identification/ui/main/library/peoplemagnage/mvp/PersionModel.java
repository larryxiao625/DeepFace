package com.iustu.identification.ui.main.library.peoplemagnage.mvp;

import com.iustu.identification.entity.PersionInfo;

import java.util.List;

/**
 * created by sgh, 2019-4-3
 * 人脸库详情管理的Model
 */
public class PersionModel {

    /**
     * 初始加载数据在PersionPresenter中被调用
     */
    public List<PersionInfo> initData() {
        return null;
    }

    /**
     * 点击“下一页”加载更多的时候在PersionPresenter中被调用
     */
    public List<PersionModel> loadMore() {
        return null;
    }

    /**
     * 点击"添加照片"的时候在PersionPresenter中被调用
     */
    public void addPhoto() {

    }

    /**
     * 点击“删除照片”的时候在PersionPresenter中被调用
     */
    public void deletePhoto() {

    }

    /**
     * 点击“删除”的时候在PersionPresenter中被调用
     */
    public void deletePer() {

    }

    /**
     * 点击“保存”的时候在PersionPresenter中被调用
     */
    public void saveChange() {

    }
}

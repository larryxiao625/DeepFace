package com.iustu.identification.ui.main.library.librariesmanage.mvp;

import com.iustu.identification.entity.Library;

import java.util.List;

/**
 * created by sgh, 2019-4-3
 * 人脸库管理的model
 */
public class LibModel {

    /**
     * 初始加载数据的时候在LibPresenter中调用
     * @return 从数据库中得到的数据
     */
    public List<Library> initData() {
        return null;
    }

    /**
     * 点击“下一页”加载更多的时候在LibPresenter中调用
     * @return 从数据库中得到的数据
     */
    public List<Library> loadMore() {
        return null;
    }

    /**
     * 添加新人脸库的逻辑代码
     */
    public void createNewLib() {

    }

    /**
     * 删除人脸库的逻辑代码
     */
    public void deleteLib() {

    }

    /**
     * 更改人脸库信息的逻辑代码
     */
    public void modifyLib() {

    }
}

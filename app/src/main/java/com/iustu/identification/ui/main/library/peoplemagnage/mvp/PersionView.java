package com.iustu.identification.ui.main.library.peoplemagnage.mvp;

import android.content.ContentValues;

import com.iustu.identification.entity.PersionInfo;

import java.util.List;

public interface PersionView {
    public static final int TYPE_ADD_PHOTO = 0;
    public static final int TYPE_DELETE_PHOTO = 1;
    public static final int TYPE_DELETE_PER = 2;
    public static final int TYPE_SAVE_CHANGE = 3;

    void setPresenter(PersionPresenter persenter);

    void bindData(List<PersionInfo> data);
    /**
     * 初始加载数据调用
     */
    void onInitData();

    /**
     * 点击“下一页”加载更多的时候调用
     */
    void onLoadMore();

    /**
     * 点击"添加照片"的时候调用
     */
    void onAddPhoto();

    /**
     * 点击“删除照片”的时候调用
     */
    void onDeletePhoto();

    /**
     * 点击“删除”的时候调用
     */
    void onDeletePer(int position, PersionInfo persionInfo);

    /**
     * 点击“保存”的时候调用
     */
    void onSaveChange(int position, PersionInfo persionInfo);

    void showWaitDialog(String content);
    void dissmissDialog();
    void onSuccess(int type, int position, ContentValues values);
    void onFailed(String message);
}

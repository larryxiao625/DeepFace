package com.iustu.identification.ui.main.library.peoplemagnage.mvp;

import android.content.ContentValues;

import com.iustu.identification.entity.PersionInfo;
import com.iustu.identification.entity.PersonInfo;

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
    void onAddPhoto(int index);

    /**
     * 点击“删除照片”的时候调用
     * @param index 表示代表第几个PersionInfo需要删除图片
     * @param position 表示删除的是其第几个图片
     * @param persionInfo 表示需要删除图片的PersionInfo
     */
    void onDeletePhoto(int index, int position, PersionInfo persionInfo);

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

    /**
     * 当数据库操作成功时调用
     * @param type 本次操作的类型
     * @param position 本次操作的数据位于List的位置，如果是删除人员的话，该参数没有意义
     * @param values 本次操作的数据的变动结果，如果是删除人员的话，该参数没有意义
     */
    void onSuccess(int type, int position, ContentValues values);
    void onFailed(String message);
}

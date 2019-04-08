package com.iustu.identification.ui.main.library.peoplemagnage.mvp;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.iustu.identification.entity.PersionInfo;
import com.iustu.identification.util.RxUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * created by sgh, 2019-4-3
 * 人脸库详情管理的Presenter
 */
public class PersionPresenter {
    private Disposable disposable;

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
    public void onInitData(int libId) {
        mView.showWaitDialog("正在加载数据...");
        Observable observable = RxUtil.getQuaryObservalbe(false, RxUtil.DB_PERSIONINFO, RxUtil.PERSIONINFO_COLUMNS, "libId = " + libId, null, null, null, null, null);
        observable.subscribe(new Observer<Cursor>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Cursor cursor) {
                List<PersionInfo> data = new ArrayList<>();
                while (cursor.moveToNext()) {
                    PersionInfo persionInfo = new PersionInfo();
                    persionInfo.feature = null;
                    persionInfo.libId = cursor.getInt(1);
                    persionInfo.name = cursor.getString(2);
                    persionInfo.gender = cursor.getString(3);
                    persionInfo.photoPath = cursor.getString(4);
                    persionInfo.identity = cursor.getString(5);
                    persionInfo.home = cursor.getString(6);
                    persionInfo.other = cursor.getString(7);
                    data.add(persionInfo);
                }
                mView.bindData(data);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
                mView.dissmissDialog();
                disposable = null;
            }
        });
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
    public void onDeletePer(PersionInfo persionInfo) {
        mView.showWaitDialog("正在删除...");
        ContentValues values = new ContentValues();
        values.put("libId", persionInfo.libId);
        values.put("name", persionInfo.name);
        Observable observable = RxUtil.getDeleteObservable(RxUtil.DB_PERSIONINFO, values);
        observable.subscribe(new Observer() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
                mView.dissmissDialog();
                disposable = null;
            }
        });
    }

    /**
     * 点击“保存”的时候调用
     */
    public void onSaveChange(PersionInfo persionInfo) {
        mView.showWaitDialog("正在修改...");
        ContentValues values = new ContentValues();
        values.put("feature", System.currentTimeMillis() + "");
        values.put("libId", persionInfo.libId);
        values.put("name", persionInfo.name);
        values.put("gender", persionInfo.gender);
        values.put("photoPath", persionInfo.photoPath);
        values.put("identity", persionInfo.identity);
        values.put("home", persionInfo.home);
        values.put("other", persionInfo.other);
        Observable observable = RxUtil.getUpdateObservable(RxUtil.DB_PERSIONINFO, "name = '" + persionInfo.name + "' and libId = " + persionInfo.libId, values);
        observable.subscribe(new Observer() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
                mView.dissmissDialog();
                disposable = null;
            }
        });
    }
}

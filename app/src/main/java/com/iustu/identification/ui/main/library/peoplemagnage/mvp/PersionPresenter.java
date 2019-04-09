package com.iustu.identification.ui.main.library.peoplemagnage.mvp;

import android.content.ContentValues;
import android.database.Cursor;

import com.iustu.identification.entity.PersonInfo;
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
                List<PersonInfo> data = new ArrayList<>();
                while (cursor.moveToNext()) {
                    PersonInfo personInfo = new PersonInfo();
                    personInfo.feature = null;
                    personInfo.libId = cursor.getInt(1);
                    personInfo.name = cursor.getString(2);
                    personInfo.gender = cursor.getString(3);
                    personInfo.photoPath = cursor.getString(4);
                    personInfo.identity = cursor.getString(5);
                    personInfo.home = cursor.getString(6);
                    personInfo.other = cursor.getString(7);
                    data.add(personInfo);
                }
                mView.bindData(data);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                mView.onFailed(e.getMessage());
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
     * @param personInfo 表示需要添加图片的对象
     * @param path 表项添加的图片路径
     * @param position 表示其所在的位置
     */
    public void onAddPhoto(PersonInfo personInfo, String path, int position) {
        mView.showWaitDialog("正在添加图片...");
        ContentValues values = new ContentValues();
        values.put("feature", System.currentTimeMillis() + "");
        values.put("libId", personInfo.libId);
        values.put("name", personInfo.name);
        values.put("gender", personInfo.gender);
        values.put("photoPath", personInfo.photoPath + ";" + path);
        values.put("identity", personInfo.identity);
        values.put("home", personInfo.home);
        values.put("other", personInfo.other);
        Observable observable = RxUtil.getUpdateObservable(RxUtil.DB_PERSIONINFO, "libId = " + personInfo.libId + " and name = '" + personInfo.name +"'", values);
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
                mView.onFailed(e.getMessage());
            }

            @Override
            public void onComplete() {
                disposable.dispose();
                mView.dissmissDialog();
                disposable = null;
                mView.onSuccess(PersionView.TYPE_ADD_PHOTO, position, values);
            }
        });
    }

    /**
     * 点击“删除照片”的时候调用
     * @param position 代表需要删除第几张图片
     * @param personInfo 代表需要删除的PersionInfo对象
     */
    public void onDeletePhoto(int position, PersonInfo personInfo) {
        mView.showWaitDialog("正在删除图片...");
        ContentValues values = new ContentValues();
        values.put("feature", System.currentTimeMillis() + "");
        values.put("libId", personInfo.libId);
        values.put("name", personInfo.name);
        values.put("gender", personInfo.gender);
        String[] s = personInfo.photoPath.split(";");
        String finalPath = null;
        for (int i = 0; i < s.length; i ++) {
            if (i != position) {
                finalPath = finalPath == null ? s[i] : (finalPath + ";" + s[i]);
            } else {
                continue;
            }
        }
        values.put("photoPath", finalPath);
        values.put("identity", personInfo.identity);
        values.put("home", personInfo.home);
        values.put("other", personInfo.other);
        Observable observable = RxUtil.getUpdateObservable(RxUtil.DB_PERSIONINFO, "name = '" + personInfo.name + "' and libId = " + personInfo.libId, values);
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
                mView.onFailed(e.getMessage());
            }

            @Override
            public void onComplete() {
                disposable.dispose();
                mView.dissmissDialog();
                disposable = null;
                mView.onSuccess(PersionView.TYPE_DELETE_PHOTO, position, values);
            }
        });
    }

    /**
     * 点击“删除”的时候调用
     */
    public void onDeletePer(int position, PersonInfo personInfo) {
        mView.showWaitDialog("正在删除...");
        ContentValues values = new ContentValues();
        values.put("libId", personInfo.libId);
        values.put("name", personInfo.name);
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
                mView.onFailed(e.getMessage());
            }

            @Override
            public void onComplete() {
                disposable.dispose();
                mView.dissmissDialog();
                disposable = null;
                mView.onSuccess(PersionView.TYPE_DELETE_PER, position, null);
            }
        });
    }

    /**
     * 点击“保存”的时候调用
     */
    public void onSaveChange(int position, PersonInfo personInfo) {
        mView.showWaitDialog("正在修改...");
        ContentValues values = new ContentValues();
        values.put("feature", System.currentTimeMillis() + "");
        values.put("libId", personInfo.libId);
        values.put("name", personInfo.name);
        values.put("gender", personInfo.gender);
        values.put("photoPath", personInfo.photoPath);
        values.put("identity", personInfo.identity);
        values.put("home", personInfo.home);
        values.put("other", personInfo.other);
        Observable observable = RxUtil.getUpdateObservable(RxUtil.DB_PERSIONINFO, "name = '" + personInfo.name + "' and libId = " + personInfo.libId, values);
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
                mView.onFailed(e.getMessage());
            }

            @Override
            public void onComplete() {
                disposable.dispose();
                mView.dissmissDialog();
                disposable = null;
                mView.onSuccess(PersionView.TYPE_SAVE_CHANGE, position, values);
            }
        });
    }
}

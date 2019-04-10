package com.iustu.identification.util;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.agin.facerecsdk.SearchResultItem;
import com.iustu.identification.bean.FaceCollectItem;
import com.iustu.identification.entity.Account;
import com.iustu.identification.entity.CompareRecord;
import com.iustu.identification.entity.Library;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * created by sgh, 2019-4-4
 * 操作Sqlite数据库的工具类
 * 只包含增删改，查的功能在RxUtil中
 */
public class SqliteUtil {
    private static SQLiteDatabase database;

    // 初始化操作，在Application的onCreate中调用
    public static void init() throws Exception {
        database = SqliteHelper.getInstance().getWritableDatabase();
    }

    public static SQLiteDatabase getDatabase() {
        if (database == null) {
            synchronized (SQLiteDatabase.class) {
                if (database == null)
                    database = SqliteHelper.getInstance().getWritableDatabase();
            }
        }
        return database;
    }

    /**
     * 抓拍记录的插入操作
     * @param imgPath 图片的路径
     * @param time 时间戳
     */
    public static void insertFaceCollectionItem(String imgPath, String time){
        FaceCollectItem item = new FaceCollectItem();
        item.setTime(time);
        item.setImgUrl(imgPath);
        Observable observable = RxUtil.getInsertFaceCollectionItem(item);
        observable.subscribe(new Observer() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {
                Log.d("Camera","onError");
                e.printStackTrace();
                ToastUtil.show(e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d("Camera","onComplete");
                ToastUtil.show("成功");
            }
        });
    }

    /**
     * 插入比对结果的方法
     * @param resultItem 对比结果,由SDK生成
     * @param time 时间戳
     * @param uploadPhoto 抓拍生成的图片的路径
     * @param
     */
    public static void insertComparedItem(SearchResultItem resultItem, String time, String uploadPhoto) {
        CompareRecord compareRecord = new CompareRecord();
        compareRecord.setRate(resultItem.score);
        compareRecord.setTime(time);
        compareRecord.setUploadPhoto(uploadPhoto);
        Observable observable = RxUtil.getInsertCompareRecordObservable(compareRecord);
        observable.subscribe(new Observer() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {
                Log.d("Camera","onError");
                e.printStackTrace();
                ToastUtil.show(e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d("Camera","onComplete");
                ToastUtil.show("成功");
            }
        });
    }

}

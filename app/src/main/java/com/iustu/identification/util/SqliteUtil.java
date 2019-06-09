package com.iustu.identification.util;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.agin.facerecsdk.SearchResultItem;
import com.iustu.identification.bean.FaceCollectItem;
import com.iustu.identification.entity.Account;
import com.iustu.identification.entity.CompareRecord;
import com.iustu.identification.entity.Library;
import com.iustu.identification.ui.main.camera.prenster.IPenster;

import java.io.File;
import java.util.Date;

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
     * 根据人脸库名称生成创建人脸库的语句
     * @param libName 需要创建的人脸库
     * @return sqlitedatabase用来执行的sql语句
     */
    public static String generateCreateTableString(String libName) {
        return String.format("create table %s (id integer primary key autoincrement, feature varchar, libName varchar, image_id varchar, name varchar, gender varchar not null, photoPath varchar not null, identity varchar, home varchar, other varchar, birthday varchar)", libName);
    }

    /**
     * 修改人脸库的名字
     * @param libName 人脸库的名字
     * @param newName 新的名字
     * @return sqlitedatabase用来执行的sql语句
     */
    public static String generateAlterTableString(String libName, String newName) {
        return String.format("alter table %s rename to %s", libName, newName);
    }

    /**
     * 根据人脸库名称删除数据表
     * @param libName 需要删除的人脸库
     * @return sqlitedatabase用来执行的sql语句
     */
    public static String generateDropTableString(String libName) {
        return String.format("drop table %s", libName);
    }

    /**
     * 抓拍记录的插入操作
     * @param imgPath 图片的路径(裁剪后的图片)
     * @param originalPhoto 未裁剪的图片的路径
     * @param time 代表时间
     */
    public static void insertFaceCollectionItem(String imgPath, String originalPhoto, Date time,IPenster comparePrenster,int isUpload){
        FaceCollectItem item = new FaceCollectItem();
        item.setTime(TextUtil.getDateString2(time));
        item.setHourTime(TextUtil.getDateString(TextUtil.FORMAT_MILLISECOND,time));
        item.setImgUrl(imgPath);
        item.setOriginalPhoto(originalPhoto);
        item.setIsUpload(isUpload);
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
                e.printStackTrace();
                ToastUtil.show(e.getMessage());
            }

            @Override
            public void onComplete() {
                comparePrenster.getView().updateCapture(imgPath);
            }
        });
    }

    /**
     * 插入比对结果的方法
     * @param libName 目标库
     * @param resultItem 对比结果,由SDK生成
     * @param time 日期
     * @param uploadPhoto 抓拍生成的图片的路径（裁剪后的）
     * @param comparePresenter
     * @param originalPhoto 抓拍生成的原图
     */
    public static void insertComparedItem(String libName, SearchResultItem resultItem, Date time, String uploadPhoto, IPenster comparePresenter, String originalPhoto) {
        CompareRecord compareRecord = new CompareRecord();
        compareRecord.setRate(resultItem.score);
        compareRecord.setTime(TextUtil.getDateString2(time));
        compareRecord.setHourTime(TextUtil.getDateString(TextUtil.FORMAT_MILLISECOND,time));
        File file = new File(uploadPhoto);
        String uploadPath = String.format("/sdcard/DeepFace/CompareCut/%s", file.getName());
        file = new File(originalPhoto);
        String originalPath = String.format("/sdcard/DeepFace/CompareOriginal/%s", file.getName());
        compareRecord.setUploadPhoto(uploadPath);
        compareRecord.setImage_id(resultItem.image_id);
        compareRecord.setOriginalPhoto(originalPath);

        // 将两张图片复制到CompareXX文件夹下
        FileUtil.copy(uploadPhoto, uploadPath);
        FileUtil.copy(originalPhoto, originalPath);
        Observable observable = RxUtil.getInsertCompareRecordObservable(libName, compareRecord);
        observable.subscribe(new Observer() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                ToastUtil.show(e.getMessage());
            }

            @Override
            public void onComplete() {
                comparePresenter.getView().updateSingleResult(compareRecord);
            }
        });
    }

    /**
     * 系统管理界面中用来更改密码的方法
     * @param accountName 账户名
     * @param accountPassword 新密码
     */
    public static void modifyAccountPassword (String accountName, String accountPassword) {
        Account account = new Account();
        account.name = accountName;
        account.password = accountPassword;
        Observable observable = RxUtil.getModifyPassword(account);
        observable.subscribe(new Observer() {
            @Override
            public void onSubscribe(Disposable d) {

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
                if (accountName.equals("admin"))
                    DataCache.getAdmin().setPassword(accountPassword);
            }
        });
    }

    /**
     * 用来提交所有人脸库使用状态
     */
    public static void updataLibrariedInUsed() {
        Observable observable = RxUtil.updataLibraries();
        observable.subscribe(new Observer() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}

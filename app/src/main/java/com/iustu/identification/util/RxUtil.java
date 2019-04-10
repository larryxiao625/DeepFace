package com.iustu.identification.util;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import com.iustu.identification.entity.Library;
import com.iustu.identification.entity.PersionInfo;

import java.io.File;
import java.util.HashSet;
import java.util.function.Consumer;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * created by sgh, 2019-4-6
 *
 * 用来生成Observable对象的工具类
 */
public class RxUtil {
    public static final String DB_ACCOUNT = "Account";          // 对应Account数据表
    public static final String DB_LIBRARY = "Library";          // 对应Library数据表
    public static final String DB_PERSIONINFO = "PersonInfo";   // 对应PersionInfo数据表
    public static final String DB_TAKERECORD = "TakeRecord";     // 对应TakeRecord数据表
    public static final String DB_COMPARERECORD = "CompareRecord";  // 对应CompareRecord数据表

    public static final String[] ACCOUNT_COLUMNS = new String[]{"name", "password"};    // Account的所有列

    public static final String[] LIBRARY_COLUMNS = new String[]{"libName", "libId", "description", "count", "inUsed"}; // Library的所有列
    public static final String[] PERSIONINFO_COLUMNS = new String[]{"feature", "libId", "name", "gender", "photoPath", "identity", "home", "other", "image_id", "libName"};


    public static final Integer VERIFY_SUCCESS=1;
    public static final Integer VERIFY_FAIL=0;


    // 获取查询数据库时的游标
    public static Observable<Cursor> getQuaryObservalbe(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return io.reactivex.Observable.create(new ObservableOnSubscribe<Cursor>() {
            @Override
            public void subscribe(ObservableEmitter e) {
                SQLiteDatabase database = SqliteUtil.getDatabase();
                Cursor cursor = database.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
                e.onNext(cursor);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // 批量更改人脸库的选中状态
    public static Observable updataLibraries(HashSet<Integer> libIds) {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void subscribe(ObservableEmitter<Object> e) {
                SQLiteDatabase database = SqliteUtil.getDatabase();
                database.beginTransaction();
                try {
                    database.beginTransaction();
                    libIds.forEach(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer integer) {
                            ContentValues values1 = new ContentValues();
                            values1.put("inUsed", 1);
                            database.update("Library", values1, "libId = " + (int)integer, null);
                        }
                    });
                    database.setTransactionSuccessful();
                    e.onComplete();
                }finally {
                    database.endTransaction();
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 创建人脸库时候调用
     * @param library 需要创建的人脸库的信息
     * @return Observable对象
     */
    public static Observable getAddLibraryObservable(Library library) {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) {
                // 首先执行数据库相应数据的添加
                SQLiteDatabase database = SqliteUtil.getDatabase();
                ContentValues values = library.toContentValues();
                database.beginTransaction();
                try {
                    database.insert(RxUtil.DB_LIBRARY, null, values);
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
                // 然后执行的是创建人脸库的路径
                int libId = library.libId;
                String finalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DeepFace/" + library.libId;
                File file = new File(finalPath);
                if (!file.exists())
                    file.mkdir();

                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 删除人脸库的时候调用
     * @param library 需要删除的人脸库
     * @return Observable对象
     */
    public static Observable getDeleteLibraryObservable(Library library) {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) {
                // 首先要删掉数据库中的
                SQLiteDatabase database = SqliteUtil.getDatabase();
                database.beginTransaction();
                try {
                    database.delete(RxUtil.DB_LIBRARY, "libId = " + library.libId, null);
                    database.delete(RxUtil.DB_PERSIONINFO, "libId = " + library.libId, null);
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
                // 删掉对相应的文件夹
                String finalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DeepFace/" + library.libId;
                FileUtil.delete(finalPath);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 修改人脸库的时候调用
     * @param old 需要修改的数据
     * @param newLib 新的人脸库信息
     * @return Observable对象
     */
    public static Observable getModifyLibraryObservable(Library old, Library newLib) {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) {
                // 首先删除数据库中的
                ContentValues values = newLib.toContentValues();
                SQLiteDatabase database = SqliteUtil.getDatabase();
                database.beginTransaction();
                try {
                    database.update(RxUtil.DB_LIBRARY, values, "libId = " + newLib.libId, null);
                    e.onComplete();
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }

                // 修改文件夹的名称
                //String finalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DeepFace/" + old.libName;
                //String newPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DeepFace/" + newLib.libName;
                //FileUtil.modify(finalPath, newPath);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 往人脸库中添加人员的时候调用
     * @param persionInfo 添加的人员的信息
     * @return Observable对象
     */
    public static Observable getAddPersionObservable(PersionInfo persionInfo) {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) {
                // 首先调用SDK生成feature
                String image_id = System.currentTimeMillis() + "";
                persionInfo.image_id = image_id;
                SDKUtil.sdkDoPerson(persionInfo);

                // 其次将选中的图片复制到人脸库的路径中
                String fileName = persionInfo.name + System.currentTimeMillis() + ".jpg";
                String finalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DeepFace/" + persionInfo.libId + "/" + fileName;
                FileUtil.copy(persionInfo.photoPath, finalPath);

                // 往数据库中添加信息
                persionInfo.photoPath = fileName;
                ContentValues values = persionInfo.toContentValues();
                SQLiteDatabase database = SqliteUtil.getDatabase();
                database.beginTransaction();
                try {
                    database.insert(RxUtil.DB_PERSIONINFO, null, values);
                    // 如果往PersionInfo中添加新数据，则需要修改Library的信息
                    Cursor cursor = database.query(false, RxUtil.DB_LIBRARY, RxUtil.LIBRARY_COLUMNS,"libId = " + persionInfo.libId, null, null, null, null, null);
                    cursor.moveToFirst();
                    Log.e("", "subscribe: libid is ============"+cursor.getCount());
                    int count = cursor.getInt(cursor.getColumnIndex("count"));
                    ContentValues values1 = new ContentValues();
                    values1.put("count", count + 1);
                    database.update(RxUtil.DB_LIBRARY, values1, "libId = " + persionInfo.libId, null);
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }

                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 人员添加照片的时候调用
     * @param persionInfo 需要添加照片的人员（未添加）
     * @param newPhoto 添加的新照片
     * @return Observable对象
     */
    public static Observable getAddPhotoObservable(PersionInfo persionInfo, String newPhoto) {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) {
                // 进行文件拷贝
                String fileName = persionInfo.name + System.currentTimeMillis() + ".jpg";
                String finalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DeepFace/" + persionInfo.libId + "/" + fileName;
                FileUtil.copy(newPhoto, finalPath);

                // 执行数据库操作
                persionInfo.photoPath = persionInfo.photoPath + ";" + fileName;
                SQLiteDatabase database = SqliteUtil.getDatabase();
                database.beginTransaction();
                try {
                    database.update(RxUtil.DB_PERSIONINFO, persionInfo.toContentValues(), "libId = " + persionInfo.libId + " and image_id = '" + persionInfo.image_id + "'", null);
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 修改人员信息的方法
     * @param persionInfo 修改后的信息
     * @return Observable方法
     */
    public static Observable getSavePersonChangeObservable(PersionInfo persionInfo) {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) {
                SQLiteDatabase database = SqliteUtil.getDatabase();
                database.beginTransaction();
                try {
                    Log.e("", "subscribe: ==================正在保存数据");
                    database.update(RxUtil.DB_PERSIONINFO, persionInfo.toContentValues(), "libId = " + persionInfo.libId + " and image_id = '" + persionInfo.image_id + "'", null);
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 删除图片时调用
     * @param persionInfo 需要删除图片的人
     * @param path 需要删除的照片的路径
     * @return Observable 对象
     */
    public static Observable getDeletePhotoObservable(PersionInfo persionInfo, String path) {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) {
                SQLiteDatabase database = SqliteUtil.getDatabase();
                database.beginTransaction();
                try {
                    database.update(RxUtil.DB_PERSIONINFO, persionInfo.toContentValues(), "libId = " + persionInfo.libId + " and image_id = '" + persionInfo.image_id + "'", null);
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
                // 其次进行文件删除
                //String finalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DeepFace/" + persionInfo.libName + "/" + persionInfo.name + System.currentTimeMillis() + ".jpg";
                FileUtil.delete(path);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 删除人员信息时调用
     * @param persionInfo 需要删除的人员
     * @return Observable对象
     */
    public static Observable getDeletePersonObservable(PersionInfo persionInfo) {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) {
                // 首先执行数据库操作
                SQLiteDatabase database = SqliteUtil.getDatabase();
                database.beginTransaction();
                try {
                    int libId = persionInfo.libId;
                    String name = persionInfo.name;
                    database.delete(RxUtil.DB_PERSIONINFO, "libId = "+libId + " and name = '"+name+"'", null);
                    Cursor cursor = database.query(false, RxUtil.DB_LIBRARY, LIBRARY_COLUMNS,"libId = "+libId, null, null, null, null, null);
                    cursor.moveToNext();
                    int count = cursor.getInt(cursor.getColumnIndex("count"));
                    ContentValues values1 = new ContentValues();
                    values1.put("count", count - 1);
                    database.update(RxUtil.DB_LIBRARY, values1, "libId = " + libId, null);
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }

                // 执行删除图片
                // (等待补全)
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}

package com.iustu.identification.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.iustu.identification.entity.Library;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * created by sgh, 2019-4-6
 *
 * 用来生成Observable对象的工具类
 */
public class RxUtil {
    public static final String DB_ACCOUNT = "Account";          // 对应Account数据表
    public static final String DB_LIBRARY = "Library";          // 对应Library数据表
    public static final String DB_PERSIONINFO = "PersionInfo";   // 对应PersionInfo数据表
    public static final String DB_TAKERECORD = "TakeRecord";     // 对应TakeRecord数据表
    public static final String DB_COMPARERECORD = "CompareRecord";  // 对应CompareRecord数据表

    public static final String[] ACCOUNT_COLUMNS = new String[]{"name", "password"};    // Account的所有列
    public static final String[] LIBRARY_COLUMNS = new String[]{"libName", "libId", "description", "count"}; // Library的所有列
    public static final String[] PERSIONAL_COLUMNS = new String[]{"feature", "libId", "name", "gender", "nation", "photoPath", "identity", "home", "other"};


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

    // 获取插入数据库的Observable
    public static Observable getInsertObservable(String tableName, ContentValues values) {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) {
                int libId = -1;
                SQLiteDatabase database = SqliteUtil.getDatabase();
                database.insert(tableName, null, values);
                String t = null;

                // 如果往PersionInfo中添加新数据，则需要修改Library的信息
                if (tableName.equals(DB_PERSIONINFO)) {
                    libId = values.getAsInteger("libId");
                    t = DB_LIBRARY;
                    Cursor cursor = database.query(false, t, null,"libId = " + libId, null, null, null, null, null);
                    cursor.moveToNext();
                    int count = cursor.getInt(3);
                    ContentValues values1 = new ContentValues();
                    values1.put("count", count + 1);
                    database.update(t, values1, "libId = " + libId, null);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // 获取更新数据表中数据的Observable
    public static Observable getUpdateObservable(String tableName, String where, ContentValues contentValues) {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) {
                SQLiteDatabase database = SqliteUtil.getDatabase();
                database.update(tableName, contentValues, where, null);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // 获取删除数据表中数据的Observable
    public static Observable getDeleteObservable(String tableName, ContentValues values) {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) {
                String t = null;
                SQLiteDatabase database = SqliteUtil.getDatabase();
                // 删除人脸库的情况
                if (tableName.equals(DB_LIBRARY)) {
                    int libId = values.getAsInteger("libId");
                    database.delete(tableName, "libId = " + libId, null);
                    // 如果删除人脸库，则需要删除关联的PersionInfo
                    t = DB_PERSIONINFO;
                    database.delete(t, "libId = " + libId, null);
                } else if(tableName.equals(DB_PERSIONINFO)) {         // 删除PersionInfo的情况
                    int libId = values.getAsInteger("libId");
                    t = DB_LIBRARY;
                    Cursor cursor = database.query(false, t, null,"libId = " + libId, null, null, null, null, null);
                    cursor.moveToNext();
                    int count = cursor.getInt(3);
                    ContentValues values1 = new ContentValues();
                    values1.put("count", count - 1);
                    database.update(t, null, "libId = " + libId, null);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}

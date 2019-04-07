package com.iustu.identification.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
                SQLiteDatabase database = SqliteUtil.getDatabase();
                database.insert(tableName, null, values);
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
    public static Observable getDeleteObservable(String tableName, String where) {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) {
                SQLiteDatabase database = SqliteUtil.getDatabase();
                database.delete(tableName, where, null);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}

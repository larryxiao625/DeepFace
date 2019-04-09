package com.iustu.identification.util;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.iustu.identification.entity.Account;
import com.iustu.identification.entity.CompareRecord;
import com.iustu.identification.entity.Library;

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
}

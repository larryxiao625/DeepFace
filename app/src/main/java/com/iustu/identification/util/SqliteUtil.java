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

    public void insertAccount(Account account) {
        ContentValues values = new ContentValues();
        values.put("name", account.name);
        values.put("password", account.password);
        database.beginTransaction();
        database.insert(SQLString.TABLE_ACCOUNT, null, values);
        database.endTransaction();
    }

    public void insertLibrary(Library library) {
        ContentValues values = new ContentValues();
        values.put("libName", library.libName);
        values.put("description", library.discription);
        values.put("count", library.count);
        database.beginTransaction();
        database.insert(SQLString.TABLE_LIBRARY, null, values);
        database.endTransaction();
    }

    public void insertCompareRecord(CompareRecord compareRecord) {
        ContentValues values = new ContentValues();
        values.put("time", compareRecord.time);
        values.put("rate", compareRecord.rate);
        values.put("uploadPhotoPath", compareRecord.uploadPhotoPath);
        values.put("feature", compareRecord.feature.toString());
        database.beginTransaction();
        database.insert(SQLString.TABLE_COMPARERECORD, null, values);
        database.endTransaction();
    }
}

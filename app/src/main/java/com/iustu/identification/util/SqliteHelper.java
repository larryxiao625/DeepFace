package com.iustu.identification.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

/**
 * created by sgh, 2019-4-2
 *
 * SQLiteOpenHelper的实现类
 *
 * 在创建新的数据表的时候：一定要在开头声明数据表的名称
 */
public class SqliteHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "deepface.db";      // 该应用对应的数据库的名称
    private static int DB_VERSION = 1;      // 数据库对应的版本号
    private static SqliteHelper INSTANCE;

    // 继承自父类的构造器
    private SqliteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // 自定义的构造器
    private SqliteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public static SqliteHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SqliteHelper.class) {
                if (INSTANCE == null)
                    INSTANCE = new SqliteHelper(context);
            }
        }
        return INSTANCE;
    }
}

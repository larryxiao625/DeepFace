package com.iustu.identification.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.iustu.identification.entity.Account;
import com.iustu.identification.entity.CompareRecord;
import com.iustu.identification.entity.Library;
import com.iustu.identification.entity.PersonInfo;
import com.iustu.identification.entity.TakeRecord;

/**
 * created by sgh, 2019-4-2
 *
 * SQLiteOpenHelper的实现类
 * 作用就是获取数据库对象
 * 在创建新的数据表的时候：一定要在开头声明数据表的名称
 */
public class SqliteHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "deepface.db";      // 该应用对应的数据库的名称
    private static int DB_VERSION = 1;      // 数据库对应的版本号
    private static Context mContext;
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
        //sqLiteDatabase.beginTransaction();
        sqLiteDatabase.execSQL(SQLString.CREATE_TABLE_ACCOUNT);
        sqLiteDatabase.execSQL(SQLString.CREATE_TABLE_LIBRARY);
        sqLiteDatabase.execSQL(SQLString.CREATE_TABLE_COMPARERECORD);
        sqLiteDatabase.execSQL(SQLString.CREATE_TABLE_PERSIONINFO);
        sqLiteDatabase.execSQL(SQLString.CREATE_TABLE_FACECOLLECTIOMITEM);
        String insertUserAccount = "insert into " + SQLString.TABLE_ACCOUNT + " values(\"user\", \"123456\")";
        String insertAdminAccount = "insert into " + SQLString.TABLE_ACCOUNT + " values(\"admin\", \"123456\")";
        sqLiteDatabase.execSQL(insertAdminAccount);
        sqLiteDatabase.execSQL(insertUserAccount);
        //sqLiteDatabase.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    // 初始化操作，在Application的onCreate中调用
    public static void init(Context context) {
        mContext = context;
    }
    public static SqliteHelper getInstance() {
        if (INSTANCE == null) {
            synchronized (SqliteHelper.class) {
                if (INSTANCE == null)
                    INSTANCE = new SqliteHelper(mContext);
            }
        }
        return INSTANCE;
    }
}

/**
 * created by sgh，2019-4-4
 * 用来写SQL语句
 */
class SQLString {
    public static final String TABLE_ACCOUNT = Account.class.getSimpleName();

    public static final String CREATE_TABLE_LIBRARY = "create table Library (libName varchar primary key, description varchar, count integer not null, inUsed integer not null)";
    public static final String CREATE_TABLE_ACCOUNT = "create table Account (name varchar(6) primary key, password varchar(11) not null)";

    public static final String CREATE_TABLE_COMPARERECORD = "create table CompareRecord (hourTime varchar, time varchar not null, uploadPhoto varchar primary key, image_id varchar not null, rate float not null, libName varchar, name varchar, gender varchar, home varchar, identity varchar, birthday varchar, photoPath varchar, other varchar, originalPhoto varchar)";
    public static final String CREATE_TABLE_PERSIONINFO = "create table PersonInfo (id integer primary key autoincrement, feature varchar, libName varchar, image_id varchar, name varchar, gender varchar not null, photoPath varchar not null, identity varchar, home varchar, other varchar, birthday varchar)";
    public static final String CREATE_TABLE_FACECOLLECTIOMITEM = "create table FaceCollectionItem (hourTime varchar, originalPath varchar, time varchar, id integer primary key autoincrement, imgUrl varchar not null, faceId varchar)";

}

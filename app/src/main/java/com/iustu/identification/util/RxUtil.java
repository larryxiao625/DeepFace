package com.iustu.identification.util;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.RequiresApi;
import android.util.Log;

import com.iustu.identification.bean.FaceCollectItem;
import com.iustu.identification.entity.Account;
import com.iustu.identification.entity.CompareRecord;
import com.iustu.identification.entity.Library;
import com.iustu.identification.entity.PersionInfo;

import java.io.File;
import java.util.function.Consumer;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
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
    public static final String DB_PERSIONINFO = "PersionInfo";   // 对应PersionInfo数据表
    //public static final String DB_TAKERECORD = "TakeRecord";     // 对应TakeRecord数据表
    public static final String DB_COMPARERECORD = "CompareRecord";  // 对应CompareRecord数据表
    public static final String DB_FACECOLLECTIOMITEM = "FaceCollectionItem"; // 对应FaceCollectionItem数据表

    public static final String[] ACCOUNT_COLUMNS = new String[]{"name", "password"};    // Account的所有列
    public static final String[] FACECOLLECTION_COLUMNS = new String[]{"hourTime", "originalPath", "faceId", "imgUrl", "time", "id", "isUpload"};  //FaceCollectionItem的所有列

    public static final String[] LIBRARY_COLUMNS = new String[]{"libName", "description", "count", "inUsed"}; // Library的所有列
    public static final String[] PERSIONINFO_COLUMNS = new String[]{"feature", "name", "gender", "photoPath", "identity", "home", "other", "image_id", "libName","birthday"};
    public static final String[] COMPARE_COLUMNS = new String[]{"hourTime", "originalPhoto", "time", "uploadPhoto", "image_id", "rate", "libName", "name", "gender", "home", "identity", "photoPath", "other"};

    // 获取查询数据库时的游标
    public static Observable<Cursor> getQuaryObservalbe(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return io.reactivex.Observable.create((ObservableOnSubscribe<Cursor>) e -> {
            SQLiteDatabase database = SqliteUtil.getDatabase();
            database.beginTransaction();
            try {
                Cursor cursor = database.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
                e.onNext(cursor);
                e.onComplete();
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // 获取查询数据库时的游标
    public static Observable<Cursor> getLoginObservalbe(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return io.reactivex.Observable.create((ObservableOnSubscribe<Cursor>) e -> {
            SQLiteDatabase database = SqliteUtil.getDatabase();
            database.beginTransaction();
            try {
                // 首先获取admin管理员账户
                Cursor cursor1 = database.query(distinct, table, columns, "name = 'admin'", null, null, null, null, null);
                e.onNext(cursor1);
                Cursor cursor = database.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
                e.onNext(cursor);
                e.onComplete();
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // 批量更改人脸库的选中状态
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Observable updataLibraries() {
        return Observable.create(e -> {
            SQLiteDatabase database = SqliteUtil.getDatabase();
            database.beginTransaction();
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put("inUsed", 0);
                // 将以下Library置为未使用
                DataCache.getChangedLib().forEach(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        database.update(RxUtil.DB_LIBRARY, contentValues, "libName = '" + s + "'", null);
                    }
                });

                contentValues.put("inUsed", 1);
                // 将以下Library置为正在使用
                DataCache.getChosenLibConfig().forEach(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        database.update(RxUtil.DB_LIBRARY, contentValues, "libName = '" + s + "'", null);
                    }
                });
                database.setTransactionSuccessful();
                e.onComplete();
            }finally {
                database.endTransaction();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 将未上传人脸变更为上传完成
     */
    public static Observable updateFaceStatus(FaceCollectItem faceCollectItem){
        return Observable.create(emitter -> {
            SQLiteDatabase database=SqliteUtil.getDatabase();
            database.beginTransaction();
            try {
                ContentValues contentValues=new ContentValues();
                contentValues.put("isUpload",1);
                database.update(RxUtil.DB_FACECOLLECTIOMITEM,contentValues,"id " + "=?",new String[]{String.valueOf(faceCollectItem.getId())});
                database.setTransactionSuccessful();
                emitter.onComplete();
            } finally {
                database.endTransaction();
            }
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
    }
    /**
     * 创建人脸库时候调用
     * @param library 需要创建的人脸库的信息
     * @return Observable对象
     */
    public static Observable getAddLibraryObservable(Library library) {
        return Observable.create(e -> {
            // 首先执行数据库相应数据的添加
            SQLiteDatabase database = SqliteUtil.getDatabase();
            ContentValues values = library.toContentValues();
            database.beginTransaction();

            Cursor cursor = database.rawQuery("select * from Library where libName = ?", new String[]{library.libName});
            if (cursor.getCount() > 0) {
                e.onError(new Exception("该人脸库已存在"));
                cursor.close();
                return;
            }

            String createTable = SqliteUtil.generateCreateTableString(library.libName);
                // 执行建表语句
            try {
                database.execSQL(createTable);
            } catch (SQLException e3) {
                e3.printStackTrace();
            }
            database.insert(RxUtil.DB_LIBRARY, null, values);
            database.setTransactionSuccessful();
            database.endTransaction();
            // 然后执行的是创建人脸库的路径
            String libName = library.libName;
            String finalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DeepFace/" + library.libName;
            File file = new File(finalPath);
            if (!file.exists())
                file.mkdir();

            e.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 删除人脸库的时候调用
     * @param library 需要删除的人脸库
     * @return Observable对象
     */
    public static Observable getDeleteLibraryObservable(Library library) {
        return Observable.create(e -> {
            // 首先要删掉数据库中的
            SQLiteDatabase database = SqliteUtil.getDatabase();
            database.beginTransaction();

            String dropTable = SqliteUtil.generateDropTableString(library.libName);
            database.execSQL(dropTable);
            database.delete(RxUtil.DB_LIBRARY, "libName = '" + library.libName + "'", null);
            database.setTransactionSuccessful();
            database.endTransaction();
            // 删掉对相应的文件夹
            String finalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DeepFace/" + library.libName;
            FileUtil.delete(finalPath);
            e.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 修改人脸库的时候调用
     * @param old 需要修改的数据
     * @param newLib 新的人脸库信息
     * @return Observable对象
     */
    public static Observable getModifyLibraryObservable(Library old, Library newLib) {
        return Observable.create(e -> {
            // 首先删除数据库中的
            String alterTable = SqliteUtil.generateAlterTableString(old.libName, newLib.libName);
            ContentValues values = newLib.toContentValues();
            SQLiteDatabase database = SqliteUtil.getDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery("select * from Library where libName = ?", new String[]{newLib.libName});
            if (cursor.getCount() > 0) {
                e.onError(new Exception("该人脸库已存在"));
                cursor.close();
                return;
            }
            // 首先修改表名称
            database.execSQL(alterTable);

            database.update(RxUtil.DB_LIBRARY, values, "libName = '" + old.libName + "'", null);
            // 将old对应的人脸库中所有数据的libName字段改成newLib的libName
            ContentValues v = new ContentValues();
            v.put("libName", newLib.libName);
            database.update(newLib.libName, v, null, null);
            e.onComplete();
            database.setTransactionSuccessful();
            database.endTransaction();

            // 修改文件夹的名称
            String finalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DeepFace/" + old.libName;
            String newPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DeepFace/" + newLib.libName;
            FileUtil.modify(finalPath, newPath);
            e.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 往人脸库中添加人员的时候调用
     * @param persionInfo 添加的人员的信息
     * @return Observable对象
     */
    public static Observable<Integer> getAddPersionObservable(PersionInfo persionInfo) {
        return Observable.create((ObservableOnSubscribe<Integer>) e -> {
            // 首先调用SDK生成feature
            String image_id = System.currentTimeMillis() + "";
            persionInfo.image_id = image_id;
            int result = SDKUtil.sdkDoPerson(persionInfo);

            // 其次将选中的图片复制到人脸库的路径中
            String fileName = persionInfo.name + System.currentTimeMillis() + ".jpg";
            String finalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DeepFace/" + persionInfo.libName + "/" + fileName;
            FileUtil.copy(persionInfo.photoPath, finalPath);

            // 往数据库中添加信息
            persionInfo.photoPath = fileName;
            ContentValues values = persionInfo.toContentValues();
            SQLiteDatabase database = SqliteUtil.getDatabase();
            database.beginTransaction();
            try {
                if(result != -1) {
                    database.insert(persionInfo.libName, null, values);
                    // 如果往PersionInfo中添加新数据，则需要修改Library的信息
                    Cursor cursor = database.query(false, RxUtil.DB_LIBRARY, RxUtil.LIBRARY_COLUMNS,"libName = '" + persionInfo.libName + "'", null, null, null, null, null);
                    cursor.moveToFirst();
                    int count = cursor.getInt(cursor.getColumnIndex("count"));
                    ContentValues values1 = new ContentValues();
                    values1.put("count", count + 1);
                    database.update(RxUtil.DB_LIBRARY, values1, "libName = '" + persionInfo.libName + "'", null);
                }
                e.onNext(result);
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
            e.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 批量导入图片的时候调用
     * @param persionInfos 需要被导入的人员
     * @return Observable对象
     */
    public static Observable<BatchReturn> getImportBatchPersionObservable(ArrayList<PersionInfo> persionInfos) {
        final int[] index = {0};
        return Observable.fromIterable(persionInfos).map(persionInfo -> {
            index[0]++;
            // 其次将选中的图片复制到人脸库的路径中
            String fileName = persionInfo.name + System.currentTimeMillis() + ".jpg";
            String finalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DeepFace/" + persionInfo.libName + "/" + fileName;
            FileUtil.copy(persionInfo.photoPath, finalPath);
            String compressPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DeepFace/temp/" + fileName;
            FileUtil.copyCompressedBitmap(persionInfo.photoPath, compressPath);
            persionInfo.photoPath = fileName;

            // 首先调用SDK生成feature
            boolean result = SDKUtil.sdkDoBatchPersion(persionInfo);
            // 说明添加失败，多半是图片有点糊
            if (!result)
                return new BatchReturn(index[0], false);
            SQLiteDatabase database = SqliteUtil.getDatabase();
            database.beginTransaction();
            try {
                long r = database.insertWithOnConflict(persionInfo.libName, null, persionInfo.toContentValues(), SQLiteDatabase.CONFLICT_IGNORE);
                // 如果往PersionInfo中添加新数据，则需要修改Library的信息
                Cursor cursor = database.query(false, RxUtil.DB_LIBRARY, RxUtil.LIBRARY_COLUMNS,"libName = '" + persionInfo.libName + "'", null, null, null, null, null);
                cursor.moveToFirst();
                int count = cursor.getInt(cursor.getColumnIndex("count"));
                ContentValues values1 = new ContentValues();
                values1.put("count", count + 1);
                database.update(RxUtil.DB_LIBRARY, values1, "libName = '" + persionInfo.libName + "'", null);
                database.setTransactionSuccessful();
                cursor.close();
            } finally{
                database.endTransaction();
            }
            return new BatchReturn(index[0], true);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 人员添加照片的时候调用
     * @param persionInfo 需要添加照片的人员（未添加）
     * @param newPhoto 添加的新照片的原路径
     * @param fileName 添加的新照片的名字
     * @return Observable对象
     */
    public static Observable getAddPhotoObservable(PersionInfo persionInfo, String newPhoto, String fileName) {
        return Observable.create(e -> {
            // 进行文件拷贝
            String finalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DeepFace/" + persionInfo.libName + "/" + fileName;
            FileUtil.copy(newPhoto, finalPath);

            // 执行数据库操作
            persionInfo.photoPath = persionInfo.photoPath + ";" + fileName;
            SQLiteDatabase database = SqliteUtil.getDatabase();
            database.beginTransaction();
            try {
                database.update(persionInfo.libName, persionInfo.toContentValues(), "image_id = '" + persionInfo.image_id + "'", null);
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
            e.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 修改人员信息的方法
     * @param persionInfo 修改后的信息
     * @return Observable方法
     */
    public static Observable getSavePersonChangeObservable(PersionInfo persionInfo) {
        return Observable.create(e -> {
            SQLiteDatabase database = SqliteUtil.getDatabase();
            database.beginTransaction();
            try {
                database.update(persionInfo.libName, persionInfo.toContentValues(), "image_id = '" + persionInfo.image_id + "'", null);
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
            e.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 删除图片时调用
     * @param persionInfo 需要删除图片的人
     * @param path 需要删除的照片的路径
     * @return Observable 对象
     */
    public static Observable getDeletePhotoObservable(PersionInfo persionInfo, String path) {
        return Observable.create(e -> {
            SQLiteDatabase database = SqliteUtil.getDatabase();
            database.beginTransaction();
            try {
                database.update(persionInfo.libName, persionInfo.toContentValues(), "image_id = '" + persionInfo.image_id + "'", null);
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
            // 其次进行文件删
            FileUtil.delete(path);
            e.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 删除人员信息时调用
     * @param persionInfo 需要删除的人员
     * @return Observable对象
     */
    public static Observable getDeletePersonObservable(PersionInfo persionInfo) {
        return Observable.create(e -> {
            // 首先执行数据库操作
            SQLiteDatabase database = SqliteUtil.getDatabase();
            database.beginTransaction();
            try {
                String name = persionInfo.name;
                Cursor c = database.query(false, persionInfo.libName, new String[]{"id"},"image_id = '" + persionInfo.image_id + "'", null, null, null, null, null);
                c.moveToNext();
                // 被删除的PersionInfo的id
                int index = c.getInt(c.getColumnIndex("id"));

                database.delete(persionInfo.libName, "image_id = '" + persionInfo.image_id + "'", null);
                Cursor cursor = database.query(false, RxUtil.DB_LIBRARY, LIBRARY_COLUMNS,"libName = '"+persionInfo.libName +"'", null, null, null, null, null);
                cursor.moveToNext();
                int count = cursor.getInt(cursor.getColumnIndex("count"));
                ContentValues values1 = new ContentValues();
                values1.put("count", count - 1);
                database.update(RxUtil.DB_LIBRARY, values1, "libName = '" + persionInfo.libName + "'", null);

                // 维护自增的id字段
                Cursor cc = database.query(false, persionInfo.libName, new String[]{"id"},"id > " + index, null, null, null, "id", null);
                while(cc.moveToNext()) {
                    ContentValues v = new ContentValues();
                    v.put("id", index);
                    database.update(persionInfo.libName, v, "id = " + (index + 1), null);
                    index++;
                }
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }

            int res = SDKUtil.deletePerFromLibrary(persionInfo);
            if (res == -1) {
                e.onError(new Exception("人脸库删除人脸特征失败"));
                return;
            }
            // 执行删除图片
            FileUtil.deletePersionPhotos(persionInfo);
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 插入抓拍记录的时候调用
     * @param item 需要插入的对象
     * @return Observable对象
     */
    public static Observable getInsertFaceCollectionItem(FaceCollectItem item) {
        return Observable.create(e -> {
            SQLiteDatabase database = SqliteUtil.getDatabase();
            database.beginTransaction();
            try {
                Cursor cursor = database.query(false, RxUtil.DB_FACECOLLECTIOMITEM, FACECOLLECTION_COLUMNS, "isUpload" + "=?", new String[]{"1"}, null, null, null, null, null);
                if (cursor.getCount() > DataCache.getParameterConfig().getSaveCount()) {
                    // 超过的数目
                    int overCount = cursor.getCount() - DataCache.getParameterConfig().getSaveCount();
                    int index = 0;
                    while(index < overCount) {
                        index ++;
                        cursor.moveToNext();
                        FileUtil.delete(cursor.getString(cursor.getColumnIndex("imgUrl")));
                        FileUtil.deleteWithCache(cursor.getString(cursor.getColumnIndex("originalPath")));
                        database.delete(RxUtil.DB_FACECOLLECTIOMITEM, "id = " + cursor.getInt(cursor.getColumnIndex("id")), null);
                    }

                }
                cursor.close();
                database.insert(RxUtil.DB_FACECOLLECTIOMITEM, null, item.toContentValues());
                database.setTransactionSuccessful();
            }finally {
                database.endTransaction();
            }
            e.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 插入比对记录的时候调用
     * @param libName 目标人脸库
     * @param compareRecord 需要插入的比对记录的对象
     * @return Observable对象
     */
    public static Observable getInsertCompareRecordObservable(String libName, CompareRecord compareRecord) {
        return Observable.create(e -> {
            SQLiteDatabase database = SqliteUtil.getDatabase();
            database.beginTransaction();
            try {
                Cursor cursor1 = database.query(false, RxUtil.DB_COMPARERECORD, COMPARE_COLUMNS, null, null, null, null, null, null, null);
                if (cursor1.getCount() > DataCache.getParameterConfig().getSaveCount()) {
                    int overCount = cursor1.getCount() - DataCache.getParameterConfig().getSaveCount();
                    int index = 0;
                    while(index < overCount) {
                        cursor1.moveToNext();
                        index ++;
                        FileUtil.deleteWithCache(cursor1.getString(cursor1.getColumnIndex("uploadPhoto")));
                        FileUtil.deleteWithCache(cursor1.getString(cursor1.getColumnIndex("originalPhoto")));
                        database.delete(RxUtil.DB_COMPARERECORD, "uploadPhoto = ?", new String[]{cursor1.getString(cursor1.getColumnIndex("uploadPhoto"))});
                    }
                }
                Cursor cursor = database.query(libName, RxUtil.PERSIONINFO_COLUMNS, "image_id = '" + compareRecord.getImage_id() + "'", null, null, null, null, null);
                while(cursor.moveToNext()) {
                    compareRecord.setGender(cursor.getString(cursor.getColumnIndex("gender")));
                    compareRecord.setHome(cursor.getString(cursor.getColumnIndex("home")));
                    compareRecord.setLibName(cursor.getString(cursor.getColumnIndex("libName")));
                    compareRecord.setIdentity(cursor.getString(cursor.getColumnIndex("identity")));
                    compareRecord.setOther(cursor.getString(cursor.getColumnIndex("other")));
                    compareRecord.setPhotoPath(cursor.getString(cursor.getColumnIndex("photoPath")));
                    compareRecord.setName(cursor.getString(cursor.getColumnIndex("name")));
                    compareRecord.setBirthday(cursor.getString(cursor.getColumnIndex("birthday")));
                    database.insert(RxUtil.DB_COMPARERECORD, null, compareRecord.toContentValues());
                }
                cursor.close();
                cursor1.close();
                database.setTransactionSuccessful();
            }finally {
                database.endTransaction();
            }
            e.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 修改账户密码的方法
     * @param account 含有新密码的账户
     * @return Observable对象
     */
    public static Observable getModifyPassword(Account account) {
        return Observable.create(e -> {
            SQLiteDatabase database = SqliteUtil.getDatabase();
            database.beginTransaction();
            try {
                database.update(RxUtil.DB_ACCOUNT, account.toContentValues(), "name = '" + account.name + "'", null);
                database.setTransactionSuccessful();
            }finally {
                database.endTransaction();
            }
            e.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 删除比对记录信息时调用
     * @param compareRecord 需要删除的记录
     * @return Observable对象
     */
    public static Observable getDeleteCompareObservable(CompareRecord compareRecord) {
        return Observable.create(e -> {
            // 首先执行数据库操作
            SQLiteDatabase database = SqliteUtil.getDatabase();
            database.beginTransaction();
            try {
                database.delete(RxUtil.DB_COMPARERECORD, "uploadPhoto = ?", new String[]{compareRecord.getUploadPhoto()});
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static class BatchReturn {
        public int index;
        public boolean isSuccessed;

        BatchReturn(int index, boolean isSuccessed) {
            this.index = index;
            this.isSuccessed = isSuccessed;
        }
    }
}





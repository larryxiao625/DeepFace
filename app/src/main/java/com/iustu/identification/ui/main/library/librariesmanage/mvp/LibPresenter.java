package com.iustu.identification.ui.main.library.librariesmanage.mvp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.iustu.identification.entity.Library;
import com.iustu.identification.ui.widget.dialog.WaitProgressDialog;
import com.iustu.identification.util.LibManager;
import com.iustu.identification.util.RxUtil;
import com.iustu.identification.util.SqliteHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * created by sgh, 2019-4-3
 * 人脸库管理界面的Presenter
 */
public class LibPresenter {
    private Disposable disposable;

    private LibModel mModel;
    private LibView mView;

    public LibPresenter(LibModel model) {
        this.mModel = model;
    }

    public void setView (LibView view) {
        this.mView = view;
    }

    /**
     * 初始加载界面的时候获取所有的人脸库
     */
    public void onInitData() {
        mView.showWaitDialog("正在获取数据...");
        Observable observable = RxUtil.getQuaryObservalbe(false, RxUtil.DB_LIBRARY, RxUtil.LIBRARY_COLUMNS, null, null, null, null, null, null);
        observable.subscribe(new Observer<Cursor>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Cursor cursor) {
                if (cursor.getCount() == 0)
                    return;
                List<Library> data = new ArrayList<>();
                Log.e("LibManagerPresenter", "onNext: ==============" + cursor.getColumnNames().toString());
                while (cursor.moveToNext()) {
                    Library library = new Library(cursor.getString(cursor.getColumnIndex("libName")), cursor.getInt(cursor.getColumnIndex("libId")), cursor.getString(cursor.getColumnIndex("description")), cursor.getInt(cursor.getColumnIndex("count")), cursor.getInt(cursor.getColumnIndex("inUsed")));
                    data.add(library);
                }
                mView.bindData(data);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                disposable.dispose();
                mView.dissmissDialog();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
                mView.dissmissDialog();
                disposable = null;
            }
        });
    }

    /**
     * 点击“下一页”的时候加载更多人脸库
     */
    public void onLoadMore() {
    }

    /**
     * 添加新人脸库的逻辑代码
     */
    public void onCreateNewLib(String name, String description) {
        mView.showWaitDialog("正在添加人脸库...");
        Library library = new Library(name, description, 0, 0);
        ContentValues contentValues = new ContentValues();
        contentValues.put("libName", library.libName);
        contentValues.put("description", library.description);
        contentValues.put("count", library.count);
        contentValues.put("inUsed", library.inUsed);
        Observable observable = RxUtil.getInsertObservable(RxUtil.DB_LIBRARY, contentValues);
        observable.subscribe(new Observer() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                // 说明操作失败
                mView.onError(e.getMessage());
                mView.dissmissDialog();
            }

            @Override
            public void onComplete() {
                List<Library> list = new ArrayList();
                list.add(library);
                mView.bindData(list);
                disposable.dispose();
                mView.dissmissDialog();
                mView.onSuccess(LibView.TYPE_ADD_LIB, 0, null);
            }
        });
    }

    /**
     * 删除人脸库的逻辑代码
     */
    public void onDeleteLib(int id, int position) {
        mView.showWaitDialog("正在删除人脸库...");
        ContentValues values = new ContentValues();
        values.put("libId", id);
        Observable<Cursor> observable = RxUtil.getDeleteObservable(RxUtil.DB_LIBRARY, values);
        observable.subscribe(new Observer<Cursor>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Cursor o) {
                // 通过游标删除数据库关联的PersionInfo
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                // 说明操作失败
                mView.onError(e.getMessage());
                mView.dissmissDialog();
            }

            @Override
            public void onComplete() {
                mView.onSuccess(LibView.TYPE_DELETE_LIB, position, null);
                disposable.dispose();
                mView.dissmissDialog();
            }
        });
    }

    /**
     * 更改人脸库信息的逻辑代码
     */
    public void onModifyLib(String name, String des, int libId, int position) {
        mView.showWaitDialog("正在更改人脸库名称...");
        ContentValues values = new ContentValues();
        values.put("libName", name);
        values.put("description", des);
        Observable observable = RxUtil.getUpdateObservable(RxUtil.DB_LIBRARY, "libId = " + libId, values);
        observable.subscribe(new Observer() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                mView.dissmissDialog();
                mView.onError(e.getMessage());
            }

            @Override
            public void onComplete() {
                ContentValues values1 = new ContentValues();
                values1.put("libName", name);
                values1.put("description", des);
                mView.onSuccess(LibView.TYPE_MODIFY_LIB, position, values1);
                disposable.dispose();
                mView.dissmissDialog();
            }
        });
    }
}

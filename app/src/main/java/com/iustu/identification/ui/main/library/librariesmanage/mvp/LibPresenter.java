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
                while (cursor.moveToNext()) {
                    Library library = new Library();
                    library.libName = cursor.getString(cursor.getColumnIndex("libName"));
                    library.count = cursor.getInt(cursor.getColumnIndex("count"));
                    library.inUsed = cursor.getInt(cursor.getColumnIndex("inUsed"));
                    library.description = cursor.getString(cursor.getColumnIndex("description"));
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
        Library library = new Library();
        library.libName = name;
        library.description =description;
        Observable observable = RxUtil.getAddLibraryObservable(library);
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
    public void onDeleteLib(Library library, int position) {
        mView.showWaitDialog("正在删除人脸库...");
        Observable<Cursor> observable = RxUtil.getDeleteLibraryObservable(library);
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
    public void onModifyLib(Library old, Library n, int position) {
        mView.showWaitDialog("正在更改人脸库名称...");
        Observable observable = RxUtil.getModifyLibraryObservable(old, n);
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
                values1.put("libName", n.libName);
                values1.put("description", n.description);
                mView.onSuccess(LibView.TYPE_MODIFY_LIB, position, values1);
                disposable.dispose();
                mView.dissmissDialog();
            }
        });
    }
}

package com.iustu.identification.ui.main.library.librariesmanage.mvp;

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
        Log.e("", "onInitData: ===========----------------");
        Observable observable = RxUtil.getQuaryObservalbe(false, RxUtil.DB_LIBRARY, RxUtil.LIBRARY_COLUMNS, null, null, null, null, null, null);
        observable.subscribe(new Observer<Cursor>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Cursor cursor) {
                Log.e("", "onInitData-onNext: ===========----------------");
                if (cursor.getCount() == 0)
                    return;
                List<Library> data = new ArrayList<>();
                while (cursor.moveToNext()) {
                    Library library = new Library(cursor.getString(0), cursor.getInt(1), cursor.getString(2), cursor.getInt(3));
                    data.add(library);
                }
                mView.bindData(data);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                disposable.dispose();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
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
    public void onCreateNewLib() {

    }

    /**
     * 删除人脸库的逻辑代码
     */
    public void onDeleteLib() {

    }

    /**
     * 更改人脸库信息的逻辑代码
     */
    public void onModifyLib() {

    }
}

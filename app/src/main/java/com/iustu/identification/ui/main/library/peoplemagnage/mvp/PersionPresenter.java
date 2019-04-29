package com.iustu.identification.ui.main.library.peoplemagnage.mvp;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.iustu.identification.entity.PersionInfo;
import com.iustu.identification.entity.PersonInfo;
import com.iustu.identification.ui.main.library.peoplemagnage.SearchDialog;
import com.iustu.identification.util.RxUtil;
import com.iustu.identification.util.SDKUtil;
import com.iustu.identification.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * created by sgh, 2019-4-3
 * 人脸库详情管理的Presenter
 */
public class PersionPresenter {
    private Disposable disposable;

    PersionView mView;
    PersionModel mModel;

    public PersionPresenter (PersionModel model) {
        this.mModel = model;
    }

    public void setView(PersionView view){
        this.mView = view;
    }
    /**
     * 初始加载数据调用
     */
    public void onInitData(String libName) {
        mView.showWaitDialog("正在加载数据...");
<<<<<<< HEAD
        Log.d("libText", "PersionPresenter:onInitData: " + libName);
=======
        Log.d("getDatabases",libName);
>>>>>>> e24cd149a86e3fc3c47f32f004bf1938e25ae7bf
        Observable observable = RxUtil.getQuaryObservalbe(false, libName, RxUtil.PERSIONINFO_COLUMNS, null, null, null, null, "id", null);
        observable.subscribe(new Observer<Cursor>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Cursor cursor) {
                List<PersionInfo> data = new ArrayList<>();
                while (cursor.moveToNext()) {
                    PersionInfo persionInfo = new PersionInfo();
                    persionInfo.feature = cursor.getString(cursor.getColumnIndex("feature"));
                    persionInfo.name = cursor.getString(cursor.getColumnIndex("name"));
                    persionInfo.gender = cursor.getString(cursor.getColumnIndex("gender"));
                    persionInfo.photoPath = cursor.getString(cursor.getColumnIndex("photoPath"));
                    persionInfo.identity = cursor.getString(cursor.getColumnIndex("identity"));
                    persionInfo.home = cursor.getString(cursor.getColumnIndex("home"));
                    persionInfo.other = cursor.getString(cursor.getColumnIndex("other"));
                    persionInfo.libName = cursor.getString(cursor.getColumnIndex("libName"));
                    persionInfo.image_id = cursor.getString(cursor.getColumnIndex("image_id"));
                    data.add(persionInfo);
                }
                mView.bindData(data);
                cursor.close();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                mView.onFailed(e.getMessage());
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
     * 点击“下一页”加载更多的时候调用
     */
    public void onLoadMore() {

    }

    /**
     * 点击"添加照片"的时候调用
     * @param persionInfo 表示需要添加图片的对象
     * @param path 表项添加的图片路径
     * @param position 表示其所在的位置
     */
    public void onAddPhoto(PersionInfo persionInfo, String path, int position) {
        mView.showWaitDialog("正在添加图片...");
        int issame = SDKUtil.checkIsSamePersion(persionInfo, path);
        if (issame == SDKUtil.NOFACE) {
            mView.dissmissDialog();
            ToastUtil.show("该图片不含人脸，添加失败");
            return;
        }
        if (issame == SDKUtil.MORE_ONE_FACE) {
            mView.dissmissDialog();
            ToastUtil.show("该图片含有多张人脸，添加失败");
            return;
        }
        if (issame == SDKUtil.NOTTHESAME) {
            mView.dissmissDialog();
            ToastUtil.show("该图片与目标人员相似度太低，添加失败");
            return;
        }
        ContentValues values = persionInfo.toContentValues();
        String fileName = persionInfo.name + System.currentTimeMillis() + ".jpg";
        values.put("photoPath", persionInfo.photoPath + ";" + fileName);
        Observable observable = RxUtil.getAddPhotoObservable(persionInfo, path, fileName);
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
                mView.onFailed(e.getMessage());
                mView.dissmissDialog();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
                mView.dissmissDialog();
                disposable = null;
                mView.onSuccess(PersionView.TYPE_ADD_PHOTO, position, values);
            }
        });
    }

    /**
     * 点击“删除照片”的时候调用
     * @param index 表示第几个PersionInfo需要删除图片
     * @param position 代表需要删除第几张图片
     * @param persionInfo 代表需要删除的PersionInfo对象
     */
    public void onDeletePhoto(int index, int position, PersionInfo persionInfo) {
        mView.showWaitDialog("正在删除图片...");
        String[] s = persionInfo.photoPath.split(";");
        String finalPath = null;
        String delete = null;
        for (int i = 0; i < s.length; i ++) {
            if (i != position) {
                finalPath = finalPath == null ? s[i] : (finalPath + ";" + s[i]);
            } else {
                delete = s[i];
                continue;
            }
        }
        delete = "/sdcard/DeepFace/" + persionInfo.libName + "/" + delete;
        persionInfo.photoPath = finalPath;
        ContentValues values = persionInfo.toContentValues();
        Observable observable = RxUtil.getDeletePhotoObservable(persionInfo, delete);
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
                mView.onFailed(e.getMessage());
                mView.dissmissDialog();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
                mView.dissmissDialog();
                disposable = null;
                mView.onSuccess(PersionView.TYPE_DELETE_PHOTO, index, values);
            }
        });
    }

    /**
     * 点击“删除”的时候调用
     */
    public void onDeletePer(int position, PersionInfo persionInfo) {
        mView.showWaitDialog("正在删除...");
        Log.d("libText", "PersionPresenter:onDeletePer: " + persionInfo.libName);
        Observable observable = RxUtil.getDeletePersonObservable(persionInfo);
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
                mView.onFailed(e.getMessage());
                mView.dissmissDialog();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
                mView.dissmissDialog();
                disposable = null;
                mView.onSuccess(PersionView.TYPE_DELETE_PER, position, null);
            }
        });
    }

    /**
     * 点击“保存”的时候调用
     */
    public void onSaveChange(int position, PersionInfo persionInfo) {
        mView.showWaitDialog("正在修改...");
        ContentValues values = persionInfo.toContentValues();
        Observable observable = RxUtil.getSavePersonChangeObservable(persionInfo);
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
                mView.onFailed(e.getMessage());
                mView.dissmissDialog();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
                mView.dissmissDialog();
                disposable = null;
                mView.onSuccess(PersionView.TYPE_SAVE_CHANGE, position, values);
            }
        });
    }

    public static void searchPerson(String libName, String where, SearchDialog searchDialog) {
        final Disposable[] disposable = new Disposable[1];
        Observable<Cursor> observable = RxUtil.getQuaryObservalbe(false, libName, new String[]{"id"}, where, null, null, null, null, null);
        observable.subscribe(new Observer<Cursor>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable[0] = d;
            }

            @Override
            public void onNext(Cursor cursor) {
                if(cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    searchDialog.postBack(id);
                } else {
                    searchDialog.postBack(-1);
                }
                cursor.close();
            }

            @Override
            public void onError(Throwable e) {
                searchDialog.showErrorMessage(e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable[0].dispose();
            }
        });
    }
}

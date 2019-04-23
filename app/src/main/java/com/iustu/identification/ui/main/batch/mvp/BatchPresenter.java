package com.iustu.identification.ui.main.batch.mvp;

import android.util.Log;

import com.iustu.identification.entity.PersionInfo;
import com.iustu.identification.util.FileUtil;
import com.iustu.identification.util.RxUtil;
import com.iustu.identification.util.StringUtil;
import com.iustu.identification.util.ToastUtil;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class BatchPresenter {
    private Disposable disposable;

    private BatchView view;

    public void setView(BatchView view) {
        this.view = view;
    }

    /**
     * 进行批量导入的时候的调用
     * @param pictures 需要导入的图片
     * @param libName 导入的目标人脸库
     */
    public void importBatchPictures(ArrayList<String> pictures, String libName) {
        ArrayList<RxUtil.BatchReturn> returns = new ArrayList<>();
        final int[] errCount = {0};
        final int[] successCount = {0};
        ArrayList<PersionInfo> persionInfos = StringUtil.clipPictures(pictures);
        for(PersionInfo persionInfo : persionInfos) {
            persionInfo.libName = libName;
            Log.d("search", "importBatchPictures: " + persionInfo.image_id);
        }
        Observable<RxUtil.BatchReturn> observable = RxUtil.getImportBatchPersionObservable(persionInfos);
        observable.subscribe(new Observer<RxUtil.BatchReturn>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(RxUtil.BatchReturn o) {
                if (o.isSuccessed)
                    successCount[0] = successCount[0] +1;
                else{
                    returns.add(o);
                    errCount[0] = errCount[0] + 1;
                }
                view.setProgress(successCount[0] + errCount[0]);
                view.setProgressTV(successCount[0], errCount[0]);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                view.changeSubmitable();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
                view.changeSubmitable();
                FileUtil.deleteTemp();
                if (returns.size() > 0) {
                    String s = "第";
                    for (RxUtil.BatchReturn batchReturn : returns) {
                        s += batchReturn.index + "、";
                    }
                    s += "张图片添加失败，请单独添加";
                    ToastUtil.showLong(s);
                }

            }
        });
    }
}

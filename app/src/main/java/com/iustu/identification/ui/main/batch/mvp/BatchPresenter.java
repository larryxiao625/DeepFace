package com.iustu.identification.ui.main.batch.mvp;

import android.util.Log;

import com.iustu.identification.entity.PersionInfo;
import com.iustu.identification.util.RxUtil;
import com.iustu.identification.util.StringUtil;

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
        final int[] errCount = {0};
        final int[] successCount = {0};
        ArrayList<PersionInfo> persionInfos = StringUtil.clipPictures(pictures);
        if (persionInfos == null)
            return;
        for(PersionInfo persionInfo : persionInfos) {
            persionInfo.libName = libName;
            persionInfo.image_id = System.currentTimeMillis() + "";
        }
        Observable<Boolean> observable = RxUtil.getImportBatchPersionObservable(persionInfos);
        observable.subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Boolean o) {
                if (o)
                    successCount[0] = successCount[0] +1;
                else
                    errCount[0] = errCount[0] + 1;
                view.setProgress(successCount[0] + errCount[0]);
                view.setProgressTV(successCount[0], errCount[0]);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
    }
}
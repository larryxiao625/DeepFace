package com.iustu.identification.ui.main.history.prenster;

import android.content.Context;
import android.database.Cursor;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.util.Log;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.iustu.identification.App;
import com.iustu.identification.bean.FaceCollectItem;
import com.iustu.identification.ui.main.history.view.HistoryFragment;
import com.iustu.identification.ui.main.history.view.IVew;
import com.iustu.identification.ui.widget.dialog.NormalDialog;
import com.iustu.identification.ui.widget.dialog.SingleButtonDialog;
import com.iustu.identification.ui.widget.dialog.WaitProgressDialog;
import com.iustu.identification.util.PickerViewFactor;
import com.iustu.identification.util.RxUtil;
import com.iustu.identification.util.TextUtil;
import com.iustu.identification.util.ToastUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.iustu.identification.util.LibManager.dispose;

public class HistoryPrenster implements IPrenster{
    private Disposable disposable;
    IVew compareHistoryIVew;
    IVew faceHistoryIVew;
    HistoryFragment.SwitchFragmentLister switchFragmentLister;
    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();
    int START_CALENDER=0; //fromDatChoose
    int END_CALENDER=1; //toDateChoose
    int FACE_HISTORY_VIEW=0;
    int COMPARE_HISTORY_VIEW=1;
    Context context;
    static HistoryPrenster instance;
    public static HistoryPrenster getInstance(Context context){
        if(instance==null){
            instance=new HistoryPrenster();
        }
        instance.context=context;
        return instance;
    }
    @Override
    public void attchCompareHistoryView(IVew iVew) {
        this.compareHistoryIVew=iVew;
    }

    @Override
    public void attchFaceHistoryView(IVew iVew) {
        this.faceHistoryIVew=iVew;
    }


    @Override
    public void attchSwitchFragment(HistoryFragment.SwitchFragmentLister switchFragmentLister) {
        this.switchFragmentLister=switchFragmentLister;
    }

    @Override
    public void startQuery() {

    }

    @Override
    public void initCalender(int viewType) {
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);
        if(viewType==FACE_HISTORY_VIEW) {
            faceHistoryIVew.setFromDateTv(TextUtil.getDateString2(startCalendar.getTime()));
            faceHistoryIVew.setToDateTv(TextUtil.getDateString2(endCalendar.getTime()));
        }else if(viewType==COMPARE_HISTORY_VIEW){
            compareHistoryIVew.setFromDateTv(TextUtil.getDateString2(startCalendar.getTime()));
            compareHistoryIVew.setToDateTv(TextUtil.getDateString2(endCalendar.getTime()));
        }
    }

    @Override
    public void initDateChoose(int viewType,int calenderType) {
        Calendar c = Calendar.getInstance();
        if(calenderType==START_CALENDER) {
            c.roll(Calendar.YEAR, -100);
        }else if(calenderType==END_CALENDER){
            c.roll(Calendar.YEAR,100);
        }
        TimePickerView timePickerView= PickerViewFactor.newTimePickerViewBuilder(context,(date, v)->{
                if(calenderType==START_CALENDER){
                    startCalendar.setTime(date);
                    startCalendar.set(Calendar.HOUR_OF_DAY, 0);
                    startCalendar.set(Calendar.MINUTE, 0);
                    startCalendar.set(Calendar.SECOND, 0);
                    if(viewType==COMPARE_HISTORY_VIEW) {
                        compareHistoryIVew.setFromDateTv(TextUtil.getDateString2(date));
                    }else if(viewType==FACE_HISTORY_VIEW){
                        faceHistoryIVew.setFromDateTv(TextUtil.getDateString2(date));
                    }
                }else if(calenderType==END_CALENDER){
                    endCalendar.setTime(date);
                    endCalendar.set(Calendar.HOUR_OF_DAY, 23);
                    endCalendar.set(Calendar.MINUTE, 59);
                    endCalendar.set(Calendar.SECOND, 59);
                    if (viewType==FACE_HISTORY_VIEW) {
                        faceHistoryIVew.setToDateTv(TextUtil.getDateString2(date));
                    }else if(viewType==COMPARE_HISTORY_VIEW){
                        compareHistoryIVew.setToDateTv(TextUtil.getDateString2(date));
                    }
                }
            }
        ).setRangDate(calenderType==START_CALENDER? c:startCalendar,calenderType==START_CALENDER? endCalendar:c)
                .setDate(calenderType==START_CALENDER? startCalendar:endCalendar)
                .build();
        if(viewType==FACE_HISTORY_VIEW) {
            faceHistoryIVew.showDateChoose(timePickerView);
        }else if(viewType==COMPARE_HISTORY_VIEW){
            compareHistoryIVew.showDateChoose(timePickerView);
        }
    }

    @Override
    public void queryError(int viewType) {
        NormalDialog normalDialog=new NormalDialog.Builder()
                .title("错误")
                .content("查询失败")
                .cancelable(false)
                .positive("重试", v->startQuery())
                .negative("返回", v-> switchFragmentLister.switchFragment(HistoryFragment.ID_FACE))
                .build();
        if(viewType==FACE_HISTORY_VIEW){
            faceHistoryIVew.showQueryError(normalDialog);
        }else if(viewType==COMPARE_HISTORY_VIEW){
            compareHistoryIVew.showQueryError(normalDialog);
        }
    }

    @Override
    public void queryProcessing(int viewType) {
        WaitProgressDialog waitProgressDialog = new WaitProgressDialog.Builder()
                .title("正在查询")
                .button("取消", v-> dispose())
                .build();
        if(viewType==FACE_HISTORY_VIEW){
            faceHistoryIVew.showQueryProcessing(waitProgressDialog);
        }else if(viewType==COMPARE_HISTORY_VIEW){
            compareHistoryIVew.showQueryProcessing(waitProgressDialog);
        }
    }

    @Override
    public void argumentsError(int viewType) {
        Log.d("CompareHistoryFragment","onArgumentsError");
        SingleButtonDialog singleButtonDialog=new SingleButtonDialog.Builder()
                .title("错误")
                .cancelable(false)
                .content("参数错误")
                .button("确定", v-> switchFragmentLister.switchFragment(HistoryFragment.ID_FACE))
                .build();
        if(viewType==FACE_HISTORY_VIEW){
            faceHistoryIVew.showArgumentsError(singleButtonDialog);
        }else if(viewType==COMPARE_HISTORY_VIEW){
            compareHistoryIVew.showArgumentsError(singleButtonDialog);
        }
    }

    @Override
    public void getFaceCollectionData(String fromTime, String toTime) {
        queryProcessing(FACE_HISTORY_VIEW);
        Observable observable = RxUtil.getQuaryObservalbe(false, RxUtil.DB_FACECOLLECTIOMITEM, RxUtil.FACECOLLECTION_COLUMNS, "datetime(time) between datetime('" + fromTime + "') and datetime('" + toTime + "')", null, null, null, null, null);
        observable.subscribe(new Observer<Cursor>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Cursor cursor) {
                Log.d("History","startQuery");
                if (cursor.getCount() == 0) {
                    ToastUtil.show("该期间无记录");
                    return;
                }
                List<FaceCollectItem> data = new ArrayList<>();
                while (cursor.moveToNext()) {
                    FaceCollectItem faceCollectItem = new FaceCollectItem();
                    faceCollectItem.setFaceId(cursor.getString(cursor.getColumnIndex("faceId")));
                    faceCollectItem.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    faceCollectItem.setImgUrl(cursor.getString(cursor.getColumnIndex("imgUrl")));
                    faceCollectItem.setTime(cursor.getString(cursor.getColumnIndex("time")));
                    data.add(faceCollectItem);
                }
                faceHistoryIVew.bindData(data);
            }

            @Override
            public void onError(Throwable e) {
                queryError(FACE_HISTORY_VIEW);
                e.printStackTrace();
                disposable.dispose();
                disposable = null;
            }

            @Override
            public void onComplete() {
                faceHistoryIVew.showSuccess();
                disposable.dispose();
                disposable = null;
            }
        });
    }
}

package com.iustu.identification.ui.main.history.view.compare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.iustu.identification.R;
import com.iustu.identification.api.Api;
import com.iustu.identification.api.message.Message;
import com.iustu.identification.api.message.response.AdvancedFaceSearchResponse;
import com.iustu.identification.bean.FaceResult;
import com.iustu.identification.bean.FaceSetResult;
import com.iustu.identification.bean.SearchCompareItem;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.main.MainActivity;
import com.iustu.identification.ui.main.history.adapter.CompareHistoryItemAdapter;
import com.iustu.identification.ui.main.history.prenster.HistoryPrenster;
import com.iustu.identification.ui.main.history.view.HistoryFragment;
import com.iustu.identification.ui.main.history.view.IVew;
import com.iustu.identification.ui.widget.dialog.NormalDialog;
import com.iustu.identification.ui.widget.dialog.SingleButtonDialog;
import com.iustu.identification.util.ExceptionUtil;
import com.iustu.identification.util.PageSetHelper;
import com.iustu.identification.util.PickerViewFactor;
import com.iustu.identification.util.TextUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Liu Yuchuan on 2017/11/22.
 */

public class CompareHistoryFragment extends BaseFragment{

    @BindView(R.id.compare_history_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.page_tv)
    TextView pageTv;
    @BindView(R.id.compare_date_from_tv)
    TextView fromDateTv;
    @BindView(R.id.compare_date_to_tv)
    TextView toDateTv;

    private static final String KEY_FACE_ID = "face_id";

    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();
    private PageSetHelper pageSetHelper;
    private CompareHistoryItemAdapter mAdapter;

    private final List<SearchCompareItem> compareItemList = new ArrayList<>();

    private String faceId;

    HistoryPrenster historyPrenster=new HistoryPrenster();

    @Override
    protected int postContentView() {
        return R.layout.fragment_compare_history;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        historyPrenster.attchView(iVew);
        historyPrenster.initCalender();
        mAdapter = new CompareHistoryItemAdapter(compareItemList);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3, LinearLayoutManager.HORIZONTAL, false){
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });
        recyclerView.setAdapter(mAdapter);
        pageSetHelper = new PageSetHelper(recyclerView, pageTv);
    }

    @Override
    public void onShow() {
        super.onShow();
        Bundle bundle = getArguments();
        if(bundle == null){
            onArgumentsError();
            return;
        }

        faceId = bundle.getString(KEY_FACE_ID, null);
        if(faceId == null){
            onArgumentsError();
            return;
        }
        mAdapter.setTargetPhotoUrl(Api.getFaceSearchImageUrl(faceId));
        startQuery();
    }

    @Override
    public void onHide() {
        super.onHide();
        compareItemList.clear();
        if(mAdapter != null){
            mAdapter.notifyDataChange();
        }
    }
    @OnClick(R.id.compare_start_query_tv)
    public void startQuery(){

    }

    @OnClick(R.id.compare_date_from_tv)
    public void fromDateChoose(){
        historyPrenster.initDateChoose(0);
    }

    @OnClick(R.id.compare_date_to_tv)
    public void toDateChoose(){
        historyPrenster.initDateChoose(1);
    }

    private void onFail(){
        ((MainActivity)mActivity).dismissWaiDialog();
        new NormalDialog.Builder()
                .title("错误")
                .content("查询失败")
                .cancelable(false)
                .positive("重试", v->startQuery())
                .negative("返回", v-> ((HistoryFragment)getParentFragment()).switchFragment(HistoryFragment.ID_FACE))
                .show(mActivity.getFragmentManager());
    }

    public void onArgumentsError(){
        Log.d("CompareHistoryFragment","onArgumentsError");
        new SingleButtonDialog.Builder()
                .title("错误")
                .cancelable(false)
                .content("参数错误")
                .button("确定", v-> ((HistoryFragment)getParentFragment()).switchFragment(HistoryFragment.ID_FACE))
                .show(mActivity.getFragmentManager());
    }

    @Override
    protected void dispose() {
        super.dispose();
        if(mAdapter != null){
            mAdapter.dispose();
        }
    }

    public void setArguments(String faceId){
        Bundle bundle =getArguments();
        if(bundle == null){
            bundle = new Bundle();
        }
        bundle.putString(KEY_FACE_ID, faceId);
        setArguments(bundle);
    }

    @OnClick(R.id.last_page_iv)
    public void onLastPage(){
        pageSetHelper.lastPage();
    }

    @OnClick(R.id.next_page_iv)
    public void onNextPage(){
        pageSetHelper.nextPage();
    }

    IVew iVew=new IVew() {
        @Override
        public void setToDateTv(String date) {
            toDateTv.setText(date);
        }

        @Override
        public void setFromDateTv(String date) {
            fromDateTv.setText(date);
        }

        @Override
        public void showDateChoose(TimePickerView timePickerView) {
            timePickerView.show();
        }
    };
}

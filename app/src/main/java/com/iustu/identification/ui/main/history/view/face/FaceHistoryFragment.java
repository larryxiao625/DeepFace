package com.iustu.identification.ui.main.history.view.face;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.iustu.identification.R;
import com.iustu.identification.api.Api;
import com.iustu.identification.api.message.Message;
import com.iustu.identification.api.message.response.SearchImageHistoryResponse;
import com.iustu.identification.bean.FaceCollectItem;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.main.history.adapter.FaceCollectItemAdapter;
import com.iustu.identification.ui.main.history.prenster.HistoryPrenster;
import com.iustu.identification.ui.main.history.view.HistoryFragment;
import com.iustu.identification.ui.main.history.view.IVew;
import com.iustu.identification.ui.main.history.view.compare.CompareHistoryFragment;
import com.iustu.identification.ui.widget.dialog.NormalDialog;
import com.iustu.identification.ui.widget.dialog.WaitProgressDialog;
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
 * Created by Liu Yuchuan on 2017/11/21.
 */

public class FaceHistoryFragment extends BaseFragment {
    @BindView(R.id.date_from_tv)
    TextView fromDateTv;
    @BindView(R.id.date_to_tv)
    TextView toDateTv;
    @BindView(R.id.face_history_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.page_tv)
    TextView pageTv;

    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();
    private FaceCollectItemAdapter mAdapter;
    private List<FaceCollectItem> itemList;
    private PageSetHelper pageSetHelper;

    private int page;
    private int totalPage;

    private WaitProgressDialog waitProgressDialog;

    HistoryPrenster historyPrenster=new HistoryPrenster();

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        historyPrenster.initCalender();
        historyPrenster.attchView(iVew);
        itemList = new ArrayList<>();
        mAdapter = new FaceCollectItemAdapter(itemList);
        mAdapter.setOnPageItemClickListener((view1, position, index) -> {
            FaceCollectItem item = itemList.get(index);
            HistoryFragment historyFragment = (HistoryFragment) getParentFragment();
            CompareHistoryFragment compareHistoryFragment = historyFragment.getFragment(HistoryFragment.ID_COMPARE);
            compareHistoryFragment.setArguments(item.getFaceId());
            historyFragment.switchFragment(HistoryFragment.ID_COMPARE);
        });
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3));
        pageSetHelper = new PageSetHelper(recyclerView, pageTv);
    }

    @Override
    protected int postContentView() {
        return R.layout.fragment_face_history;
    }

    public void loadData(int page){
        if(page >= totalPage && page > 0){
            return;
        }

    }

    @OnClick(R.id.date_from_tv)
    public void fromDateChoose(){
        historyPrenster.initDateChoose(0);
    }

    @OnClick(R.id.date_to_tv)
    public void toDateChoose(){
        historyPrenster.initDateChoose(1);
    }

    @OnClick(R.id.start_query_tv)
    public void startQuery(){
        loadData(0);
    }

    public void onFail(String extraMessage){
        if(waitProgressDialog != null){
            waitProgressDialog.dismiss();
        }
        new NormalDialog.Builder()
                .title("错误")
                .content("查询失败," + extraMessage)
                .positive("重试", v->loadData(0))
                .negative("确定", null)
                .show(mActivity.getFragmentManager());
    }

    public void showSingleButton(){
        waitProgressDialog = new WaitProgressDialog.Builder()
                .title("正在查询")
                .button("取消", v-> dispose())
                .build();
        waitProgressDialog.show(mActivity.getFragmentManager(), "wait");
    }

    @OnClick(R.id.last_page_iv)
    public void onLastPage(){
        pageSetHelper.lastPage();
    }

    @OnClick(R.id.next_page_iv)
    public void onNextPage(){
        pageSetHelper.nextPage();
        if(mAdapter.getPageNow() >= mAdapter.getPageMax() - 1){
            loadData(++page);
        }
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

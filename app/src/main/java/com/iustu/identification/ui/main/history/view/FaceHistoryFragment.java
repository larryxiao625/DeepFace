package com.iustu.identification.ui.main.history.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.iustu.identification.R;
import com.iustu.identification.bean.FaceCollectItem;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.main.history.adapter.FaceCollectItemAdapter;
import com.iustu.identification.ui.main.history.prenster.HistoryPrenster;
import com.iustu.identification.ui.main.history.view.HistoryFragment;
import com.iustu.identification.ui.main.history.view.IVew;
import com.iustu.identification.ui.main.history.view.CompareHistoryFragment;
import com.iustu.identification.ui.widget.dialog.NormalDialog;
import com.iustu.identification.ui.widget.dialog.SingleButtonDialog;
import com.iustu.identification.ui.widget.dialog.WaitProgressDialog;
import com.iustu.identification.util.PageSetHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

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

    HistoryPrenster historyPrenster;

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        historyPrenster=HistoryPrenster.getInstance(getActivity());
        historyPrenster.attchFaceHistoryView(iVew);
        historyPrenster.initCalender(0);
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
        historyPrenster.initDateChoose(0,0);
    }

    @OnClick(R.id.date_to_tv)
    public void toDateChoose(){
        historyPrenster.initDateChoose(0,1);
    }

    @OnClick(R.id.start_query_tv)
    public void startQuery(){
        //loadData(0);
        historyPrenster.getFaceCollectionData(fromDateTv.getText().toString(), toDateTv.getText().toString());
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


        @Override
        public void showQueryError(NormalDialog normalDialog) {
            normalDialog.show(mActivity.getFragmentManager(),"queryError");
        }

        @Override
        public void showQueryProcessing(WaitProgressDialog waitProgressDialog) {
            waitProgressDialog.show(mActivity.getFragmentManager(),"queryProcessing");
        }

        @Override
        public void showArgumentsError(SingleButtonDialog singleButtonDialog) {
            singleButtonDialog.show(mActivity.getFragmentManager(),"argumentsError");
        }

        @Override
        public void bindData(List data) {
            itemList.addAll(data);
            mAdapter.notifyDataChange();
        }
    };
}

package com.iustu.identification.ui.main.history.view;

import android.graphics.BitmapFactory;
import android.location.GnssMeasurementsEvent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.iustu.identification.R;
import com.iustu.identification.entity.CompareRecord;
import com.iustu.identification.ui.base.BaseDialogFragment;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.main.history.adapter.CompareHistoryItemAdapter;
import com.iustu.identification.ui.main.history.prenster.HistoryPrenster;
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
 * Created by Liu Yuchuan on 2017/11/22.
 */

public class CompareHistoryFragment extends BaseFragment implements CompareHistoryItemAdapter.CompareListener {

    @BindView(R.id.compare_history_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.page_tv)
    TextView pageTv;
    @BindView(R.id.compare_date_from_tv)
    TextView fromDateTv;
    @BindView(R.id.compare_date_to_tv)
    TextView toDateTv;
    @BindView(R.id.compare_original_iv)
    ImageView originalPhoto;

    private static final String KEY_FACE_ID = "face_id";

    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();
    private PageSetHelper pageSetHelper;
    private CompareHistoryItemAdapter mAdapter;

    private final List<CompareRecord> compareItemList = new ArrayList<>();

    private String faceId;

    HistoryPrenster historyPrenster;

    ArrayList<BaseDialogFragment> baseDialogFragments=new ArrayList<>();

    @Override
    protected int postContentView() {
        return R.layout.fragment_compare_history;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        historyPrenster=HistoryPrenster.getInstance(getActivity());
        historyPrenster.attchCompareHistoryView(iVew);
        historyPrenster.initCalender(1);
        mAdapter = new CompareHistoryItemAdapter(compareItemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setAdapter(mAdapter);
        pageSetHelper = new PageSetHelper(recyclerView, pageTv);
        mAdapter.setListener(this);
        originalPhoto.setOnClickListener(v -> {
            v.setVisibility(View.GONE);
        });
    }

    @Override
    public void onShow() {
        super.onShow();
        Bundle bundle = getArguments();
        if(bundle == null){
            historyPrenster.argumentsError(1);
            return;
        }

        faceId = bundle.getString(KEY_FACE_ID, null);
        if(faceId == null){
            historyPrenster.argumentsError(1);
            return;
        }
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
        historyPrenster.getCompareRecord(fromDateTv.getText().toString(),toDateTv.getText().toString());
    }

    @OnClick(R.id.compare_date_from_tv)
    public void fromDateChoose(){
        historyPrenster.initDateChoose(1,0);
    }

    @OnClick(R.id.compare_date_to_tv)
    public void toDateChoose(){
        historyPrenster.initDateChoose(1,1);
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

        @Override
        public void showQueryError(NormalDialog normalDialog) {
            for(int i=0;i<baseDialogFragments.size();i++){
                baseDialogFragments.get(i).dismiss();
                baseDialogFragments.remove(i);
            }
            baseDialogFragments.add(normalDialog);
            normalDialog.show(mActivity.getFragmentManager(),"queryError");
        }

        @Override
        public void showQueryProcessing(WaitProgressDialog waitProgressDialog) {
            for(int i=0;i<baseDialogFragments.size();i++){
                baseDialogFragments.get(i).dismiss();
                baseDialogFragments.remove(i);
            }
            baseDialogFragments.add(waitProgressDialog);
            waitProgressDialog.show(mActivity.getFragmentManager(),"queryProcessing");
        }

        @Override
        public void showArgumentsError(SingleButtonDialog singleButtonDialog) {
            for(int i=0;i<baseDialogFragments.size();i++){
                baseDialogFragments.get(i).dismiss();
                baseDialogFragments.remove(i);
            }
            baseDialogFragments.add(singleButtonDialog);
            singleButtonDialog.show(mActivity.getFragmentManager(),"argumentsError");
        }

        @Override
        public void bindData(List data) {
            compareItemList.clear();
            compareItemList.addAll(data);
            mAdapter.notifyDataChange();
        }

        @Override
        public void showSuccess() {
            for(int i=0;i<baseDialogFragments.size();i++){
                baseDialogFragments.get(i).dismiss();
                baseDialogFragments.remove(i);
            }
        }

        @Override
        public void onSuccess(int position) {
            compareItemList.remove(position);
            mAdapter.notifyDataChange();
        }
    };

    @Override
    public void onDelete(CompareRecord compareRecord, int position) {
        historyPrenster.deleteCompareItem(compareRecord, position);
    }

    @Override
    public void lookOriginPhoto(int position) {
        if (originalPhoto.getVisibility() == View.GONE) {
            Glide.with(this)
                    .load(BitmapFactory.decodeFile(compareItemList.get(position).getOriginalPhoto()))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(originalPhoto);
            originalPhoto.setVisibility(View.VISIBLE);
        }
    }
}

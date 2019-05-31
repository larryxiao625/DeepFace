package com.iustu.identification.ui.main.history.view;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.iustu.identification.R;
import com.iustu.identification.bean.FaceCollectItem;
import com.iustu.identification.ui.base.BaseDialogFragment;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.main.history.adapter.FaceCollectItemAdapter;
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
 * Created by Liu Yuchuan on 2017/11/21.
 */

public class FaceHistoryFragment extends BaseFragment implements FaceCollectItemAdapter.FaceItemClickListener, View.OnClickListener {
    @BindView(R.id.date_from_tv)
    TextView fromDateTv;
    @BindView(R.id.date_to_tv)
    TextView toDateTv;
    @BindView(R.id.face_history_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.page_tv)
    TextView pageTv;
    @BindView(R.id.face_history_original_iv)
    ImageView originalPhoto;
    @BindView(R.id.face_history_original_fl)
    FrameLayout frameLayout;
    @BindView(R.id.restart_all_fail)
    TextView reUploadFail;

    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();
    private FaceCollectItemAdapter mAdapter;
    private List<FaceCollectItem> itemList;
    private PageSetHelper pageSetHelper;

    private int page;
    private int totalPage;


    HistoryPrenster historyPrenster;

    ArrayList<BaseDialogFragment> dialogArrayList=new ArrayList();

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
        mAdapter.setItemClickListener(this::lookOriginal);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3));
        pageSetHelper = new PageSetHelper(recyclerView, pageTv);
        originalPhoto.setOnClickListener(this);
        frameLayout.setOnClickListener(this);
        reUploadFail.setOnClickListener(this);
    }

    @Override
    protected int postContentView() {
        return R.layout.fragment_face_history;
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
        Log.d("history","getHistory");
        historyPrenster.getFaceCollectionData(fromDateTv.getText().toString(), toDateTv.getText().toString());
    }

    @OnClick(R.id.last_page_iv)
    public void onLastPage(){
        pageSetHelper.lastPage();
    }

    @OnClick(R.id.next_page_iv)
    public void onNextPage(){
        pageSetHelper.nextPage();
//        if(mAdapter.getPageNow() >= mAdapter.getPageMax() - 1){
//            loadData(++page);
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.face_history_original_iv:
                v.setVisibility(View.GONE);
                frameLayout.setVisibility(View.GONE);
            case R.id.face_history_original_fl:
                v.setVisibility(View.GONE);
                originalPhoto.setVisibility(View.GONE);
            case R.id.restart_all_fail:
                historyPrenster.getFaceUploadFailData(fromDateTv.getText().toString(),toDateTv.getText().toString());
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
            for(int i=0;i<dialogArrayList.size();i++){
                dialogArrayList.get(i).dismiss();
                dialogArrayList.remove(i);
            }
            dialogArrayList.add(normalDialog);
            normalDialog.show(mActivity.getSupportFragmentManager(),"queryError");
        }

        @Override
        public void showQueryProcessing(WaitProgressDialog waitProgressDialog) {
            for(int i=0;i<dialogArrayList.size();i++){
                dialogArrayList.get(i).dismiss();
                dialogArrayList.remove(i);
            }
            dialogArrayList.add(waitProgressDialog);
            waitProgressDialog.show(mActivity.getSupportFragmentManager(),"queryProcessing");
        }

        @Override
        public void showArgumentsError(SingleButtonDialog singleButtonDialog) {
            for(int i=0;i<dialogArrayList.size();i++){
                dialogArrayList.get(i).dismiss();
                dialogArrayList.remove(i);
            }
            dialogArrayList.add(singleButtonDialog);
            singleButtonDialog.show(mActivity.getSupportFragmentManager(),"argumentsError");
        }

        @Override
        public void bindData(List data) {
            itemList.clear();
            itemList.addAll(data);
            mAdapter.notifyDataChange();
        }

        @Override
        public void showSuccess() {
            for (int i=0;i<dialogArrayList.size();i++){
                dialogArrayList.get(i).dismiss();
                dialogArrayList.remove(i);
            }
        }

        @Override
        public void onSuccess(int position) {

        }
    };

    @Override
    public void lookOriginal(int position) {
        if (frameLayout.getVisibility() == View.GONE) {
            originalPhoto.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(BitmapFactory.decodeFile(itemList.get(position).getOriginalPhoto()))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(originalPhoto);
        }
    }
}

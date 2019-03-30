package com.iustu.identification.ui.main.history.compare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.iustu.identification.R;
import com.iustu.identification.api.Api;
import com.iustu.identification.api.message.Message;
import com.iustu.identification.api.message.response.AdvancedFaceSearchResponse;
import com.iustu.identification.bean.FaceResult;
import com.iustu.identification.bean.FaceSetResult;
import com.iustu.identification.bean.SearchCompareItem;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.main.MainActivity;
import com.iustu.identification.ui.main.history.HistoryFragment;
import com.iustu.identification.ui.widget.dialog.NormalDialog;
import com.iustu.identification.ui.widget.dialog.SingleButtonDialog;
import com.iustu.identification.util.ExceptionUtil;
import com.iustu.identification.util.PageSetHelper;

import java.util.ArrayList;
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

    private static final String KEY_FACE_ID = "face_id";

    private PageSetHelper pageSetHelper;
    private CompareHistoryItemAdapter mAdapter;

    private final List<SearchCompareItem> compareItemList = new ArrayList<>();

    private String faceId;

    @Override
    protected int postContentView() {
        return R.layout.fragment_compare_history;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        mAdapter = new CompareHistoryItemAdapter(compareItemList);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3, LinearLayoutManager.HORIZONTAL, false){
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });
        recyclerView.setAdapter(mAdapter);
        pageSetHelper = new PageSetHelper(recyclerView, pageTv);
        onShow();
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

    public void startQuery(){
        Api.queryComparedResult(faceId)
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    ((MainActivity)mActivity).showWaitDialog("正在查询", v-> ((HistoryFragment)getParentFragment()).switchFragment(HistoryFragment.ID_FACE));
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listMessage -> {
                    if(listMessage.getCode() != Message.CODE_SUCCESS){
                        onFail();
                    }else {
                        if(listMessage.getBody() == null){
                            return;
                        }
                        for (AdvancedFaceSearchResponse advancedFaceSearchResponse : listMessage.getBody()) {
                            for (FaceResult faceResult : advancedFaceSearchResponse.getFaceResults()) {
                                for (FaceSetResult faceSetResult : faceResult.getFaceSetResults()) {
                                    compareItemList.add(new SearchCompareItem(faceResult.getFaceSetId(), faceSetResult, advancedFaceSearchResponse.getFaceRect()));
                                }
                            }
                        }
                    }
                }, t->{
                    ((MainActivity)mActivity).dismissWaiDialog();
                    onFail();
                    ExceptionUtil.getThrowableMessage(CompareHistoryFragment.this.getClass().getSimpleName(), t);
                }, ()->{
                    ((MainActivity)mActivity).dismissWaiDialog();
                    mAdapter.notifyDataChange();
                    pageSetHelper.notifyChange();
                });
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
}

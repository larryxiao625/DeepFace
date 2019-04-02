package com.iustu.identification.ui.main.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.iustu.identification.R;
import com.iustu.identification.api.Api;
import com.iustu.identification.api.message.Message;
import com.iustu.identification.api.message.response.AdvancedFaceSearchResponse;
import com.iustu.identification.bean.FaceResult;
import com.iustu.identification.bean.SearchCompareItem;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.main.MainActivity;
import com.iustu.identification.ui.widget.TitleBar;
import com.iustu.identification.ui.widget.dialog.NormalDialog;
import com.iustu.identification.util.ExceptionUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Liu Yuchuan on 2017/11/4.
 */

public class CompareFragment extends BaseFragment implements TitleBar.TitleBarListener{

    @BindView(R.id.compare_tb)
    TitleBar titleBar;
    @BindView(R.id.photo_iv)
    ImageView photo;
    @BindView(R.id.compare_rv)
    RecyclerView recyclerView;

    private CompareItemAdapter mAdapter;

    private List<SearchCompareItem> mSearchCompareItemList;

    private File file;

    private int imgWidth;
    private int imgHeight;

    @Override
    protected int postContentView() {
        return R.layout.fragment_compare;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        titleBar.setTitleBarListener(this);
        mSearchCompareItemList = new ArrayList<>();
        mAdapter = new CompareItemAdapter(mSearchCompareItemList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
    }

    @Override
    public void onTitleButtonClick(int id) {
        onBackPressed();
    }

    public void startCompare(){
        Api.advancedFaceSearch(file)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::addDisposable)
                .subscribe(messageResponse -> {
                    if(!messageResponse.isSuccessful()){
                        onOptFail("错误码(" + messageResponse.code() + ")");
                    }else if(messageResponse.body()== null){
                        onOptFail("响应错误");
                    }else if(messageResponse.body().getCode() != Message.CODE_SUCCESS){
                        switch (messageResponse.body().getCode()) {
                            case Message.CODE_NO_LILBRARY:
                                onOptFail("未选择比对库");
                                break;
                            default:
                                onOptFail("错误码(" + messageResponse.body().getCode() + ")");
                                break;
                        }
                    }else {
                        List<AdvancedFaceSearchResponse> list = messageResponse.body().getBody();
                        for (AdvancedFaceSearchResponse advancedFaceSearchResponse : list) {
                            if (advancedFaceSearchResponse.getFaceResults() != null && advancedFaceSearchResponse.getFaceResults().size() > 0) {
                                for (FaceResult faceResult : advancedFaceSearchResponse.getFaceResults()) {
                                    if(faceResult.getFaceSetResults() != null && faceResult.getFaceSetResults().size() > 0) {
                                        SearchCompareItem item = new SearchCompareItem(faceResult.getFaceSetId(), faceResult.getFaceSetResults().get(0), advancedFaceSearchResponse.getFaceRect(), file.getAbsolutePath());
                                        item.setWidth(imgWidth);
                                        item.setHeight(imgHeight);
                                        mSearchCompareItemList.add(item);
                                    }
                                }
                            }
                        }
                    }
                    ((MainActivity)mActivity).getFragment(3).setInitData(false);
                }, throwable -> {
                    onOptFail("连接服务器失败");
                    ExceptionUtil.getThrowableMessage(CompareFragment.this.getTag(), throwable);
                }, () -> {
//                    Collections.sort(mSearchCompareItemList, (o1, o2) -> Double.compare(o2.getScore(), o1.getScore()));
//                    if(mSearchCompareItemList.size() > ParametersConfig.getInstance().getDisplayCount()){
//                        List<SearchCompareItem> list = new ArrayList<>(mSearchCompareItemList);
//                        mSearchCompareItemList.addAll(list.subList(0, ParametersConfig.getInstance().getDisplayCount()));
//                        mSearchCompareItemList.clear();
//                    }
                    mAdapter.notifyDataSetChanged();
                    ((MainActivity)mActivity).dismissWaiDialog();
                });
    }

    public void onOptFail(String extra){
        ((MainActivity)mActivity).dismissWaiDialog();
        new NormalDialog.Builder()
                .title("错误")
                .content("比对失败," + extra)
                .cancelable(false)
                .positive("重试", v->startCompare())
                .negative("返回", v -> ((MainActivity) mActivity).switchFragment(0))
                .show(mActivity.getFragmentManager());
    }

    @Override
    public void onBackPressed() {
        ((MainActivity) mActivity).dismissWaiDialog();
        ((MainActivity)mActivity).switchFragment(0);
    }

    public void loadImg(File file){
        ((MainActivity)mActivity).showWaitDialog("正在比对", v -> ((MainActivity)mActivity).switchFragment(0));
        this.file = file;
        photo.post(() -> {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imgWidth = bitmap.getWidth();
            imgHeight = bitmap.getHeight();
            bitmap.recycle();
            Glide.with(mActivity)
                    .setDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.photo_holder))
                    .load(Uri.fromFile(file))
                    .into(photo);
            startCompare();
        });
    }
}

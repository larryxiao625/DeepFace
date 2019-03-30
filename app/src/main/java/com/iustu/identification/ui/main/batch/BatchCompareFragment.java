package com.iustu.identification.ui.main.batch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iustu.identification.R;
import com.iustu.identification.api.Api;
import com.iustu.identification.api.message.Message;
import com.iustu.identification.api.message.response.AdvancedFaceSearchResponse;
import com.iustu.identification.bean.BatchCompareImg;
import com.iustu.identification.bean.FaceResult;
import com.iustu.identification.bean.SearchCompareItem;
import com.iustu.identification.config.ParametersConfig;
import com.iustu.identification.config.SystemConfig;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.main.batch.folder.FolderChooseFragment;
import com.iustu.identification.ui.main.batch.img.ImgChooseFragment;
import com.iustu.identification.ui.main.camera.CompareItemAdapter;
import com.iustu.identification.ui.widget.dialog.NormalDialog;
import com.iustu.identification.ui.widget.dialog.WaitProgressDialog;
import com.iustu.identification.util.ExceptionUtil;
import com.iustu.identification.util.FileUtil;
import com.iustu.identification.util.IconFontUtil;
import com.iustu.identification.util.MSP;
import com.iustu.identification.util.NetWorkManager;
import com.iustu.identification.util.TextUtil;
import com.iustu.identification.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by Liu Yuchuan on 2017/11/17.
 */

public class BatchCompareFragment extends BaseFragment {
    @BindView(R.id.batch_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.folder_path_tv)
    TextView pathTv;
    @BindView(R.id.folder_tv)
    TextView folderIcon;
    @BindView(R.id.progress_layout)
    LinearLayout progressLayout;
    @BindView(R.id.compare_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.compare_progress_tv)
    TextView progressTv;

    private File mFolder;
    private boolean chooseFolder;
    private int depth;

    private final ArrayList<String> imgsToCompare = new ArrayList<>();

    private static final String FORMAT_PROGRESS = "%d/%d";

    public static final String KEY_REQUEST_TYPE = "TYPE";
    public static final int REQUEST_TYPE_INVALID = -1;
    public static final int REQUEST_TYPE_FILE = 0;

    private int successCount;
    private int errorCount;

    private CompareItemAdapter mAdapter;
    private List<SearchCompareItem> mSearchCompareItemList;
    private WaitProgressDialog waitProgressDialog;

    private boolean isInProgress;

    @Override
    protected int postContentView() {
        return R.layout.fragment_batch_compare;
    }

    public void reset(){
        mSearchCompareItemList.clear();
        if(mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }
        progressBar.setProgress(0);
        successCount = 0;
        errorCount = 0;
        progressTv.setText(String.format(Locale.ENGLISH,FORMAT_PROGRESS, successCount, imgsToCompare.size()));
    }



    public void showWait(){
        waitProgressDialog = new WaitProgressDialog.Builder()
                .title("正在比对")
                .cancelable(false)
                .button("取消", v-> onCancel())
                .build();
        waitProgressDialog.show(mActivity.getFragmentManager(), "wait");
    }

    @Override
    public void onShow() {
        isInProgress = false;
        Bundle bundle = getArguments();
        reset();
        if(bundle == null){
            return;
        }
        setArguments(null);
        int type = bundle.getInt(KEY_REQUEST_TYPE);
        if(type == REQUEST_TYPE_FILE){
            String path = bundle.getString(FolderChooseFragment.KEY_PATH, null);
            if(path == null)
                return;
            mFolder = new File(path);
            pathTv.setText(path);
            depth = bundle.getInt(FolderChooseFragment.KEY_DEPTH);
            chooseFolder = true;
            for (File file : mFolder.listFiles()) {
                if(FileUtil.isImg(file)){
                    imgsToCompare.add(file.getAbsolutePath());
                }
            }
        }
    }

    public void setImgList(List<String> pathList){
        imgsToCompare.clear();
        imgsToCompare.addAll(pathList);
    }

    private void startBatchCompare(){
        if(imgsToCompare.size() == 0){
            ToastUtil.show("未选择比对图片");
            return;
        }
        successCount = 0;
        errorCount = 0;
        final boolean change;
        if(SystemConfig.getInstance().isWlan4gSwitchOn() && NetWorkManager.getNetWorkState() == NetWorkManager.NETWORK_WIFI){
            // TODO: 2018/1/4 更改提示
            ToastUtil.show("关闭wifi，请保证打开数据");
            NetWorkManager.enableWifi(false);
            change = true;
        }else {
            change = false;
        }
        Observable.interval(2, TimeUnit.SECONDS)
                .take(imgsToCompare.size())
                .map(aLong -> imgsToCompare.get((int) (long)aLong))
                .subscribeOn(Schedulers.computation())
                .doOnSubscribe(d->{
                    onProgressChange();
                    addDisposable(d);
                    isInProgress = true;
                    showWait();
                    mSearchCompareItemList.clear();
                    mAdapter.notifyDataSetChanged();
                })
                .flatMap(s->Api.advancedFaceSearch(new File(s)))
                .observeOn(AndroidSchedulers.mainThread())
                .zipWith(Observable.fromIterable(imgsToCompare), this::dealResponse)
                .subscribe(addList -> {
                    mSearchCompareItemList.addAll(addList);
                    if(addList.size() != 0){
                        successCount++;
                    }
                    onProgressChange();
                }, t->{
                    if(change){
                        NetWorkManager.enableWifi(true);
                    }
                    if(waitProgressDialog != null){
                        waitProgressDialog.dismiss();
                    }
                    errorCount = imgsToCompare.size() - successCount;
                    onProgressChange();
                    ExceptionUtil.getThrowableMessage(BatchCompareFragment.class.getSimpleName(), t);
                    t.printStackTrace();
                    onComplete();
                }, ()->{
                    if(change){
                        NetWorkManager.enableWifi(true);
                    }
                    onComplete();
                });
    }

    private List<SearchCompareItem> dealResponse(Response<Message<List<AdvancedFaceSearchResponse>>> messageResponse, String path){
        List<SearchCompareItem> searchCompareItemList = new ArrayList<>();
        if(!messageResponse.isSuccessful() || messageResponse.body().getCode() != Message.CODE_SUCCESS){
            errorCount++;
            return searchCompareItemList;
        }
        List<AdvancedFaceSearchResponse> list = messageResponse.body().getBody();
//        String path = messageResponse.body().getId();
        for (AdvancedFaceSearchResponse advancedFaceSearchResponse : list) {
            if(advancedFaceSearchResponse.getFaceResults() != null && advancedFaceSearchResponse.getFaceResults().size() > 0) {
                for (FaceResult faceResult : advancedFaceSearchResponse.getFaceResults()) {
                    if(faceResult.getFaceSetResults() != null && faceResult.getFaceSetResults().size() > 0) {
                        SearchCompareItem searchCompareItem = new SearchCompareItem(faceResult.getFaceSetId(), faceResult.getFaceSetResults().get(0), advancedFaceSearchResponse.getFaceRect(), path);
                        searchCompareItemList.add(searchCompareItem);
                        BatchCompareImg batchCompareImg = new BatchCompareImg();
                        batchCompareImg.setCompared(true);
                        batchCompareImg.setPath(path);
                        batchCompareImg.save();
                    }
                }
            }
        }

        if(searchCompareItemList.size() == 0){
            successCount++;
        }
//        Collections.sort(searchCompareItemList, (o1, o2) -> Double.compare(o1.getScore(), o2.getScore()));
//        if(searchCompareItemList.size() > ParametersConfig.getInstance().getDisplayCount()){
//            return searchCompareItemList.subList(0, ParametersConfig.getInstance().getDisplayCount());
//        }
        return searchCompareItemList;
    }

    public void onProgressChange(){
        progressTv.setText(String.format(Locale.ENGLISH,FORMAT_PROGRESS, successCount + errorCount, imgsToCompare.size()));
        progressBar.setProgress((int) (100.0 * (successCount + errorCount) / imgsToCompare.size()));
    }

    public void onComplete(){
        if(waitProgressDialog != null){
            waitProgressDialog.dismiss();
        }
        new NormalDialog.Builder()
                .title("提示")
                .content(TextUtil.format("比对完成,其中成功%d个,失败%d个", successCount, errorCount))
                .positive("重试", v->{
                    reset();
                    startBatchCompare();
                })
                .negative("确定", null)
                .show(mActivity.getFragmentManager());
        isInProgress = false;
        mAdapter.notifyDataSetChanged();
    }

    public void onCancel(){
        dispose();
        if(waitProgressDialog != null){
            waitProgressDialog.dismiss();
        }
        new NormalDialog.Builder()
                .title("提示")
                .content(TextUtil.format("比对取消,已完成%d个,其中成功%d个,失败%d个", successCount + errorCount, successCount, errorCount))
                .positive("重试", v->{
                    reset();
                    startBatchCompare();
                })
                .negative("确定", null)
                .show(mActivity.getFragmentManager());
        isInProgress = false;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        IconFontUtil util = IconFontUtil.getDefault();
        util.setText(folderIcon, IconFontUtil.FOLDER);
        SharedPreferences preferences = MSP.getInstance("folder");
        depth = preferences.getInt(FolderChooseFragment.KEY_DEPTH, 0);
        String path = preferences.getString(FolderChooseFragment.KEY_PATH, null);
        if(path == null || depth == 0){
            mFolder = null;
        }else {
            mFolder = new File(path);
            if(!mFolder.exists()){
                mFolder = null;
                depth = 0;
            }else {
                pathTv.setText(path);
                for (File file : mFolder.listFiles()) {
                    if(FileUtil.isImg(file)){
                        imgsToCompare.add(file.getAbsolutePath());
                    }
                }
                chooseFolder = true;
            }
        }
        progressBar.setMax(100);
        mSearchCompareItemList = new ArrayList<>();
        mAdapter = new CompareItemAdapter(mSearchCompareItemList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_REQUEST_TYPE, REQUEST_TYPE_INVALID);
   }

    @OnClick(R.id.folder_tv)
    public void chooseFolder(){
        if(isInProgress){
            ToastUtil.show("请等待当前任务完成");
            return;
        }
        BatchFragment batchFragment = ((BatchFragment)getParentFragment());
        BaseFragment fragment = batchFragment.getFragment(BatchFragment.ID_FOLDER_CHOOSE);
        Bundle bundle = fragment.getArguments();
        if(bundle == null){
            bundle = new Bundle();
        }
        if(mFolder == null || depth == 0){
            bundle.putString(FolderChooseFragment.KEY_PATH, null);
            bundle.putInt(FolderChooseFragment.KEY_DEPTH, 0);
        }else {
            bundle.putString(FolderChooseFragment.KEY_PATH, mFolder.getAbsolutePath());
            bundle.putInt(FolderChooseFragment.KEY_DEPTH, depth);
        }
        fragment.setArguments(bundle);
        batchFragment.switchFragment(BatchFragment.ID_FOLDER_CHOOSE);
    }

    @OnClick(R.id.img_choose_tv)
    public void onImgChoose(){
        if(isInProgress){
            ToastUtil.show("请等待当前任务完成");
            return;
        }
        if(!chooseFolder){
            ToastUtil.show("请先选择图片所在文件夹！");
            return;
        }

        if(mFolder == null || !mFolder.exists()){
            chooseFolder = false;
            new NormalDialog.Builder()
                    .title("提示")
                    .content("所选文件夹无法读取（可能已经被删除），请重新选择")
                    .positive("重新选择", v -> chooseFolder())
                    .negative("取消", null)
                    .show(mActivity.getFragmentManager());
            return;
        }

        BatchFragment batchFragment = ((BatchFragment)getParentFragment());
        ImgChooseFragment imgChooseFragment = (ImgChooseFragment) batchFragment.getFragment(BatchFragment.ID_IMG_CHOOSE);
        Bundle bundle = new Bundle();
        bundle.putString(FolderChooseFragment.KEY_PATH, mFolder.getAbsolutePath());
        imgChooseFragment.setArguments(bundle);
        batchFragment.switchFragment(BatchFragment.ID_IMG_CHOOSE);
    }

    @OnClick(R.id.start_batch_compare_tv)
    public void startCompare(){
        if(isInProgress){
            ToastUtil.show("请等待当前任务完成");
            return;
        }
        if(!chooseFolder){
            ToastUtil.show("请先选择图片所在文件夹");
            return;
        }

        reset();
        recyclerView.setVisibility(View.VISIBLE);
        progressLayout.setVisibility(View.VISIBLE);
        startBatchCompare();
    }

    @Override
    public void onBackPressed() {
        if(isInProgress) {
            new NormalDialog.Builder()
                    .title("提示")
                    .content("停止当前任务?")
                    .negative("取消", null)
                    .positive("确定", v-> dispose())
                    .show(mActivity.getFragmentManager());
        }else {
            super.onBackPressed();
        }
    }
}

package com.iustu.identification.ui.main.batch.img;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iustu.identification.R;
import com.iustu.identification.bean.BatchCompareImg;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.main.batch.BatchCompareFragment;
import com.iustu.identification.ui.main.batch.BatchFragment;
import com.iustu.identification.ui.main.batch.folder.FolderChooseFragment;
import com.iustu.identification.ui.widget.TabBar;
import com.iustu.identification.ui.widget.TitleBar;
import com.iustu.identification.ui.widget.dialog.NormalDialog;
import com.iustu.identification.ui.widget.dialog.WaitProgressDialog;
import com.iustu.identification.util.ExceptionUtil;
import com.iustu.identification.util.FileUtil;
import com.iustu.identification.util.IconFontUtil;
import com.iustu.identification.util.ToastUtil;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Liu Yuchuan on 2017/11/18.
 */

public class ImgChooseFragment extends BaseFragment implements TitleBar.TitleBarListener, TabBar.OnTabBarSelectListener{
    private File mFolder;

    @BindView(R.id.title_bar_img_choose)
    TitleBar titleBar;
    @BindView(R.id.select_all_tv)
    TextView isSelectAllTv;
    @BindView(R.id.select_all_layout)
    LinearLayout selectLayout;
    @BindView(R.id.img_choose_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tab_bar)
    TabBar tabBar;

    private ImgAdapter mAdapter;

    private boolean isSelectAll;

    private HashSet<Integer> mChooseIndexSet;
    private List<BatchCompareImg> mAllImgList;
    private List<BatchCompareImg> mNewImgList;
    private List<BatchCompareImg> mHistoryList;

    private int selectNow;

    private WaitProgressDialog waitProgressDialog;

    @Override
    protected int postContentView() {
        return R.layout.img_choose;
    }

    public void showWait(){
        waitProgressDialog = new WaitProgressDialog.Builder()
                .title("正在读取")
                .cancelable(false)
                .button("取消", v->onBackPressed())
                .build();
        waitProgressDialog.show(mActivity.getFragmentManager(), "wait");
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        titleBar.setTitleBarListener(this);
        mAllImgList = new ArrayList<>();
        mNewImgList = new ArrayList<>();
        mHistoryList = new ArrayList<>();
        mChooseIndexSet = new HashSet<>();
        mAdapter = new ImgAdapter(mAllImgList);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3));
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((view1, position) -> {
            BatchCompareImg img = getList().get(position);
            if(!img.isChoose()){
                img.setChoose(true);
                mChooseIndexSet.add(mAllImgList.indexOf(img));
                isSelectAll = mAdapter.isSelectAll();
                if(isSelectAll){
                    IconFontUtil.getDefault().setText(isSelectAllTv, IconFontUtil.SELECT_ALL_SQUAD);
                }
            }else {
                img.setChoose(false);
                mChooseIndexSet.remove(mAllImgList.indexOf(img));
                if(isSelectAll){
                    isSelectAll = false;
                    IconFontUtil.getDefault().setText(isSelectAllTv, IconFontUtil.UNSELECT_SQUAD);
                }
            }
            mAdapter.notifyItemChanged(position);
        });
        tabBar.setOnTabBarSelectListener(this);
    }

    @Override
    public void onShow() {
        if(mAdapter != null){
            mAdapter.clear();
        }
        mFolder = null;
        Bundle bundle = getArguments();
        if(bundle != null){
            mFolder = new File(bundle.getString(FolderChooseFragment.KEY_PATH, ""));
        }

        if(bundle == null || !mFolder.exists() || mFolder.isFile()){
            new NormalDialog.Builder()
                    .title("错误")
                    .content("读取文件夹失败")
                    .positive("重选", v-> ((BatchFragment)getParentFragment()).switchFragment(BatchFragment.ID_FOLDER_CHOOSE))
                    .negative("取消", v-> backToCompare(false))
                    .show(mActivity.getFragmentManager());
            return;
        }

        showWait();
        isSelectAll = true;
        selectNow = 0;
        IconFontUtil.getDefault().setText(isSelectAllTv, IconFontUtil.SELECT_ALL_SQUAD);
        showImg();
    }

    private List<BatchCompareImg> getList(){
        switch (selectNow){
            case 1:
                return mNewImgList;
            case 2:
                return mHistoryList;
            default:
                return mAllImgList;
        }
    }

    @Override
    public void onHide() {
        if(mAllImgList != null) {
            mAllImgList.clear();
        }
        if(mNewImgList != null) {
            mNewImgList.clear();
        }
        if(mHistoryList != null) {
            mHistoryList.clear();
        }
        mAdapter.notifyDataSetChanged();
    }

    public void showImg(){
        tabBar.setSelected(0);
        if(mAllImgList == null){
            mAllImgList = new ArrayList<>();
        }
        if(mNewImgList == null){
            mNewImgList = new ArrayList<>();
        }
        if(mHistoryList == null){
            mHistoryList = new ArrayList<>();
        }
        mChooseIndexSet.clear();
        mAdapter.notifyDataSetChanged();
        Observable.fromArray(mFolder.listFiles())
                .doOnSubscribe(this::addDisposable)
                .subscribeOn(Schedulers.computation())
                .filter(FileUtil::isImg)
                .sorted((o1, o2) -> {
                    if(o1.lastModified() < o2.lastModified()){
                        return -1;
                    }
                    if(o1.lastModified() > o2.lastModified()){
                        return 1;
                    }
                    return 0;
                })
                .map(file -> {
                    int hash = file.hashCode();
                    List<BatchCompareImg> imgs = DataSupport.where("path = ?", file.getAbsolutePath()).find(BatchCompareImg.class);
                    if(imgs.size() > 0){
                        return imgs.get(0);
                    }
                    return new BatchCompareImg(hash, file.getAbsolutePath(), false);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(batchCompareImg -> {
                    mAllImgList.add(batchCompareImg);
                    if (batchCompareImg.isCompared()) {
                        mHistoryList.add(batchCompareImg);
                    } else {
                        batchCompareImg.setChoose(true);
                        mChooseIndexSet.add(mAllImgList.indexOf(batchCompareImg));
                        mNewImgList.add(batchCompareImg);
                    }
                }, t->{
                    if(waitProgressDialog != null){
                        waitProgressDialog.dismiss();
                    }
                    ExceptionUtil.getThrowableMessage(ImgChooseFragment.class.getSimpleName(), t);
                    ToastUtil.show("未知错误!");
                    onBackPressed();
                }, ()->{
                    mAdapter.setImgList(mAllImgList);
                    mAdapter.notifyDataSetChanged();
                    if(waitProgressDialog != null){
                        waitProgressDialog.dismiss();
                    }
                });
    }

    @OnClick(R.id.select_all_layout)
    public void onSelectAll(){
        if(!isSelectAll){
            selectAll();
        }else {
            unSelectAll();
        }
    }

    @Override
    public void onBackPressed() {
        backToCompare(false);
    }

    @Override
    public void onTitleButtonClick(int id) {
        if(id == TitleBar.ID_BACK) {
            backToCompare(false);
        }else {
            backToCompare(true);
        }
    }

    private void backToCompare(boolean choose){
        BatchFragment parent = (BatchFragment)getParentFragment();
        BatchCompareFragment batchCompareFragment = (BatchCompareFragment) parent.getFragment(BatchFragment.ID_BATCH_COMPARE);
        if(choose){
            ArrayList<String> list = new ArrayList<>();
            for (Integer integer : mChooseIndexSet) {
                list.add(mAllImgList.get(integer).getPath());
            }
            batchCompareFragment.setImgList(list);
        }
        parent.switchFragment(BatchFragment.ID_BATCH_COMPARE);
    }

    private void selectAll(){
        List<BatchCompareImg> list = getList();
        for (int i = 0; i < list.size(); i++) {
            BatchCompareImg img = list.get(i);
            if(!img.isChoose()){
                img.setChoose(true);
                mChooseIndexSet.add(i);
                mAdapter.notifyItemChanged(i);
            }
        }
        IconFontUtil.getDefault().setText(isSelectAllTv, IconFontUtil.SELECT_ALL_SQUAD);
        isSelectAll = true;
    }

    private void unSelectAll(){
        List<BatchCompareImg> list = getList();
        for (int i = 0; i < list.size(); i++) {
            BatchCompareImg img = list.get(i);
            if(img.isChoose()){
                img.setChoose(false);
                mChooseIndexSet.remove(i);
                mAdapter.notifyItemChanged(i);
            }
        }
        IconFontUtil.getDefault().setText(isSelectAllTv, IconFontUtil.UNSELECT_SQUAD);
        isSelectAll = false;
    }


    @Override
    public void onTabSelect(int id) {
        selectNow = id;
        mAdapter.setImgList(getList());
        isSelectAll = mAdapter.isSelectAll();
        if(isSelectAll){
            IconFontUtil.getDefault().setText(isSelectAllTv, IconFontUtil.SELECT_ALL_SQUAD);
        }else {
            IconFontUtil.getDefault().setText(isSelectAllTv, IconFontUtil.UNSELECT_SQUAD);
        }
        mAdapter.notifyDataSetChanged();
    }
}

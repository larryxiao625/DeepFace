package com.iustu.identification.ui.main.library.peoplemagnage;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.iustu.identification.R;
import com.iustu.identification.entity.PersionInfo;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.base.PageRecyclerViewAdapter;
import com.iustu.identification.ui.main.MainActivity;
import com.iustu.identification.ui.main.library.addperson.AddPersonFragment;
import com.iustu.identification.ui.main.library.peoplemagnage.mvp.PersionPresenter;
import com.iustu.identification.ui.main.library.peoplemagnage.mvp.PersionView;
import com.iustu.identification.ui.widget.dialog.EditDialog;
import com.iustu.identification.ui.widget.dialog.WaitProgressDialog;
import com.iustu.identification.util.ExceptionUtil;
import com.iustu.identification.util.FileCallBack;
import com.iustu.identification.util.ImageUtils;
import com.iustu.identification.util.PageSetHelper;
import com.iustu.identification.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Liu Yuchuan on 2017/11/21.
 *
 * 逻辑分析：
 * 1. 初始化
 * 2. 执行“人员删除”、“照片删除”、“添加照片”、“保存更改”、“加载更多”、“初始加载”等功能
 *
 * 修改思路：
 * 1. “人员删除”、“照片删除”、“添加照片”、“保存更改”、“加载更多”、“初始加载”等功能放到Presetner中
 */

public class PeopleManageFragment extends BaseFragment implements PersionView, PageRecyclerViewAdapter.LoadMoreListener{
    private static final String KEY_LIB_ID = "libId";
    private static final String KEY_LIB_NAME = "libName";

    @BindView(R.id.people_manage_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.page_tv)
    TextView pageTv;

    private String photoPath;     // 添加的照片的路径
    private WaitProgressDialog waitProgressDialog;

    private List<PersionInfo> mPersonList;
    private PersonInfoAdapter mAdapter;
    private PageSetHelper pageSetHelper;
    private PersionPresenter presenter;

    private int currentPersionPosition;      // 表示当前正在添加图片的Persion

    private String libName;
    private int libId;

    private int totalPage;
    private int page;
    private boolean isOnLoadMore;

    private int addPhotoIndex;
    private int addPhotoPosition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.libId = getArguments().getInt(KEY_LIB_ID);
        this.libName = getArguments().getString(KEY_LIB_NAME);
    }

    @Override
    protected int postContentView() {
        return R.layout.fragment_people_manage;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        super.initView(savedInstanceState, view);
        mPersonList = new ArrayList<>();
        mAdapter = new PersonInfoAdapter(mPersonList);
        mAdapter.setLoadMoreListener(this);
        mAdapter.setItemListener(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3 , LinearLayoutManager.HORIZONTAL, false){
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });
        pageSetHelper = new PageSetHelper(recyclerView, pageTv);
        recyclerView.post(this::onShow);
        this.libId = getArguments().getInt(KEY_LIB_ID);
        this.libName = getArguments().getString(KEY_LIB_NAME);
        onInitData();
    }

    private void loadData(int page){
        if(page != 0 && page >= totalPage){
            return;
        }

    }

    @Override
    public void onShow() {
        if(mAdapter != null) {
            mAdapter.notifyDataChange();
        }
        if(pageSetHelper != null){
            pageSetHelper.notifyChange();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void setArguments(String libName){
        Bundle bundle = getArguments();
        if(bundle == null){
            bundle = new Bundle();
        }
        bundle.putString(KEY_LIB_NAME, libName);
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


    @Override
    public void loadMore() {
        if(!isOnLoadMore) {
            loadData(++page);
        }
    }

    @OnClick(R.id.page_tv)
    public void onPageClick(){
        new EditDialog.Builder()
                .hint("输入跳转页数")
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .content(String.valueOf(mAdapter.getPageNow()))
                .negative("取消", null)
                .positive("跳转", (v1, content, layout) -> {
                    int pageTo;
                    try {
                        pageTo = Integer.parseInt(content);
                    }catch (NumberFormatException e){
                        layout.setError("输入不合法");
                        return false;
                    }

                    if(pageTo <= 0 || pageTo > mAdapter.getPageMax()){
                        layout.setError("输入不合法");
                        return false;
                    }
                    pageSetHelper.setPage(pageTo);
                    return true;
                })
                .show(mActivity.getFragmentManager());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ImageUtils.REQUEST_GALLERY && resultCode == Activity.RESULT_OK){
            String path = ImageUtils.getRealPathFromUri(mActivity, data.getData());
            photoPath = path;
            int degree = ImageUtils.readPictureDegree(path);
            if(degree == 0){
                presenter.onAddPhoto(mPersonList.get(currentPersionPosition), photoPath, currentPersionPosition);
            }else {
                Observable<File> observable = ImageUtils.modifiedSavePhoto("添加照片", path, ImageUtils.readPictureDegree(path), new FileCallBack() {
                    @Override
                    public void onStartSaveFile() {
                        ((MainActivity)mActivity).showWaitDialog("正在处理,请勿退出", v->dispose());
                    }
                });
                if(observable == null){
                    ToastUtil.show("照片处理失败");
                    return;
                }

                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(f -> {
                            ((MainActivity)mActivity).dismissWaiDialog();
                            //addPhoto(new File(path));
                            presenter.onAddPhoto(mPersonList.get(currentPersionPosition), photoPath, currentPersionPosition);
                        }, t->{
                            ((MainActivity)mActivity).dismissWaiDialog();
                            ToastUtil.show("照片处理失败");
                            ExceptionUtil.getThrowableMessage(AddPersonFragment.class.getSimpleName(), t);
                        });
            }
        }
    }

    @Override
    public void onDestroyView() {
        if(mAdapter != null){
            mAdapter.dispose();
        }
        super.onDestroyView();
    }

    @Override
    public void setPresenter(PersionPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void bindData(List<PersionInfo> data) {
        mPersonList.addAll(data);
        mAdapter.notifyDataChange();
    }

    @Override
    public void onInitData() {
        presenter.onInitData(libName);
    }

    @Override
    public void onLoadMore() {
        presenter.onLoadMore();
    }

    @Override
    public void onAddPhoto(int index) {
        currentPersionPosition = index;
        ImageUtils.startChoose(this);
    }

    @Override
    public void onDeletePhoto(int index, int position, PersionInfo persionInfo) {
        presenter.onDeletePhoto(index, position, persionInfo);
    }

    @Override
    public void onDeletePer(int position, PersionInfo persionInfo) {
        presenter.onDeletePer(position, persionInfo);
    }

    @Override
    public void onSaveChange(int position, PersionInfo persionInfo) {
        presenter.onSaveChange(position, persionInfo);
    }

    @Override
    public void showWaitDialog(String content) {
        waitProgressDialog = new WaitProgressDialog.Builder()
                .title(content)
                .cancelable(false)
                .build();
        waitProgressDialog.show(mActivity.getFragmentManager(), "Loading");
    }

    @Override
    public void dissmissDialog() {
        waitProgressDialog.dismiss();
        waitProgressDialog = null;
    }

    @Override
    public void onFailed(String message) {
        ToastUtil.show("操作失败:" + message);
    }

    @Override
    public void onSuccess(int type, int position, ContentValues values) {
        switch (type) {
            case TYPE_SAVE_CHANGE:
                PersionInfo persionInfo = mPersonList.get(position);
                persionInfo.name = values.getAsString("name");
                persionInfo.home = values.getAsString("home");
                persionInfo.gender = values.getAsString("gender");
                persionInfo.identity = values.getAsString("identity");
                mAdapter.notifyDataChange();
                break;
            case TYPE_DELETE_PER:
                mPersonList.remove(position);
                mAdapter.notifyDataChange();
                break;
            case TYPE_DELETE_PHOTO:
                mPersonList.get(position).photoPath = values.getAsString("photoPath");
                mAdapter.notifyDataChange();
                break;
            case TYPE_ADD_PHOTO:
                mPersonList.get(position).photoPath = values.getAsString("photoPath");
                mAdapter.notifyDataChange();
                break;
        }
    }

    // 搜索人员
    public void searchPerson() {
        SearchDialog fragment = new SearchDialog();
        Bundle bundle = new Bundle();
        bundle.putString("libName", libName);
        fragment.setArguments(bundle);
        fragment.show(getActivity().getFragmentManager(), "show");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(Integer page) {
        mAdapter.setPageNow(page);
        pageSetHelper.notifyChange();
    }
}

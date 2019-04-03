package com.iustu.identification.ui.main.library.peoplemagnage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;

import com.iustu.identification.R;
import com.iustu.identification.api.Api;
import com.iustu.identification.api.message.Message;
import com.iustu.identification.bean.Library;
import com.iustu.identification.bean.PersonInfo;
import com.iustu.identification.entity.PersionInfo;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.base.PageRecyclerViewAdapter;
import com.iustu.identification.ui.main.MainActivity;
import com.iustu.identification.ui.main.library.LibraryFragment;
import com.iustu.identification.ui.main.library.peoplemagnage.mvp.PersionPresenter;
import com.iustu.identification.ui.main.library.peoplemagnage.mvp.PersionView;
import com.iustu.identification.ui.widget.dialog.EditDialog;
import com.iustu.identification.ui.widget.dialog.NormalDialog;
import com.iustu.identification.ui.widget.dialog.SingleButtonDialog;
import com.iustu.identification.util.ExceptionUtil;
import com.iustu.identification.util.FileCallBack;
import com.iustu.identification.util.ImageUtils;
import com.iustu.identification.util.LibManager;
import com.iustu.identification.util.PageSetHelper;
import com.iustu.identification.util.ToastUtil;

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
    private static final String KEY_FACE_SET_ID = "face set id";
    private static final String KEY_FACE_SET_INDEX = "face set index";

    @BindView(R.id.people_manage_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.page_tv)
    TextView pageTv;

    private List<PersonInfo> mPersonList;
    private PersonInfoAdapter mAdapter;
    private PageSetHelper pageSetHelper;
    private PersionPresenter presenter;

    private String faceSetId;
    private int faceSetIndex;

    private int totalPage;
    private int page;
    private boolean isOnLoadMore;

    private int addPhotoIndex;
    private int addPhotoPosition;

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
    }

    private void loadData(int page){
        if(page != 0 && page >= totalPage){
            return;
        }
        Api.getPeopleList(faceSetId, page)
                .doOnSubscribe(d->{
                    addDisposable(d);
                    isOnLoadMore = true;
                    if(page == 0){
                        ((MainActivity)mActivity).showWaitDialog("正在加载", v -> ((LibraryFragment)getParentFragment()).switchFragment(LibraryFragment.ID_LIBRARIES_MANAGE));
                        mPersonList.clear();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(peopleListResponseMessage -> {
                    ((MainActivity)mActivity).dismissWaiDialog();
                    if(peopleListResponseMessage.getCode() != Message.CODE_SUCCESS){
                        onLoadError("错误码(" + peopleListResponseMessage.getCode() + ")");
                    }else {
                        isInitData = true;
                    }
                    if(peopleListResponseMessage.getBody() != null) {
                        mPersonList.addAll(peopleListResponseMessage.getBody().getPeoples());
                        for (PersonInfo personInfo : mPersonList) {
                            personInfo.setFaceSetId(faceSetId);
                        }
                        totalPage = peopleListResponseMessage.getBody().getTotalPage();
                        mAdapter.notifyDataChange();
                        pageSetHelper.notifyChange();
                    }
                    isOnLoadMore = false;
                }, t->{
                    ((MainActivity)mActivity).dismissWaiDialog();
                    ExceptionUtil.getThrowableMessage(t);
                    isOnLoadMore = false;
                    if(page == 0) {
                        onLoadError("无法连接服务器");
                    }
                });
    }

    public void onLoadError(String extra){
        new NormalDialog.Builder()
                .title("错误")
                .content("加载失败," + extra)
                .positive("重试", v->loadData(0))
                .negative("返回", v->((LibraryFragment)getParentFragment()).switchFragment(LibraryFragment.ID_LIBRARIES_MANAGE))
                .show(mActivity.getFragmentManager());
    }

    @Override
    public void onHide() {
        super.onHide();
        isOnLoadMore = false;
        pageSetHelper.setPage(1);
        mPersonList.clear();
        mAdapter.notifyDataChange();
    }

    @Override
    public void onShow() {
        Bundle bundle = getArguments();
        if(bundle != null){
            faceSetId = bundle.getString(KEY_FACE_SET_ID, null);
            faceSetIndex = bundle.getInt(KEY_FACE_SET_INDEX, -1);
        }
        if(faceSetId == null || faceSetIndex == -1){
            onArgsError();
            return;
        }
        page = 0;
        loadData(0);
    }

    private void onArgsError() {
        new SingleButtonDialog.Builder()
                .title("提示")
                .content("参数错误")
                .cancelable(false)
                .button("返回", v-> ((LibraryFragment)getParentFragment()).switchFragment(LibraryFragment.ID_LIBRARIES_MANAGE))
                .show(mActivity.getFragmentManager());
    }

    public void setArguments(String faceSetId, int index){
        Bundle bundle = getArguments();
        if(bundle == null){
            bundle = new Bundle();
        }
        bundle.putString(KEY_FACE_SET_ID, faceSetId);
        bundle.putInt(KEY_FACE_SET_INDEX, index);
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



    private void deletePhoto(int index, int position, String id, int urlPosition){
        PersonInfo personInfo = mPersonList.get(index);
        Api.delFace(faceSetId, personInfo.getId(), id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    if (message.getCode() == Message.CODE_SUCCESS) {
                        mPersonList.get(index).deletePhoto(urlPosition);
                        mAdapter.notifyItemChanged(position);
                        Library library = LibManager.getLibraryList().get(faceSetIndex);
                        library.setCount(library.getCount() - 1);
                        ToastUtil.show("删除照片成功");
                    } else {
                        ExceptionUtil.toastOptFail("删除照片失败", message);
                    }
                }, throwable -> ExceptionUtil.toastServerError());
    }

    private void deletePerson(int index) {
        Api.delPeople(mPersonList.get(index).getId(), faceSetId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(messageResp -> {
                    if(messageResp.getCode() == Message.CODE_SUCCESS){
                        mPersonList.remove(index);
                        mAdapter.notifyDataChange();
                        pageSetHelper.notifyChange();
                        ToastUtil.show("删除成功!");
                    }else {
                        ToastUtil.show("删除失败!");
                    }
                }, t->{
                    ExceptionUtil.getThrowableMessage(t);
                    ExceptionUtil.toastServerError();
                });
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
            int degree = ImageUtils.readPictureDegree(path);
            if(degree == 0){
                addPhoto(new File(path));
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
                            addPhoto(new File(path));
                        }, t->{
                            ((MainActivity)mActivity).dismissWaiDialog();
                            ToastUtil.show("照片处理失败");
                            ExceptionUtil.getThrowableMessage(AddPersonFragment.class.getSimpleName(), t);
                        });
            }
        }
    }

    public void addPhoto(File file){
        if(addPhotoIndex < 0 || addPhotoIndex > mPersonList.size()){
            ToastUtil.show("无法找到添加对象!");
            return;
        }
        PersonInfo personInfo = mPersonList.get(addPhotoIndex);
        Api.addFace(faceSetId, personInfo.getId(), file)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stringMessage -> {
                    if(stringMessage.getCode() != Message.CODE_SUCCESS){
                        ExceptionUtil.toastOptFail("添加失败", stringMessage);
                    }else {
                        personInfo.addPhoto(stringMessage.getId());
                        mAdapter.notifyItemChanged(addPhotoPosition);
                        Library library = LibManager.getLibraryList().get(faceSetIndex);
                        library.setCount(library.getCount() + 1);
                        ToastUtil.show("添加成功");
                    }
                }, t->{
                    ExceptionUtil.getThrowableMessage(t);
                    ExceptionUtil.toastServerError();
                });
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

    }

    @Override
    public void onInitData() {
        presenter.onInitData();
    }

    @Override
    public void onLoadMore() {
        presenter.onLoadMore();
    }

    @Override
    public void onAddPhoto() {
        presenter.onAddPhoto();
    }

    @Override
    public void onDeletePhoto() {
        presenter.onDeletePhoto();
    }

    @Override
    public void onDeletePer() {
        presenter.onDeletePer();
    }

    @Override
    public void onSaveChange() {
        presenter.onSaveChange();
    }
}

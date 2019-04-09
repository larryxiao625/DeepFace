package com.iustu.identification.ui.main.library.librariesmanage;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.iustu.identification.R;
import com.iustu.identification.entity.Library;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.base.PageRecyclerViewAdapter;
import com.iustu.identification.ui.main.library.LibraryFragment;
import com.iustu.identification.ui.main.library.addpersion.AddPersonFragment;
import com.iustu.identification.ui.main.library.librariesmanage.mvp.LibPresenter;
import com.iustu.identification.ui.main.library.librariesmanage.mvp.LibView;
import com.iustu.identification.ui.main.library.peoplemagnage.PeopleManageFragment;
import com.iustu.identification.ui.widget.dialog.Edit2Dialog;
import com.iustu.identification.ui.widget.dialog.NormalDialog;
import com.iustu.identification.ui.widget.dialog.WaitProgressDialog;
import com.iustu.identification.util.IconFontUtil;
import com.iustu.identification.util.LibManager;
import com.iustu.identification.util.PageSetHelper;
import com.iustu.identification.util.RxUtil;
import com.iustu.identification.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Liu Yuchuan on 2017/11/20.
 * 逻辑分析：
 * 1. 获取所有的人脸库信息，并加载
 * 2. 新增人脸库、修改人脸库(onModifyLib)、删除人脸库(onDelete) 三者的操作在该Fragment中完成
 * 3. 新增人员 的操作直接跳转到人员信息的界面完成，该Fragment并不参与
 *
 * 修改：
 * 1. 获取人脸库信息交给Presenter
 * 2. deleteLib、createNewLib、modifyLib 三者的实现都交由Presenter处理
 */

public class LibrariesManageFragment extends BaseFragment implements LibView, LibrariesManageAdapter.OnLibrariesItemButtonClickedListener{

    @BindView(R.id.libraries_manage_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.page_tv)
    TextView pageTv;
    @BindView(R.id.new_icon_tv)
    TextView newIconTv;

    private WaitProgressDialog waitProgressDialog;

    private LibPresenter presenter;
    private PageSetHelper pageSetHelper;
    private LibrariesManageAdapter mAdapter;
    private List<Library> mLibraryList = new ArrayList<>();

    @Override
    protected int postContentView() {
        return R.layout.fragment_libraries_manage;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        super.initView(savedInstanceState, view);
        //mLibraryList = LibManager.getLibraryList();     // 获取所有的人脸库,这里我们需要修改
        mAdapter = new LibrariesManageAdapter(mLibraryList);
        mAdapter.setOnLibrariesItemButtonClickedListener(this);
        mAdapter.setPageSetHelper(pageSetHelper);
        // RecyclerView的Item点击事件实现更改库名称
        mAdapter.setOnPageItemClickListener((view1, index, position) -> {
            Library library = mLibraryList.get(position);
            new Edit2Dialog.Builder()
                    .title("修改人脸库")
                    .hint1("库名称")
                    .hint2("备注")
                    .content1(library.libName)
                    .content2(library.description)
                    .positive("提交", (v1, layout1, layout2) -> {
                        String name = layout1.getEditText().getText().toString();
                        if(name.trim().equals("")){
                            layout1.setError("库名称不能为空");
                            return false;
                        }
                        modifyLibName(name, layout2.getEditText().getText().toString(), library.libId, position);
                        return true;
                    })
                    .negative("取消", null)
                    .show(mActivity.getFragmentManager());
        });
        mAdapter.setLoadMoreListener(new PageRecyclerViewAdapter.LoadMoreListener() {
            @Override
            public void loadMore() {
                // Presenter 实现
            }
        });
        //mAdapter.setDisplayCountPerPage();
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 10, LinearLayoutManager.HORIZONTAL, false){
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });
        recyclerView.setAdapter(mAdapter);
        pageSetHelper = new PageSetHelper(recyclerView, pageTv);
        IconFontUtil.getDefault().setText(newIconTv, IconFontUtil.ADD);
        // 初始化数据
        // initData();
    }


    @Override
    public void onShow() {
        mLibraryList.clear();
        initData();
        if(mAdapter != null) {
            mAdapter.notifyDataChange();
        }
        if(pageSetHelper != null){
            pageSetHelper.notifyChange();
        }
    }

    // 上一页 的点击事件
    @OnClick(R.id.last_page_iv)
    public void lastPage(){
        if(pageSetHelper != null){
            pageSetHelper.lastPage();
        }
    }

    // 下一页 的点击事件
    @OnClick(R.id.next_page_iv)
    public void nextPage(){
        if(pageSetHelper != null){
            pageSetHelper.nextPage();
        }
    }


    @Override
    public void onImportMany(View v, int index) {

    }

    // 点击新增人员按钮时，直接跳转到对应的Fragment
    // 注意此时库管理界面中库数量也是要变的
    @Override
    public void onNewMember(View v, int index) {
        Library library = mLibraryList.get(index);
        LibraryFragment libraryFragment = (LibraryFragment) getParentFragment();
        ((AddPersonFragment)libraryFragment.getFragment(LibraryFragment.ID_ADD_PERSON))
                .setArguments(library.libId, library.libName);
        libraryFragment.switchFragment(LibraryFragment.ID_ADD_PERSON);
    }

    @Override
    public void onManagePeople(View v, int index, int position) {
        // 切换到人员管理的Fragment
        // 并将数据传递过去
        // 可见ChildFragment的相互切换还是委托给ParentFragment来完成的
        LibraryFragment libraryFragment = (LibraryFragment) getParentFragment();
        PeopleManageFragment peopleManageFragment = (PeopleManageFragment) libraryFragment.getFragment(LibraryFragment.ID_PEOPLE_MANAGE);
        peopleManageFragment.setArguments("", mLibraryList.get(index).libId);
        libraryFragment.switchFragment(LibraryFragment.ID_PEOPLE_MANAGE);
    }

    @Override
    public void onDelete(View v, int index) {
        new NormalDialog.Builder()
                .title("提示")
                .content("确定删除库 " + mLibraryList.get(index).libName + " 吗？")
                .positive("确定", view->{
                    deleteLib(mLibraryList.get(index).libId, index);
                })
                .negative("取消", null)
                .show(mActivity.getFragmentManager());
    }

    // 新增 按钮的点击事件
    @OnClick(R.id.new_lib_layout)
    public void onNewLib(){
        new Edit2Dialog.Builder()
                .title("新增人脸库")
                .hint1("库名称")
                .hint2("备注")
                .positive("提交", (v1, layout1, layout2) -> {
                    String name = layout1.getEditText().getText().toString();
                    if(name.trim().equals("")){
                        layout1.setError("库名称不能为空");
                        return false;
                    }
                    createNewLib(name, layout2.getEditText().getText().toString());
                    return true;
                })
                .negative("取消", null)
                .show(mActivity.getFragmentManager());
    }

    public void deleteLib(int id, int position){
        presenter.onDeleteLib(id, position);
    }

    // 更改库名称
    public void modifyLibName(String newName, String newDes, int libId, int position){
        presenter.onModifyLib(newName, newDes, libId, position);
    }

    public void createNewLib(String name, String des){
        presenter.onCreateNewLib(name, des);
    }

    // 初始加载时进行数据初始化
    public void initData() {
        // presenter
        presenter.onInitData();
    }

    @Override
    public void setPresenter(LibPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void bindData(List<com.iustu.identification.entity.Library> data) {
        mLibraryList.addAll(data);
        mAdapter.notifyDataChange();
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
    public void onError(String message) {
        ToastUtil.show("操作失败:" + message);
    }

    @Override
    public void onSuccess(int type, int position, ContentValues values) {
        switch (type) {
            case TYPE_ADD_LIB:
                ToastUtil.show("添加成功");
                break;
            case TYPE_DELETE_LIB:
                mLibraryList.remove(position);
                mAdapter.notifyDataChange();
                break;
            case TYPE_MODIFY_LIB:
                Library library = mLibraryList.get(position);
                library.libName = values.getAsString("libName");
                library.description = values.getAsString("description");
                mAdapter.notifyDataChange();
        }
    }
}

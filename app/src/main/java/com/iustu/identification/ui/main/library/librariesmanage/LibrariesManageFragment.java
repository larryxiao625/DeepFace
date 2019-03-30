package com.iustu.identification.ui.main.library.librariesmanage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.iustu.identification.R;
import com.iustu.identification.api.Api;
import com.iustu.identification.api.message.Message;
import com.iustu.identification.bean.Library;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.main.library.AddPersonFragment;
import com.iustu.identification.ui.main.library.LibraryFragment;
import com.iustu.identification.ui.main.library.peoplemagnage.PeopleManageFragment;
import com.iustu.identification.ui.widget.dialog.Edit2Dialog;
import com.iustu.identification.ui.widget.dialog.NormalDialog;
import com.iustu.identification.util.ExceptionUtil;
import com.iustu.identification.util.IconFontUtil;
import com.iustu.identification.util.LibManager;
import com.iustu.identification.util.PageSetHelper;
import com.iustu.identification.util.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Liu Yuchuan on 2017/11/20.
 */

public class LibrariesManageFragment extends BaseFragment implements LibrariesManageAdapter.OnLibrariesItemButtonClickedListener{
    @BindView(R.id.libraries_manage_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.page_tv)
    TextView pageTv;
    @BindView(R.id.new_icon_tv)
    TextView newIconTv;

    private PageSetHelper pageSetHelper;
    private LibrariesManageAdapter mAdapter;
    private List<Library> mLibraryList;

    @Override
    protected int postContentView() {
        return R.layout.fragment_libraries_manage;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        super.initView(savedInstanceState, view);
        mLibraryList = LibManager.getLibraryList();
        mAdapter = new LibrariesManageAdapter(mLibraryList);
        mAdapter.setOnLibrariesItemButtonClickedListener(this);
        mAdapter.setOnPageItemClickListener((view1, index, position) -> {
            LibraryFragment libraryFragment = (LibraryFragment) getParentFragment();
            PeopleManageFragment peopleManageFragment = (PeopleManageFragment) libraryFragment.getFragment(LibraryFragment.ID_PEOPLE_MANAGE);
            peopleManageFragment.setArguments(mLibraryList.get(index).getIdOnServer(), index);
            libraryFragment.switchFragment(LibraryFragment.ID_PEOPLE_MANAGE);
        });
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 10, LinearLayoutManager.HORIZONTAL, false){
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });
        recyclerView.setAdapter(mAdapter);
        pageSetHelper = new PageSetHelper(recyclerView, pageTv);
        IconFontUtil.getDefault().setText(newIconTv, IconFontUtil.ADD);
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

    @OnClick(R.id.last_page_iv)
    public void lastPage(){
        if(pageSetHelper != null){
            pageSetHelper.lastPage();
        }
    }

    @OnClick(R.id.next_page_iv)
    public void nextPage(){
        if(pageSetHelper != null){
            pageSetHelper.nextPage();
        }
    }


    @Override
    public void onLock(View v, int index) {

    }

    @Override
    public void onNewMember(View v, int index) {
        Library library = mLibraryList.get(index);
        LibraryFragment libraryFragment = (LibraryFragment) getParentFragment();
        ((AddPersonFragment)libraryFragment.getFragment(LibraryFragment.ID_ADD_PERSON))
                .setArguments(library.getName(), library.getIdOnServer(), index);
        libraryFragment.switchFragment(LibraryFragment.ID_ADD_PERSON);
    }

    @Override
    public void onModifyLib(View v, int index, int position) {
        Library library = mLibraryList.get(index);
        new Edit2Dialog.Builder()
                .title("修改人脸库")
                .hint1("库名称")
                .hint2("备注")
                .content1(library.getName())
                .content2(library.getRemark())
                .positive("提交", (v1, layout1, layout2) -> {
                    String name = layout1.getEditText().getText().toString();
                    if(name.trim().equals("")){
                        layout1.setError("库名称不能为空");
                        return false;
                    }
                    modifyLib(name, layout2.getEditText().getText().toString(), library.getIdOnServer(), index, position);
                    return true;
                })
                .negative("取消", null)
                .show(mActivity.getFragmentManager());
    }

    @Override
    public void onDelete(View v, int index) {
        new NormalDialog.Builder()
                .title("提示")
                .content("确定删除库 " + mLibraryList.get(index).getName() + " 吗？")
                .positive("确定", view->{
                    deleteLib(mLibraryList.get(index).getIdOnServer(), index);
                })
                .negative("取消", null)
                .show(mActivity.getFragmentManager());
    }

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

    public void deleteLib(String id, int index){
        Api.destroyFaceSet(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    if(message.getCode() != Message.CODE_SUCCESS){
                        ToastUtil.show("删除失败！");
                    }else {
                        mLibraryList.remove(index);
                        mAdapter.notifyDataChange();
                        pageSetHelper.notifyChange();
                        ToastUtil.show("删除成功!");
                    }
                }, t->{
                    ExceptionUtil.getThrowableMessage(t);
                    ExceptionUtil.toastServerError();
                });
    }

    public void modifyLib(String name, String remark, String id, int index, int position){
        Api.modifyFaceSet(remark, name, id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    if(message.getCode() != Message.CODE_SUCCESS){
                        ToastUtil.show("修改失败！");
                    }else {
                        Library library = mLibraryList.get(index);
                        library.setName(name);
                        library.setRemark(remark);
                        mAdapter.notifyItemChanged(position);
                        ToastUtil.show("修改成功!");
                        LibManager.getIdNameMap().put(library.getIdOnServer(), library.getName());
                    }
                }, t->{
                    ExceptionUtil.getThrowableMessage(t);
                    ExceptionUtil.toastServerError();
                });
    }

    public void createNewLib(String name, String remark){
        Api.createFaceSet(name, remark)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::addDisposable)
                .subscribe(stringMessage -> {
                    if(stringMessage.getCode() != Message.CODE_SUCCESS){
                        ToastUtil.show("创建失败！");
                    }else {
                        Library library = new Library();
                        library.setInUse(false);
                        library.setName(name);
                        library.setId(mLibraryList.size());
                        library.setCount(0);
                        library.setIdOnServer(stringMessage.getBody());
                        library.setLock(false);
                        mLibraryList.add(library);
                        mAdapter.notifyDataChange();
                        pageSetHelper.notifyChange();
                        ToastUtil.show("创建成功!");
                    }
                }, t->{
                    ExceptionUtil.getThrowableMessage(t);
                    ExceptionUtil.toastServerError();
                });
    }
}

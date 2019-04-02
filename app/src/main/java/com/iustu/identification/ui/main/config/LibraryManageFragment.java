package com.iustu.identification.ui.main.config;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.iustu.identification.R;
import com.iustu.identification.bean.Library;
import com.iustu.identification.config.LibraryConfig;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.util.LibManager;
import com.iustu.identification.util.PageSetHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Liu Yuchuan on 2017/11/5.
 */

public class LibraryManageFragment extends BaseFragment {
    @BindView(R.id.library_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.page_tv)
    TextView pageTv;

    private LibraryManagerAdapter mAdapter;
    private PageSetHelper pageSetHelper;
    private LibraryConfig libraryConfig;

    private List<String> mChooseList;
    private List<Library> mLibraryList;

    @Override
    protected int postContentView() {
        return R.layout.fragment_library_manage;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        super.initView(savedInstanceState, view);
        libraryConfig = LibraryConfig.getInstance();
        mChooseList = libraryConfig.getChosenLibs();
        mLibraryList = LibManager.getLibraryList();
        for (Library library : mLibraryList) {
            if(mChooseList.contains(library.getIdOnServer())){
                library.setInUse(true);
            }else {
                library.setInUse(false);
            }
        }
        mAdapter = new LibraryManagerAdapter(mLibraryList);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, mAdapter.getDisplayCountPerPage(), LinearLayoutManager.HORIZONTAL,false ){
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });
        mAdapter.setOnPageItemClickListener((v, index, position)->{
            Library library = mLibraryList.get(index);
            if(library.isInUse()){
                library.setInUse(false);
                mChooseList.remove(library.getIdOnServer());
                Log.e(getClass().getSimpleName(), "remove " + library.getIdOnServer() + " " + mChooseList);
            }else {
                library.setInUse(true);
                mChooseList.add(library.getIdOnServer());
                Log.e(getClass().getSimpleName(), "add " + library.getIdOnServer() + " " + mChooseList);
            }
            mAdapter.notifyItemChanged(position);
        });
        recyclerView.setAdapter(mAdapter);
        pageSetHelper = new PageSetHelper(recyclerView, pageTv);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(libraryConfig != null){
            libraryConfig.save();
        }
    }

    @Override
    public void onShow() {
        if(mAdapter != null){
            mAdapter.notifyDataChange();
        }
        if(pageSetHelper != null){
            pageSetHelper.notifyChange();
        }
    }

    @Override
    public void onHide() {
        super.onHide();
        if(libraryConfig != null){
            libraryConfig.save();
        }
    }

    @OnClick(R.id.last_page_iv)
    public void lastPage(){
        pageSetHelper.lastPage();
    }

    @OnClick(R.id.next_page_iv)
    public void nextPage(){
        pageSetHelper.nextPage();
    }
}

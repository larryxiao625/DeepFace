package com.iustu.identification.util;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.iustu.identification.ui.base.PageRecyclerViewAdapter;

import java.util.Locale;

/**
 * Created by Liu Yuchuan on 2017/11/20.
 */

public class PageSetHelper {
    private TextView mPageTv;
    private PageRecyclerViewAdapter mAdapter;
    private static final String FORMAT_PAGE = "%d/%d";

    public PageSetHelper(RecyclerView recyclerView, TextView textView){
        mPageTv = textView;
        mAdapter = (PageRecyclerViewAdapter) recyclerView.getAdapter();
        mPageTv.setText(String.format(Locale.ENGLISH, FORMAT_PAGE, mAdapter.getPageNow(), mAdapter.getPageMax()));
    }

    public void nextPage(){
        mAdapter.nextPage();
        notifyChange();
    }

    public void lastPage(){
        mAdapter.lastPage();
        notifyChange();
    }

    public void setPage(int page){
        mAdapter.setPageNow(page);
        notifyChange();
    }

    public void notifyChange(){
        mPageTv.setText(String.format(Locale.ENGLISH, FORMAT_PAGE, mAdapter.getPageNow(), mAdapter.getPageMax()));
    }
}

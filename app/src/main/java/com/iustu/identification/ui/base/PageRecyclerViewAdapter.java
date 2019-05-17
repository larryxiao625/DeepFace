package com.iustu.identification.ui.base;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iustu.identification.util.PageSetHelper;

import java.util.List;

/**
 * Created by Liu Yuchuan on 2017/11/20.
 */

public abstract class PageRecyclerViewAdapter<VH extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<VH> {
    private int pageNow;
    private int pageMax;
    private int displayCountPerPage;
    private PageSetHelper pageSetHelper;

    protected final List<T> mDataLast;

    public PageRecyclerViewAdapter(List<T> dataLast) {
        this.mDataLast = dataLast;
        pageNow = 1;
        setDisplayCountPerPage(10);
    }

    @Override
    public final void onBindViewHolder(@NonNull VH holder, int position) {
        // 展示的内容关系到pageNow的值
        // 所有更改pageNow的值需要调用notifyDataSetChange没毛病
        int index = position + (pageNow - 1) * displayCountPerPage;
        if(index >= 0 && index < mDataLast.size()) {
            onBindHolder(holder, index, position);
        }
    }

    public abstract void onBindHolder(VH holder, int index, int position);

    @Override
    public int getItemCount() {
        return pageNow < pageMax ? displayCountPerPage : mDataLast.size() - (pageMax - 1) * displayCountPerPage;
    }

    public int getPageNow() {
        return pageNow;
    }

    public void setPageNow(int pageNow) {
        this.pageNow = pageNow;
        notifyDataSetChanged();
    }

    public void nextPage(){
        if(pageNow >= pageMax){
            return;
        }
//        loadMoreListener.loadMore();
        setPageNow(pageNow+1);
    }

    public void lastPage(){
        if(pageNow <= 1) {
            return;
        }
        setPageNow(pageNow-1);
    }

    public int getPageMax() {
        return pageMax;
    }

    public int getDisplayCountPerPage() {
        return displayCountPerPage;
    }

    public void setDisplayCountPerPage(int displayCountPerPage) {
        this.displayCountPerPage = displayCountPerPage;
        pageMax = mDataLast.size() / displayCountPerPage;
        if(mDataLast.size() % displayCountPerPage != 0 || mDataLast.size() == 0){
            pageMax++;
        }
        notifyDataSetChanged();
    }

    // 当插入数据后因为需要管理page
    // 所有插入数据后需要调用该方法，而不是notifyDataSetChanged
    // 同时需要通知PageSetHelper更改显示的pagemax
    public void notifyDataChange(){
        int oldPageMax = pageMax;
        pageMax = mDataLast.size() / displayCountPerPage;
        if(mDataLast.size() % displayCountPerPage != 0 || mDataLast.size() == 0){
            pageMax++;
        }
        if(pageMax < oldPageMax){
            pageNow = 1;
        }
        if (pageSetHelper != null)
            pageSetHelper.notifyChange();       // 通知SetHelper是否需要重新显示pageMax
        notifyDataSetChanged();
    }

    public void setPageSetHelper(PageSetHelper helper) {
        this.pageSetHelper = helper;
    }

    private LoadMoreListener loadMoreListener;

    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public interface LoadMoreListener{
        void loadMore();
    }

    public boolean inCurrentPage(int index){
        int startIndex = displayCountPerPage * (pageNow - 1);
        int endIndex = displayCountPerPage * pageNow;
        return index >= startIndex && index <= endIndex;
    }

    public int calculatePosition(int index){
        return index - displayCountPerPage * (pageNow - 1);
    }
}
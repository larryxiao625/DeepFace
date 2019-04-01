package com.iustu.identification.ui.main.library.librariesmanage;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iustu.identification.R;
import com.iustu.identification.bean.Library;
import com.iustu.identification.ui.base.OnPageItemClickListener;
import com.iustu.identification.ui.base.PageRecyclerViewAdapter;
import com.iustu.identification.util.IconFontUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Liu Yuchuan on 2017/11/13.
 */

public class LibrariesManageAdapter extends PageRecyclerViewAdapter<LibrariesManageAdapter.Holder, Library> {
    private boolean isAdmin;

    public LibrariesManageAdapter(List<Library> dataLast) {
        super(dataLast);
    }

    @Override
    public void onBindHolder(Holder holder, int index, int position) {
        Library library = mDataLast.get(index);
        holder.id.setText(String.valueOf(library.getId()));
        holder.name.setText(library.getName());
        holder.number.setText(String.valueOf(library.getCount()));
        IconFontUtil.getDefault().setText(holder.select, IconFontUtil.UNSELECT_SINGLE);
        /*
        if(isAdmin){
            holder.lock.setVisibility(View.VISIBLE);
            if(library.isLock()) {
                IconFontUtil.getDefault().setText(holder.lock, IconFontUtil.LOCK);
            }else {
                IconFontUtil.getDefault().setText(holder.lock, IconFontUtil.UNLOCK);
            }
        }else {
            holder.lock.setVisibility(View.INVISIBLE);
        }
        */
        IconFontUtil.getMyDefault().setText(holder.batch, IconFontUtil.PEOPLE_IMPORT);
        IconFontUtil.getDefault().setText(holder.edit, IconFontUtil.ALTER);
        IconFontUtil.getDefault().setText(holder.add, IconFontUtil.MEMBER_NEW);
        IconFontUtil.getDefault().setText(holder.delete, IconFontUtil.DELETE);

        holder.itemView.setOnClickListener(v -> {
            if(onPageItemClickListener != null){
                onPageItemClickListener.onClick(v, index, position);
            }
        });

        holder.batch.setOnClickListener(v -> {
            if(isAdmin && onLibrariesItemButtonClickedListener != null){
                onLibrariesItemButtonClickedListener.onImportMany(v, index);
            }
        });
        holder.add.setOnClickListener(v -> {
            if(onLibrariesItemButtonClickedListener != null){
                onLibrariesItemButtonClickedListener.onNewMember(v, index);
            }
        });
        holder.edit.setOnClickListener(v -> {
            if(onLibrariesItemButtonClickedListener != null){
                onLibrariesItemButtonClickedListener.onManagePeople(v, index, position);
            }
        });
        holder.delete.setOnClickListener(v -> {
          if(onLibrariesItemButtonClickedListener != null){
              onLibrariesItemButtonClickedListener.onDelete(v, index);
          }
        });
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_libararies_manage, parent, false);
        return new Holder(view);
    }

    static class Holder extends RecyclerView.ViewHolder{
        @BindView(R.id.lib_id_tv)
        TextView id;
        @BindView(R.id.lib_name_tv)
        TextView name;
        @BindView(R.id.lib_number_tv)
        TextView number;
        @BindView(R.id.select_tv)
        TextView select;
        @BindView(R.id.lib_batch_tv)
        TextView batch;
        @BindView(R.id.lib_add_tv)
        TextView add;
        @BindView(R.id.lib_edit_tv)
        TextView edit;
        @BindView(R.id.lib_delete_tv)
        TextView delete;

        Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    private boolean isSelectAll(){
        for (Library library : mDataLast) {
            if(!library.isInUse()){
                return false;
            }
        }
        return true;
    }

    // 该接口代表的是每个item后面的四个操作的点击事件
    // 而不是RecyclerView中每个item的点击事件
    // 名字具有迷惑性，不要搞错了
    public interface OnLibrariesItemButtonClickedListener {
        void onImportMany(View v, int index);
        void onNewMember(View v, int index);
        void onManagePeople(View v, int index, int position);
        void onDelete(View v, int index);
    }

    private OnLibrariesItemButtonClickedListener onLibrariesItemButtonClickedListener;

    // RecyclerView的item的点击事件，目前没有使用
    public void setOnLibrariesItemButtonClickedListener(OnLibrariesItemButtonClickedListener onLibrariesItemButtonClickedListener) {
        this.onLibrariesItemButtonClickedListener = onLibrariesItemButtonClickedListener;
    }

    private OnPageItemClickListener onPageItemClickListener;

    public void setOnPageItemClickListener(OnPageItemClickListener onPageItemClickListener) {
        this.onPageItemClickListener = onPageItemClickListener;
    }
}

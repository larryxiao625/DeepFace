package com.iustu.identification.ui.main.config.libmanager;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iustu.identification.R;
import com.iustu.identification.entity.Library;
import com.iustu.identification.ui.base.OnPageItemClickListener;
import com.iustu.identification.ui.base.PageRecyclerViewAdapter;
import com.iustu.identification.util.IconFontUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Liu Yuchuan on 2017/11/13.
 */

public class LibraryManagerAdapter extends PageRecyclerViewAdapter<LibraryManagerAdapter.Holder, com.iustu.identification.entity.Library>{

    public LibraryManagerAdapter(List<com.iustu.identification.entity.Library> dataLast) {
        super(dataLast);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_libarary_database, parent, false);
        return new Holder(view);
    }
    @Override
    public void onBindHolder(Holder holder, int index, int position) {
        Library library = mDataLast.get(index);
        holder.id.setText(String.valueOf(index + 1));
        if(library.inUsed == 0) {
            IconFontUtil.getDefault().setText(holder.select, IconFontUtil.UNSELECT_SQUAD);
            holder.state.setText("未使用");
        }else {
            IconFontUtil.getDefault().setText(holder.select, IconFontUtil.SELECT_ALL_SQUAD);
            holder.state.setText("正在使用");
        }
        holder.name.setText(library.libName);
        holder.number.setText(String.valueOf(library.count));
        holder.itemView.setOnClickListener(v -> {
            if(onPageItemClickListener != null){
                onPageItemClickListener.onClick(v, position, index);
            }
        });
    }


    static class Holder extends RecyclerView.ViewHolder{
        @BindView(R.id.lib_id_tv)
        TextView id;
        @BindView(R.id.lib_name_tv)
        TextView name;
        @BindView(R.id.lib_number_tv)
        TextView number;
        @BindView(R.id.lib_state_tv)
        TextView state;
        @BindView(R.id.select_tv)
        TextView select;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private OnPageItemClickListener onPageItemClickListener;

    public void setOnPageItemClickListener(OnPageItemClickListener onPageItemClickListener) {
        this.onPageItemClickListener = onPageItemClickListener;
    }
}
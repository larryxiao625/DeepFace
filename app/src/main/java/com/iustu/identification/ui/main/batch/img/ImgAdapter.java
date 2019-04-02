package com.iustu.identification.ui.main.batch.img;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iustu.identification.App;
import com.iustu.identification.R;
import com.iustu.identification.bean.BatchCompareImg;
import com.iustu.identification.ui.base.OnItemClickListener;
import com.iustu.identification.util.IconFontUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liu Yuchuan on 2017/11/20.
 */

public class ImgAdapter extends RecyclerView.Adapter<ImgAdapter.Holder> {
    private final List<BatchCompareImg> mImgList = new ArrayList<>();

    public ImgAdapter(List<BatchCompareImg> imgList) {
        mImgList.clear();
        mImgList.addAll(imgList);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_batch_compare, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        BatchCompareImg img = mImgList.get(position);
        if(img.isChoose()){
            holder.choose.setTextColor(App.getContext().getResources().getColor(R.color.lite_green));
            IconFontUtil.getDefault().setText(holder.choose, IconFontUtil.SELECT_SINGLE);
        }else {
            holder.choose.setTextColor(App.getContext().getResources().getColor(R.color.white));
            IconFontUtil.getDefault().setText(holder.choose, IconFontUtil.UNSELECT_SINGLE);
        }
        Glide.with(holder.img)
                .load(Uri.fromFile(new File(img.getPath())))
                .into(holder.img);
        holder.itemView.setOnClickListener(v -> {
            if(onItemClickListener != null){
                onItemClickListener.onClick(v, position);
            }
        });
    }

    public void clear(){
        mImgList.clear();
        notifyDataSetChanged();
    }

    public void setImgList(List<BatchCompareImg> ImgList) {
        mImgList.clear();
        mImgList.addAll(ImgList);
    }

    @Override
    public int getItemCount() {
        return mImgList == null ? 0 : mImgList.size();
    }

    static class Holder extends RecyclerView.ViewHolder{
        private View itemView;
        private ImageView img;
        private TextView choose;
        Holder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.img = itemView.findViewById(R.id.batch_img_iv);
            this.choose = itemView.findViewById(R.id.choose_check_box_tv);
        }
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public boolean isSelectAll(){
        for (BatchCompareImg img : mImgList) {
            if(!img.isChoose()){
                return false;
            }
        }
        return true;
    }
}

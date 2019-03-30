package com.iustu.identification.ui.main.history.face;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.iustu.identification.R;
import com.iustu.identification.bean.FaceCollectItem;
import com.iustu.identification.ui.base.OnPageItemClickListener;
import com.iustu.identification.ui.base.PageRecyclerViewAdapter;
import com.iustu.identification.util.ImageUtils;
import com.iustu.identification.util.TextUtil;

import java.util.List;

/**
 * Created by Liu Yuchuan on 2017/11/22.
 */

public class FaceCollectItemAdapter extends PageRecyclerViewAdapter<FaceCollectItemAdapter.Holder, FaceCollectItem> {

    private OnPageItemClickListener onPageItemClickListener;

    public FaceCollectItemAdapter(List<FaceCollectItem> dataLast) {
        super(dataLast);
        setDisplayCountPerPage(12);
    }

    @Override
    public void onBindHolder(Holder holder, int index, int position) {
        if(index >= mDataLast.size()){
            return;
        }
        FaceCollectItem item = mDataLast.get(index);
        // TODO: 2018/1/19 处理时间字符串
        String [] time = item.getTime().split("T");
        holder.timeTv.setText(TextUtil.fromHtml(time[0] + "<br><font><small>" + time[1] + "</small></font>"));
        Glide.with(holder.faceImg)
                .setDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.photo_holder).error(R.drawable.photo_holder))
                .load(item.getImgUrl())
                .into(holder.faceImg);
        holder.itemView.setOnClickListener(v -> {
            if(onPageItemClickListener != null){
                onPageItemClickListener.onClick(v, position, index);
            }
        });
    }

    public void setOnPageItemClickListener(OnPageItemClickListener onPageItemClickListener) {
        this.onPageItemClickListener = onPageItemClickListener;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_face_collect, parent, false);
        return new Holder(view);
    }

    static class Holder extends RecyclerView.ViewHolder{
        TextView timeTv;
        ImageView faceImg;
        Holder(View itemView) {
            super(itemView);
            timeTv = itemView.findViewById(R.id.date_time_tv);
            faceImg = itemView.findViewById(R.id.face_collect_iv);
            timeTv.setBackgroundColor(Color.argb(122, 24, 38, 67));
        }
    }
}

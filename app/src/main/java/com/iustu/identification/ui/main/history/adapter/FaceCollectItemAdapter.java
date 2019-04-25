package com.iustu.identification.ui.main.history.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.iustu.identification.R;
import com.iustu.identification.bean.FaceCollectItem;
import com.iustu.identification.bean.ParameterConfig;
import com.iustu.identification.ui.base.OnPageItemClickListener;
import com.iustu.identification.ui.base.PageRecyclerViewAdapter;
import com.iustu.identification.util.DataCache;
import com.iustu.identification.util.TextUtil;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by Liu Yuchuan on 2017/11/22.
 */

public class FaceCollectItemAdapter extends PageRecyclerViewAdapter<FaceCollectItemAdapter.Holder, FaceCollectItem> {

    private ParameterConfig config = DataCache.getParameterConfig();
    private OnPageItemClickListener onPageItemClickListener;

    public FaceCollectItemAdapter(List<FaceCollectItem> dataLast) {
        super(dataLast);
        setDisplayCountPerPage(config.getDisplayCount());
    }

    public interface FaceItemClickListener {
        void lookOriginal(int position);
    }

    private FaceItemClickListener itemClickListener;

    public void setItemClickListener(FaceItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onBindHolder(Holder holder, int index, int position) {
        if(index >= mDataLast.size()){
            return;
        }
        FaceCollectItem item = mDataLast.get(index);
        holder.setFaceCollectionItem(item);
//        holder.itemView.setOnClickListener(v -> {
//            if(onPageItemClickListener != null){
//                onPageItemClickListener.onClick(v, position, index);
//            }
//        });
        holder.faceImg.setOnClickListener( v -> {
            Log.d("FaceCollectClick", String.valueOf(itemClickListener));
            if (itemClickListener != null)
                itemClickListener.lookOriginal(index);
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

        public void setFaceCollectionItem(FaceCollectItem faceCollectionItem) {
            timeTv.setText(faceCollectionItem.getTime() + "\n" + faceCollectionItem.getHourTime());
            Glide.with(itemView.getContext())
                    .setDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.photo_holder).error(R.drawable.photo_holder).dontAnimate())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .load(new File(faceCollectionItem.getImgUrl()))
                    .into(faceImg);
        }
    }
}

package com.iustu.identification.ui.main.camera.adapter;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.iustu.identification.R;
import com.iustu.identification.entity.CompareRecord;
import com.iustu.identification.ui.widget.ScaleView;
import com.iustu.identification.util.ExpandableViewHoldersUtil;
import com.iustu.identification.util.ImageUtils;
import com.iustu.identification.util.LibManager;
import com.iustu.identification.util.TextUtil;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Liu Yuchuan on 2017/12/7.
 */

public class CompareItemAdapter extends RecyclerView.Adapter<CompareItemAdapter.Holder>{
    private List<CompareRecord> searchCompareItemList;

    public CompareItemAdapter(List<CompareRecord> searchCompareItemList) {
        this.searchCompareItemList = searchCompareItemList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_compare, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        CompareRecord item = searchCompareItemList.get(position);
        holder.setCompareRecord(item);
        if(item.isExtend()){
            holder.moreInfoLayout.setVisibility(View.VISIBLE);
        }else {
            holder.moreInfoLayout.setVisibility(View.GONE);
        }
        holder.foldButton.setOnClickListener(v -> {
            if(item.isExtend()){
                item.setExtend(false);
                ExpandableViewHoldersUtil.rotateExpandIcon(holder.foldImage,180,0);
                ExpandableViewHoldersUtil.collapseHolder(holder, 300,0,true);
            }else {
                item.setExtend(true);
                ExpandableViewHoldersUtil.rotateExpandIcon(holder.foldImage,0,180);
                ExpandableViewHoldersUtil.expandHolder(holder,0,300,true);
            }
        });
    }

    private void loadInfo(int index){
        CompareRecord item = searchCompareItemList.get(index);
    }

    public void updateSingleData(CompareRecord searchCompareItem,int position){
        searchCompareItemList.add(position,searchCompareItem);
        notifyItemChanged(0);
    }

    @Override
    public int getItemCount() {
        return searchCompareItemList == null? 0 : searchCompareItemList.size();
    }

    public static class Holder extends RecyclerView.ViewHolder{
        @BindView(R.id.photo_capture_iv)
        ImageView capturePhoto;
        @BindView(R.id.photo_match_iv)
        ImageView matchPhoto;
        @BindView(R.id.compare_scv)
        ScaleView compareScaleView;
        @BindView(R.id.more_info_ll)
        LinearLayout moreInfoLayout;
        @BindView(R.id.people_library_tv)
        TextView libNameTv;
        @BindView(R.id.name_tv)
        TextView nameTv;
        @BindView(R.id.gender_tv)
        TextView genderTv;
        @BindView(R.id.birthday_tv)
        TextView birthTv;
        @BindView(R.id.id_card_tv)
        TextView idCardTv;
        @BindView(R.id.location_tv)
        TextView locationTv;
        @BindView(R.id.fold_fm)
        FrameLayout foldButton;
        @BindView(R.id.fold_icon_iv)
        ImageView foldImage;
        @BindView(R.id.compare_time_tv)
        TextView compareTimeTv;
        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("SetTextI18n")
        private void setCompareRecord(CompareRecord info){
            if(info==null){

            }else if(info.getPhotoPath()==null){

            } else {
                Glide.with(itemView).asBitmap()
                        .load(new File(info.getUploadPhoto()))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .format(DecodeFormat.PREFER_RGB_565)
                        .into(capturePhoto);
                String[] photos = info.getPhotoPath().split(";");
                String libPath = "/sdcard/DeepFace/" + info.getLibName()+ "/" + photos[0];
                Log.d("libPath",libPath);
                Glide.with(itemView).load(BitmapFactory.decodeFile(libPath))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(matchPhoto);
                libNameTv.setText(TextUtil.format(String.valueOf(info.getLibName())));
                nameTv.setText(TextUtil.format(info.getName()));
                birthTv.setText(TextUtil.format(info.getBirthday()));
                idCardTv.setText(TextUtil.format(info.getIdentity()));
                locationTv.setText(TextUtil.format(info.getHome()));
                genderTv.setText(TextUtil.format(info.getGender()));
                compareScaleView.setScale((int) (info.getRate()*100));
                compareTimeTv.setText(info.getTime()+"\n"+info.getHourTime());
            }
        }
    }
}

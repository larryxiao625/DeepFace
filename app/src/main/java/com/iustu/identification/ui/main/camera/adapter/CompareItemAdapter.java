package com.iustu.identification.ui.main.camera.adapter;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iustu.identification.R;
import com.iustu.identification.entity.CompareRecord;
import com.iustu.identification.entity.PersonInfo;
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
        // TODO: 2019/4/9 设置图片加载方法
//        if(item.getWidth() == 0) {
//            Glide.with(holder.capturePhoto)
//                    .asBitmap()
//                    .load(Uri.fromFile(new File(item.getPhotoPath())))
//                    .into(new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//                            if(resource.isRecycled()){
//                                return;
//                            }
//                            item.setWidth(resource.getWidth());
//                            item.setHeight(resource.getHeight());
//                            Glide.with(holder.capturePhoto)
//                                    .load(Uri.fromFile(new File(item.getPhotoPath())))
//                                    .apply(new RequestOptions().transforms(new ImageUtils.CropFace(item.getWidth(), item.getHeight(), item.getRect())).placeholder(R.drawable.photo_holder).error(R.drawable.photo_holder))
//                                    .into(holder.capturePhoto);
//                        }
//                    });
//        }else {
//            Glide.with(holder.capturePhoto)
//                    .load(Uri.fromFile(new File(item.getPhotoPath())))
//                    .apply(new RequestOptions().transforms(new ImageUtils.CropFace(item.getWidth(), item.getHeight(), item.getRect())).placeholder(R.drawable.photo_holder).error(R.drawable.photo_holder))
//                    .into(holder.capturePhoto);
//        }

//        holder.compareScaleView.setScale((int) (item.getScore() * 100));
//        if(!item.isInitInfo()){
//            holder.setPersonInfo(null);
//            loadInfo(position);
//        }else {
//            PersonInfo personInfo = item.getPersonInfo();
//            holder.setPersonInfo(item.getPersonInfo());
//            if(personInfo.isInitUrls()) {
//                Glide.with(holder.matchPhoto)
//                        .applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.photo_holder))
//                        .load(personInfo.getUrlAt(0))
//                        .into(holder.matchPhoto);
//            }else {
//                loadPicUrl(position);
//            }

//            holder.setPersonInfo(item.getPersonInfo());
//        }
//
//        Glide.with(holder.matchPhoto)
//                .applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.photo_holder).error(R.drawable.photo_holder))
//                .load(item.getPhotoUrl())
//                .into(holder.matchPhoto);


        if(item.isExtend()){
            holder.moreInfoLayout.setVisibility(View.VISIBLE);
            holder.foldImage.setImageResource(R.drawable.xiangs1);
        }else {
            holder.moreInfoLayout.setVisibility(View.GONE);
            holder.foldImage.setImageResource(R.drawable.xiangs2);
        }
        holder.foldButton.setOnClickListener(v -> {
            if(item.isExtend()){
                item.setExtend(false);
                ExpandableViewHoldersUtil.rotateExpandIcon(holder.foldImage,0,180);
                ExpandableViewHoldersUtil.collapseHolder(holder, 300,0,true);
            }else {
                item.setExtend(true);
                ExpandableViewHoldersUtil.rotateExpandIcon(holder.foldImage,180,0);
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
        @BindView(R.id.nationality_tv)
        TextView nationalityTv;
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

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void setCompareRecord(CompareRecord info){
            if(info == null){

            }else {
                // TODO: 2019/4/9 根据人脸库获取中文名方法
                Glide.with(itemView).load(new File(info.getUploadPhoto())).into(capturePhoto);
                String[] photos = info.getPhotoPath().split(";");
<<<<<<< HEAD
                String libPath = "/sdcard/DeepFace/" + info.getLibName()+ "/" + photos[0];
=======
                String libPath = "/sdcard/DeepFace/" + info.getLibName()+"/" + photos[0];
>>>>>>> 19adfaed756e84018d5d4b861ce389d5cc210e85
                Glide.with(itemView).load(new File(libPath)).into(matchPhoto);
                libNameTv.setText(TextUtil.format(String.valueOf(info.getLibName())));
                nameTv.setText(TextUtil.format(info.getName()));
                birthTv.setText(TextUtil.format(info.getGender()));
                idCardTv.setText(TextUtil.format(info.getIdentity()));
                locationTv.setText(TextUtil.format(info.getHome()));
                nationalityTv.setText(TextUtil.format(info.getHome()));
                compareScaleView.setScale((int) (info.getRate()*100));
            }
        }
    }
}

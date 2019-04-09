package com.iustu.identification.ui.main.history.adapter;

import android.graphics.Bitmap;
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
import com.iustu.identification.api.message.Message;
import com.iustu.identification.entity.CompareRecord;
import com.iustu.identification.entity.PersonInfo;
import com.iustu.identification.ui.base.PageRecyclerViewAdapter;
import com.iustu.identification.ui.widget.ScaleView;
import com.iustu.identification.util.ExceptionUtil;
import com.iustu.identification.util.ImageUtils;
import com.iustu.identification.util.LibManager;
import com.iustu.identification.util.TextUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Liu Yuchuan on 2017/11/23.
 */

public class CompareHistoryItemAdapter extends PageRecyclerViewAdapter<CompareHistoryItemAdapter.Holder, CompareRecord>{

    private CompositeDisposable compositeDisposable;

    private String targetPhotoUrl;

    public CompareHistoryItemAdapter(List<CompareRecord> dataLast) {
        super(dataLast);
        setDisplayCountPerPage(3);
    }

    public void setTargetPhotoUrl(String targetPhotoUrl){
        this.targetPhotoUrl = targetPhotoUrl;
    }

    @Override
    public void onBindHolder(Holder holder, int index, int position) {
        CompareRecord item = mDataLast.get(index);
        // TODO: 2019/4/9 完成历史记录插入方法
//        if(item.isInitInfo()) {
//            holder.setPersonInfo(item.getPersonInfo());
//            holder.scaleView.setScale((int) (item.getScore() * 100));
//            holder.libName.setText(LibManager.getLibName(item.getFaceSetId()));
//            Glide.with(holder.libPhoto)
//                    .load(item.getPhotoUrl())
//                    .apply(new RequestOptions().placeholder(R.drawable.photo_holder).error(R.drawable.photo_holder))
//                    .into(holder.libPhoto);
//        }else {
//            holder.setPersonInfo(null);
//            loadInfo(index);
//       }
        // TODO: 2019/4/9 完善图片加载方法
//        if(item.getWidth() == 0) {
//            Glide.with(holder.targetPhoto)
//                    .asBitmap()
//                    .load(targetPhotoUrl)
//                    .into(new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//                            if (resource.isRecycled()) {
//                                return;
//                            }
//                            item.setWidth(resource.getWidth());
//                            item.setHeight(resource.getHeight());
//                            Glide.with(holder.targetPhoto)
//                                    .load(targetPhotoUrl)
//                                    .apply(new RequestOptions()
//                                            .transform(new ImageUtils.CropFace(item.getWidth(), item.getHeight(), item.getRect()))
//                                            .placeholder(R.drawable.photo_holder)
//                                            .error(R.drawable.photo_holder))
//                                    .into(holder.targetPhoto);
//                        }
//                    });
//        }else {
//            Glide.with(holder.targetPhoto)
//                    .load(targetPhotoUrl)
//                    .apply(new RequestOptions()
//                            .transform(new ImageUtils.CropFace(item.getWidth(), item.getHeight(), item.getRect()))
//                            .placeholder(R.drawable.photo_holder)
//                            .error(R.drawable.photo_holder))
//                    .into(holder.targetPhoto);
//        }
    }

//    private void loadInfo(int index){
//        CompareRecord item = mDataLast.get(index);
//        Api.getPeopleInfo(item.getFaceSetId(), item.getPeopleId())
//                .doOnSubscribe(this::addDisposable)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(personInfoMessage -> {
//                    if(personInfoMessage.getCode() == Message.CODE_SUCCESS){
//                        item.setPersonInfo(personInfoMessage.getBody());
//                        item.setInitInfo(true);
//                        if(inCurrentPage(index)) {
//                            notifyItemChanged(calculatePosition(index));
//                        }
//                    }else {
//                        item.setInitInfo(false);
//                    }
//                },throwable -> {
//                    ExceptionUtil.getThrowableMessage(getClass().getSimpleName(), throwable);
//                    item.setInitInfo(false);
//                });
//    }

    private void addDisposable(Disposable d){
        if(compositeDisposable == null){
            compositeDisposable = new CompositeDisposable();
        }
        if(d != null){
            compositeDisposable.add(d);
        }
    }

    public void dispose(){
        if(compositeDisposable != null){
            compositeDisposable.clear();
        }
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_compare_history, parent, false);
        return new Holder(view);
    }

    static class Holder extends RecyclerView.ViewHolder{
        @BindView(R.id.photo_iv)
        ImageView targetPhoto;
        @BindView(R.id.photo_lib_iv)
        ImageView libPhoto;
        @BindView(R.id.scale_view_compare_history)
        ScaleView scaleView;
        @BindView(R.id.name_tv)
        TextView name;
        @BindView(R.id.id_card_tv)
        TextView idCard;
        @BindView(R.id.nationality_tv)
        TextView nationality;
        @BindView(R.id.lib_tv)
        TextView libName;
        @BindView(R.id.compare_time_tv)
        TextView compareTime;

        Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void setPersonInfo(PersonInfo personInfo){
            if(personInfo == null){
                name.setText("姓名:");
                idCard.setText("身份证号:");
                nationality.setText("籍贯:");
                libName.setText("目标库:");
            }else {
                name.setText(TextUtil.format("姓名:%s", personInfo.getName()));
                idCard.setText(TextUtil.format("身份证号:%s", personInfo.getIdentity()));
                nationality.setText(TextUtil.format("籍贯:", personInfo.getName()));
                libName.setText(TextUtil.format("目标库:%s", LibManager.getLibName(String.valueOf(personInfo.getLibId()))));
            }
        }
    }
}

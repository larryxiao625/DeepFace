package com.iustu.identification.ui.main.history.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.iustu.identification.R;
import com.iustu.identification.entity.CompareRecord;
import com.iustu.identification.ui.base.PageRecyclerViewAdapter;
import com.iustu.identification.ui.widget.ScaleView;
import com.iustu.identification.util.IconFontUtil;
import com.iustu.identification.util.TextUtil;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Liu Yuchuan on 2017/11/23.
 */

public class CompareHistoryItemAdapter extends PageRecyclerViewAdapter<CompareHistoryItemAdapter.Holder, CompareRecord>{

    public interface CompareListener{
        void onDelete(CompareRecord compareRecord, int position);
        void lookOriginPhoto(int position);
    }
    private CompositeDisposable compositeDisposable;
    private CompareListener listener;

    public void setListener(CompareListener deleteListener) {
        this.listener = deleteListener;
    }

    private String targetPhotoUrl;

    public CompareHistoryItemAdapter(List<CompareRecord> dataLast) {
        super(dataLast);
        setDisplayCountPerPage(3);
    }

    @Override
    public void onBindHolder(Holder holder, int index, int position) {
        CompareRecord item = mDataLast.get(index);
        holder.setCompareRecord(item);
        holder.delete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(item, index);
            }
        });
        holder.targetPhoto.setOnClickListener( v -> {
            if (listener != null)
                listener.lookOriginPhoto(index);
        });
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
        @BindView(R.id.compare_delete)
        TextView delete;

        Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void setCompareRecord(CompareRecord compareRecord){
            if(compareRecord == null){
                name.setText("姓名:");
                idCard.setText("身份证号:");
                nationality.setText("籍贯:");
                libName.setText("目标库:");
            }else {
                IconFontUtil.getDefault().setText(delete, IconFontUtil.DELETE);
                name.setText(TextUtil.format("姓名:%s", compareRecord.getName()));
                idCard.setText(compareRecord.getIdentity());
                nationality.setText(compareRecord.getHome());
                libName.setText(TextUtil.format("目标库:%s", String.valueOf(compareRecord.getLibName())));
                compareTime.setText(compareRecord.getTime() + " " + compareRecord.getHourTime());
                scaleView.setScale((int) (compareRecord.getRate() * 100));
                nationality.setSelected(true);
                idCard.setSelected(true);
                Glide.with(itemView).load(new File(compareRecord.getUploadPhoto()))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(targetPhoto);
                String[] photos = compareRecord.getPhotoPath().split(";");
                String libPath = "/sdcard/DeepFace/" + compareRecord.getLibName()+"/" + photos[0];
                Glide.with(itemView).load(new File(libPath))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(libPhoto);

            }
        }
    }

}

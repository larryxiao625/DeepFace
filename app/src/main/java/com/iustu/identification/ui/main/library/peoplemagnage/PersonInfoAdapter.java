package com.iustu.identification.ui.main.library.peoplemagnage;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.iustu.identification.R;
import com.iustu.identification.api.Api;
import com.iustu.identification.api.message.Message;
import com.iustu.identification.entity.PersionInfo;
import com.iustu.identification.ui.base.PageRecyclerViewAdapter;
import com.iustu.identification.ui.main.library.peoplemagnage.mvp.PersionView;
import com.iustu.identification.util.ExceptionUtil;
import com.iustu.identification.util.IconFontUtil;
import com.iustu.identification.util.TextUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Liu Yuchuan on 2017/11/21.
 */

public class PersonInfoAdapter extends PageRecyclerViewAdapter<PersonInfoAdapter.Holder, PersionInfo> {

    private CompositeDisposable compositeDisposable;

    public PersonInfoAdapter(List<PersionInfo> dataLast) {
        super(dataLast);
        setDisplayCountPerPage(3);
    }

    @Override
    public void onBindHolder(Holder holder, int index, int position) {
        PersionInfo p = mDataLast.get(index);

        holder.setPersionInfo(p);
        holder.setEditEnable(false);

        holder.setSaveListener(v -> {
            PersionInfo persionInfo = mDataLast.get(index);
            persionInfo.name = holder.name.getText().toString();
            persionInfo.home = holder.location.getText().toString();
            persionInfo.gender = holder.sex.getText().toString();
            persionInfo.identity = holder.idCard.getText().toString();
            notifyDataSetChanged();
            personView.onSaveChange(persionInfo);
        });

        holder.setEditListener(v -> {
            if(!holder.isEnable){
                holder.setEditEnable(true);
                v.requestFocus();
            }
        });

        holder.add.setOnClickListener(v->{
            if(personView != null){
                personView.onAddPhoto();
            }
        });
        holder.addIcon.setOnClickListener(v->{
            if(personView != null){
                personView.onAddPhoto();
            }
        });
        holder.delete.setOnClickListener(v->{
            if(personView != null){
                PersionInfo persionInfo = mDataLast.get(index);
                mDataLast.remove(index);
                notifyDataSetChanged();
                personView.onDeletePer(persionInfo);
            }
        });
        holder.deleteIcon.setOnClickListener(v->{
            if(personView != null){
                PersionInfo persionInfo = mDataLast.get(index);
                mDataLast.remove(index);
                notifyDataSetChanged();
                personView.onDeletePer(persionInfo);
            }
        });
        holder.deletePhoto.setOnClickListener(v->{
            if(personView != null){
                personView.onDeletePhoto();
            }
        });
        holder.nextImg.setOnClickListener(v->{

        });
        holder.lastImg.setOnClickListener(v->{

        });
    }

    private PersionView personView;

    public void setItemListener(PersionView personOptInterface) {
        this.personView = personOptInterface;
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_person_info, parent, false);
        return new Holder(view);
    }

    private void loadPicUrls(int index){
        PersionInfo personInfo = mDataLast.get(index);

    }

    private void addDisposable(Disposable disposable){
        if(compositeDisposable == null){
            compositeDisposable = new CompositeDisposable();
        }

        if(disposable != null){
            compositeDisposable.add(disposable);
        }
    }

    public void dispose(){
        if(compositeDisposable != null){
            compositeDisposable.clear();
        }
    }

    static class Holder extends RecyclerView.ViewHolder{
        @BindView(R.id.last_img_tv) TextView lastImg;
        @BindView(R.id.next_img_tv) TextView nextImg;
        @BindView(R.id.img_page_tv) TextView imgPage;
        @BindView(R.id.delete_photo_view) View deletePhoto;
        @BindView(R.id.name_edit) EditText name;
        @BindView(R.id.id_card_edit) EditText idCard;
        @BindView(R.id.sex_edit) EditText sex;
        @BindView(R.id.location_edit)EditText location;
        @BindView(R.id.save_icon_tv) TextView saveIcon;
        @BindView(R.id.save_tv) TextView save;
        @BindView(R.id.add_photo_icon_tv) TextView addIcon;
        @BindView(R.id.add_photo_tv) TextView add;
        @BindView(R.id.delete_icon_tv) TextView deleteIcon;
        @BindView(R.id.delete_tv) TextView delete;
        @BindView(R.id.photo_iv) ImageView photo;
        @BindView(R.id.switch_img_layout) LinearLayout switchPhotoLayout;
        @BindView(R.id.edit_layout) LinearLayout editLayout;
        private boolean isEnable;

        Holder(View itemView) {
            super(itemView);
            isEnable = false;
            ButterKnife.bind(this, itemView);
            IconFontUtil util = IconFontUtil.getDefault();
            util.setText(lastImg, IconFontUtil.ARROW_LEFT);
            util.setText(nextImg, IconFontUtil.ARROW_RIGHT);
            util.setText(saveIcon, IconFontUtil.SAVE);
            util.setText(addIcon, IconFontUtil.ADD);
            util.setText(deleteIcon, IconFontUtil.DELETE);
            switchPhotoLayout.setBackgroundColor(Color.argb(122, 24, 38, 67));
        }

        void setPersionInfo(PersionInfo p){
            name.setText(p.name);
            idCard.setText(p.identity);
            sex.setText(p.gender);
            location.setText(p.home);
        }

        private void setEditEnable(boolean enable){
            TextUtil.enableEdit(enable, name, idCard, location, sex);
            isEnable = enable;
        }

        private void setSaveListener(View.OnClickListener listener){
            save.setOnClickListener(listener);
            saveIcon.setOnClickListener(listener);
        }

        private void setEditListener(View.OnClickListener onClickListener){
            name.setOnClickListener(onClickListener);
            idCard.setOnClickListener(onClickListener);
            sex.setOnClickListener(onClickListener);
            location.setOnClickListener(onClickListener);
        }
    }
}

package com.iustu.identification.ui.main.camera.adapter;

import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.iustu.identification.App;
import com.iustu.identification.R;
import com.iustu.identification.ui.base.PageRecyclerViewAdapter;

import java.util.List;

public class CatchFaceAdapter extends RecyclerView.Adapter<CatchFaceAdapter.CatchFaceHolder> {
    List<String> catchFaces;
    public CatchFaceAdapter() {
    }

    public CatchFaceAdapter(List<String> catchFaces) {
        this.catchFaces = catchFaces;
    }

    @NonNull
    @Override
    public CatchFaceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CatchFaceHolder(LayoutInflater.from(App.getContext()).inflate(R.layout.item_face_catch,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CatchFaceHolder holder, int position) {
        holder.setCatchFace(catchFaces.get(position));
    }

    @Override
    public int getItemCount() {
        return catchFaces.size();
    }

    class CatchFaceHolder extends RecyclerView.ViewHolder{
        ImageView catchFaceImg;
        public CatchFaceHolder(View itemView) {
            super(itemView);
            catchFaceImg=itemView.findViewById(R.id.photo_capture_pic);
        }

        public void setCatchFace(String catchFace){
            Glide.with(itemView).load(Drawable.createFromPath(catchFace))
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(catchFaceImg);
        }
    }
}

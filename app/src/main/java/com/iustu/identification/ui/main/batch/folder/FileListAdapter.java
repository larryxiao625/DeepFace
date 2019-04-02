package com.iustu.identification.ui.main.batch.folder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iustu.identification.R;
import com.iustu.identification.ui.base.OnHeaderClickListener;
import com.iustu.identification.ui.base.OnItemClickListener;
import com.iustu.identification.util.FileUtil;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by Liu Yuchuan on 2017/11/18.
 */

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.Holder> {
    private List<File> mFileList;

    private boolean withHeader;

    FileListAdapter(List<File> folderList) {
        this.mFileList = folderList;
        withHeader = false;
    }

    void setWithHeader(boolean withHeader) {
        this.withHeader = withHeader;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        if(position == 0 && withHeader){
            holder.icon.setImageResource(R.drawable.ic_folder);
            holder.desc.setText("上层文件夹");
            holder.name.setText("...");
            holder.itemView.setOnClickListener(v -> {
                if(onHeaderClickListener != null){
                    onHeaderClickListener.onHeaderClick(v);
                }
            });
            return;
        }

        File file;
        if(withHeader){
            file = mFileList.get(position-1);
        }else {
            file = mFileList.get(position);
        }

        String regex = ".+(.JPEG|.jpeg|.JPG|.jpg|.GIF|.gif|.BMP|.bmp|.PNG|.png)$";
        if(file.isDirectory()) {
            holder.icon.setImageResource(R.drawable.ic_folder);
            holder.desc.setText(String.format(Locale.CHINESE, "文件夹，包含%d个文件（夹）", file.listFiles().length));
        }else if(Pattern.matches(regex, file.getName())){
            holder.icon.setImageResource(R.drawable.ic_photo);
            String [] names = file.getName().split("\\.");
            if(names.length > 0) {
                holder.desc.setText(String.format("%s图片文件，%s", names[names.length - 1], FileUtil.calculateSpace(file)));
            }else {
                holder.desc.setText(String.format("图片，%s", FileUtil.calculateSpace(file)));
            }
        }else {
            holder.icon.setImageResource(R.drawable.ic_file);
            String [] names = file.getName().split("\\.");
            if(names.length <= 1) {
                holder.desc.setText(String.format("文件，%s", FileUtil.calculateSpace(file)));
            }else {
                holder.desc.setText(String.format("%s文件，%s", names[names.length - 1].toLowerCase(), FileUtil.calculateSpace(file)));
            }
        }
        holder.name.setText(file.getName());
        holder.itemView.setOnClickListener(v -> {
            if(onItemClickListener != null && file.isDirectory()){
                if(withHeader) {
                    onItemClickListener.onClick(v, position - 1);
                }else {
                    onItemClickListener.onClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(!withHeader) {
            return mFileList.size();
        }

        return mFileList.size() + 1;
    }


    static class Holder extends RecyclerView.ViewHolder{
        private ImageView icon;
        private TextView name;
        private TextView desc;
        private View itemView;
        Holder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            icon = itemView.findViewById(R.id.file_icon_iv);
            name = itemView.findViewById(R.id.file_name_tv);
            desc = itemView.findViewById(R.id.file_desc_tv);
        }
    }

    private OnItemClickListener onItemClickListener;

    void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnHeaderClickListener onHeaderClickListener;

    void setOnHeaderClickListener(OnHeaderClickListener onHeaderClickListener) {
        this.onHeaderClickListener = onHeaderClickListener;
    }
}

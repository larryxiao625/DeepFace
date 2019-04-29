package com.iustu.identification.ui.main.batch.folder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.iustu.identification.R;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.widget.ItemDecoration;
import com.iustu.identification.ui.widget.TitleBar;
import com.iustu.identification.ui.widget.dialog.NormalDialog;
import com.iustu.identification.util.FileUtil;
import com.iustu.identification.util.MSP;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Liu Yuchuan on 2017/11/18.
 */

public class FolderChooseFragment extends BaseFragment implements TitleBar.TitleBarListener{
    @BindView(R.id.folder_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.path_tv)
    TextView pathNowTv;
    @BindView(R.id.folder_choose_title_bar)
    TitleBar titleBar;

    public static final String KEY_PATH = "path";
    public static final String KEY_DEPTH = "depth";

    private int depth;
    private List<File> mFileList = new ArrayList<>();
    private File parentFile;
    private FileListAdapter mAdapter;

    @Override
    protected int postContentView() {
        return R.layout.fragment_folder_choose;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        titleBar.setTitleBarListener(this);
        mFileList = new ArrayList<>();
        mAdapter = new FileListAdapter(mFileList);
        mAdapter.setOnItemClickListener((view1, position) -> switchFolder(mFileList.get(position), true));
        mAdapter.setOnHeaderClickListener(view12 -> onTitleBack());
        recyclerView.addItemDecoration(new ItemDecoration(mActivity, RecyclerView.HORIZONTAL));
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        initParentFile();
        switchFolder(parentFile, true);
    }


    public void switchFolder(File file, boolean on){
        Observable.fromArray(file.listFiles())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    mFileList.clear();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((file1 -> {
                            String regex = ".+(.JPEG|.jpeg|.JPG|.jpg|.GIF|.gif|.BMP|.bmp|.PNG|.png)$";
                        if (file1.isDirectory() || Pattern.matches(regex, file1.getName()))
                            mFileList.add(file1);
                        }),
                        throwable -> Log.e("folder", throwable.getClass().getSimpleName() + "->" + throwable.getMessage()),
                        () -> {
                            parentFile = file;
                            pathNowTv.setText(parentFile.getAbsolutePath());
                            if(on) {
                                depth++;
                            }else {
                                depth--;
                                if(depth < 1){
                                    depth = 1;
                                }
                            }
                            if(depth > 1){
                                mAdapter.setWithHeader(true);
                            }else {
                                mAdapter.setWithHeader(false);
                            }
                            mAdapter.notifyDataSetChanged();
                        });
    }

    @Override
    public void onShow() {
        initParentFile();
        switchFolder(parentFile, true);
    }

    private void initParentFile(){
        Bundle bundle = getArguments();
        String path;
        File file;
        if(bundle == null){
            defaultInit();
            return;
        }

        depth = bundle.getInt(KEY_DEPTH);
        path = bundle.getString(KEY_PATH, null);

        if(depth <= 0 || path == null){
            defaultInit();
            return;
        }

        file = new File(path);
        if(file.exists() && file.isDirectory()){
            depth--;
            parentFile = file;
            return;
        }

        defaultInit();
    }

    private void defaultInit(){
        depth = 0;
        parentFile = Environment.getExternalStorageDirectory();
    }

    @Override
    public void onBackPressed() {
        new NormalDialog.Builder()
                .title("取消选择")
                .content("退出选择文件夹？")
                .positive("是", v -> getBackToBatch(false))
                .negative("否", null)
                .show(mActivity.getFragmentManager());
    }

    public void onTitleBack(){
        if(depth == 1) {
            onBackPressed();
            return;
        }

        switchFolder(parentFile.getParentFile(), false);
    }

    @Override
    public void onTitleButtonClick(int id) {
        if(id == TitleBar.ID_BACK){
            onTitleBack();
        }else {
            getBackToBatch(true);
        }
    }

    public void getBackToBatch(boolean choose){
        String path = null;
        int depth = 0;
        if(choose){
            path = parentFile.getAbsolutePath();
            depth = this.depth;
        }
        List<String> stringList = new ArrayList<>();
        for(File file: mFileList){
            if(FileUtil.isImg(file)){
                stringList.add(file.getAbsolutePath());
            }
        }
        MSP.getInstance("folder")
                .edit()
                .putInt(KEY_DEPTH, depth)
                .putString(KEY_PATH, path)
                .apply();
        Log.d("test", "getBackToBatch: " + path);
        Intent intent = new Intent();
        intent.putExtra("path", path);
        getActivity().setResult(200, intent);
        getActivity().finish();
    }
}

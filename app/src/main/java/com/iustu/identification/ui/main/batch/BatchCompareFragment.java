package com.iustu.identification.ui.main.batch;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iustu.identification.R;
import com.iustu.identification.entity.BatchSuccess;
import com.iustu.identification.ui.main.batch.folder.ChooseFolderActivity;
import com.iustu.identification.ui.main.batch.folder.FolderChooseFragment;
import com.iustu.identification.ui.main.batch.mvp.BatchPresenter;
import com.iustu.identification.ui.main.batch.mvp.BatchView;
import com.iustu.identification.ui.widget.dialog.NormalDialog;
import com.iustu.identification.ui.widget.dialog.WaitProgressDialog;
import com.iustu.identification.util.FileUtil;
import com.iustu.identification.util.IconFontUtil;
import com.iustu.identification.util.MSP;
import com.iustu.identification.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Liu Yuchuan on 2017/11/17.
 */

public class BatchCompareFragment extends DialogFragment implements BatchView {
    TextView pathTv;
    TextView folderIcon;
    ProgressBar progressBar;
    TextView progressTv;
    Button submmit;
    TextView progressTip;

    private File mFolder;
    private boolean chooseFolder;
    private int depth;

    private final ArrayList<String> imgsToCompare = new ArrayList<>();

    private static final String FORMAT_PROGRESS = "%d/%d";

    public static final String KEY_REQUEST_TYPE = "TYPE";
    public static final int REQUEST_TYPE_INVALID = -1;
    public static final int REQUEST_TYPE_FILE = 0;

    private int successCount;
    private int errorCount;

    private WaitProgressDialog waitProgressDialog;

    private boolean isInProgress;

    private BatchPresenter presenter;
    private String libName;
    private int index;
    private int success;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        libName = getArguments().getString("libName");
        index = getArguments().getInt("index");
    }

    @Override
    public void onStart() {
        super.onStart();
        Window dialogWindow = getDialog().getWindow();
        //dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#68030919")));
        dialogWindow.setBackgroundDrawableResource(R.drawable.out_line);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.alpha = 0.5f;
        dialogWindow.setAttributes(params);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        dialogWindow.setLayout((int)(dm.widthPixels * 0.84), (int)(dm.heightPixels * 0.4));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_batch_compare, container, false);
        initView(savedInstanceState, view);
        presenter = new BatchPresenter();
        presenter.setView(this);
        return view;
    }

    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        pathTv = (TextView)view.findViewById(R.id.folder_path_tv);
        folderIcon = (TextView)view.findViewById(R.id.folder_tv);
        progressBar = (ProgressBar)view.findViewById(R.id.compare_progress_bar);
        progressTv = (TextView)view.findViewById(R.id.compare_progress_tv);
        submmit = (Button)view.findViewById(R.id.start_batch_submmit);
        progressTip = (TextView)view.findViewById(R.id.progress_tv);

        IconFontUtil util = IconFontUtil.getDefault();
        util.setText(folderIcon, IconFontUtil.FOLDER);

        folderIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFolder();
            }
        });
        progressBar.setMax(100);
        submmit.setOnClickListener(v -> {
            startCompare();
        });
   }

    public void chooseFolder(){
        if(isInProgress){
            ToastUtil.show("请等待当前任务完成");
            return;
        }
        Intent intent = new Intent(getActivity(), ChooseFolderActivity.class);
        startActivityForResult(intent, 100);
    }

    public void startCompare(){
        getDialog().setCanceledOnTouchOutside(false);
        if(isInProgress){
            ToastUtil.show("请等待当前任务完成");
            return;
        }
        if(pathTv.getText().toString().equals("") || pathTv.getText().toString() == null){
            ToastUtil.show("请先选择图片所在文件夹");
            return;
        }
        isInProgress = true;
        presenter.importBatchPictures(imgsToCompare, libName);
    }

    public void onBackPressed() {
        if(isInProgress) {
            new NormalDialog.Builder()
                    .title("提示")
                    .content("停止当前任务?")
                    .negative("取消", null)
                    .positive("确定", v -> dismiss())
                    .show(getActivity().getFragmentManager());
        }else {
            this.dismiss();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        EventBus.getDefault().post(new BatchSuccess(success, index, libName));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 200) {
            String path = data.getStringExtra("path");
            pathTv.setText(path);
            mFolder = new File(path);
            for (File file : mFolder.listFiles()) {
                if(FileUtil.isImg(file)){
                    imgsToCompare.add(file.getAbsolutePath());
                }
            }
            progressBar.setMax(imgsToCompare.size());
            progressTv.setText("0/"+imgsToCompare.size());
        }
    }

    @Override
    public void setProgress(int p) {
        progressBar.setProgress(p);
        progressTv.setText(p + "/" + imgsToCompare.size());
    }

    @Override
    public void setProgressTV(int successCount, int errCount) {
        success = successCount;
        progressTip.setText(String.format("导入成功：%d张，导入失败：%d张", successCount, errCount));
    }

    @Override
    public void changeSubmitable() {
        isInProgress = false;
        getDialog().setCanceledOnTouchOutside(true);
    }
}

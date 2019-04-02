package com.iustu.identification.ui.widget.dialog;

import android.app.FragmentManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iustu.identification.R;
import com.iustu.identification.ui.base.BaseDialogFragment;

/**
 * Created by Liu Yuchuan on 2017/11/24.
 */

public class WaitProgressDialog extends BaseDialogFragment{
    private TextView button;
    private TextView title;

    private String buttonText;
    private String titleText;
    private static final String KEY_BUTTON = "button";
    private static final String KEY_TITLE = "title";

    @Override
    protected int postContentView() {
        return R.layout.dialog_wait_progress;
    }

    @Override
    protected void initView(View view) {
//        getDialog().setCanceledOnTouchOutside(false);
        button = view.findViewById(R.id.dialog_single_tv);
        title = view.findViewById(R.id.dialog_title_tv);
        Bundle bundle = getArguments();
        if(bundle != null){
            buttonText = bundle.getString(KEY_BUTTON, "");
            titleText = bundle.getString(KEY_TITLE, "");
        }
        button.setText(buttonText);
        title.setText(titleText);
        button.setOnClickListener(v->{
            dismiss();
            if(onClickListener != null){
                onClickListener.onClick(v);
            }
        });
    }

    public void setTitle(String titleText){
        this.titleText = titleText;
        title.setText(titleText);
    }

    private View.OnClickListener onClickListener;


    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout((int) (dm.widthPixels * 0.7), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public static class Builder{
        private String buttonText;
        private String titleText;
        private View.OnClickListener onClickListener;
        private boolean cancelable;

        public Builder button(String buttonText, View.OnClickListener onClickListener){
            this.buttonText = buttonText;
            this.onClickListener = onClickListener;
            return this;
        }

        public Builder title(String titleText){
            this.titleText = titleText;
            return this;
        }

        public Builder cancelable(boolean cancelable){
            this.cancelable = cancelable;
            return this;
        }

        public WaitProgressDialog build(){
            WaitProgressDialog waitProgressDialog = new WaitProgressDialog();
            waitProgressDialog.setCancelable(cancelable);
            Bundle bundle = new Bundle();
            bundle.putString(KEY_BUTTON, buttonText);
            bundle.putString(KEY_TITLE, titleText);
            waitProgressDialog.setArguments(bundle);
            waitProgressDialog.onClickListener = onClickListener;
            return waitProgressDialog;
        }

        public void show(FragmentManager manager){
            WaitProgressDialog waitProgressDialog = build();
            waitProgressDialog.show(manager, "wait");
        }
    }
}

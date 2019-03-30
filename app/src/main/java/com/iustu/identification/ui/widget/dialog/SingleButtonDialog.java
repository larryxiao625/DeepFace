package com.iustu.identification.ui.widget.dialog;

import android.app.FragmentManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.iustu.identification.R;
import com.iustu.identification.ui.base.BaseDialogFragment;

/**
 * Created by Liu Yuchuan on 2017/11/15.
 */

public class SingleButtonDialog extends BaseDialogFragment{
    private static final String KEY_TITLE = "title";
    private static final String KEY_BUTTON = "button";
    private static final String KEY_CONTENT = "content";

    private String buttonText;
    private String title;
    private String content;

    private TextView button;
    private TextView titleTv;
    private TextView contentTv;


    private View.OnClickListener onClickListener;

    @Override
    protected int postContentView() {
        return R.layout.dialog_single_button;
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        Window  window = getDialog().getWindow();
        if(window != null){
            window.setLayout((int) (dm.widthPixels * 0.7), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    protected void initView(View view) {
        titleTv = view.findViewById(R.id.dialog_title_tv);
        button = view.findViewById(R.id.dialog_single_tv);
        contentTv = view.findViewById(R.id.dialog_content_tv);
        Bundle bundle = getArguments();
        if(bundle != null){
            title = bundle.getString(KEY_TITLE, "");
            buttonText = bundle.getString(KEY_BUTTON, "");
            content = bundle.getString(KEY_CONTENT, "");
        }
        if(titleTv != null){
            titleTv.setText(title);
        }
        if(contentTv != null){
            contentTv.setText(content);
        }
        button.setText(buttonText);
        button.setOnClickListener(v->{
            dismiss();
            if(onClickListener != null){
                onClickListener.onClick(v);
            }
        });
    }


    public static class Builder{
        private String title = "";
        private String content = "";
        private String buttonText;
        private boolean cancelable;
        private View.OnClickListener onClickListener;

        public Builder title(String title){
            this.title = title;
            return this;
        }

        public SingleButtonDialog.Builder button(String buttonText, View.OnClickListener onClickListener){
            this.buttonText = buttonText;
            this.onClickListener = onClickListener;
            return this;
        }

        public Builder content(String content){
            this.content = content;
            return this;
        }

        public Builder cancelable(boolean cancelable){
            this.cancelable = cancelable;
            return this;
        }

        public SingleButtonDialog build(){
            SingleButtonDialog singleButtonDialog = new SingleButtonDialog();
            singleButtonDialog.setCancelable(cancelable);
            Bundle bundle = new Bundle();
            bundle.putString(KEY_TITLE, title);
            bundle.putString(KEY_CONTENT, content);
            bundle.putString(KEY_BUTTON, buttonText);
            singleButtonDialog.onClickListener = this.onClickListener;
            singleButtonDialog.setArguments(bundle);
            return singleButtonDialog;
        }

        public void show(FragmentManager manager){
            SingleButtonDialog normalDialog = build();
            normalDialog.show(manager, "single");
        }
    }
}

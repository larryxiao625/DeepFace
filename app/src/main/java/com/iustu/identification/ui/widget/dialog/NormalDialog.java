package com.iustu.identification.ui.widget.dialog;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.iustu.identification.R;
import com.iustu.identification.ui.base.BaseDialogFragment;

import butterknife.BindView;

/**
 * Created by Liu Yuchuan on 2017/11/15.
 */

public class NormalDialog extends BaseDialogFragment{
    private static final String KEY_TITLE = "title";
    private static final String KEY_POSITIVE = "positive";
    private static final String KEY_NEGATIVE = "negative";
    private static final String KEY_CONTENT = "content";

    private String title;
    private String positiveText;
    private String negativeText;
    private String content;

    private TextView titleTv;
    private TextView negativeTv;
    private TextView positiveTv;
    private TextView contentTv;

    private View.OnClickListener positiveListener;
    private View.OnClickListener negativeListener;

    @Override
    protected int postContentView() {
        return R.layout.dialog_normal;
    }

    @Override
    protected void initView(View view) {
        titleTv = view.findViewById(R.id.dialog_title_tv);
        positiveTv = view.findViewById(R.id.dialog_positive_tv);
        negativeTv = view.findViewById(R.id.dialog_negative_tv);
        contentTv = view.findViewById(R.id.dialog_content_tv);
        Bundle bundle = getArguments();
        if(bundle != null){
            title = bundle.getString(KEY_TITLE, "");
            positiveText = bundle.getString(KEY_POSITIVE, "");
            negativeText = bundle.getString(KEY_NEGATIVE, "");
            content = bundle.getString(KEY_CONTENT, "");
        }
        if(titleTv != null){
            titleTv.setText(title);
        }
        if(positiveTv != null){
            positiveTv.setText(positiveText);
            positiveTv.setOnClickListener(v -> {
                if(positiveListener != null){
                    positiveListener.onClick(v);
                }
                dismiss();
            });
        }
        if(negativeTv != null){
            negativeTv.setText(negativeText);
            negativeTv.setOnClickListener(v -> {
                if(negativeListener != null){
                    negativeListener.onClick(v);
                }
                dismiss();
            });
        }
        if(contentTv != null){
            contentTv.setText(content);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout((int) (dm.widthPixels * 0.75), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public static class Builder{
        private String title = "";
        private String positiveText ="";
        private String negativeText = "";
        private String content = "";
        private boolean cancelable;

        private View.OnClickListener positiveListener;
        private View.OnClickListener negativeListener;

        public Builder title(String title){
            this.title = title;
            return this;
        }

        public Builder cancelable(boolean cancelable){
            this.cancelable = cancelable;
            return this;
        }

        public Builder positive(String positiveText, View.OnClickListener positiveListener){
            this.positiveText = positiveText;
            this.positiveListener = positiveListener;
            return this;
        }

        public Builder negative(String negativeText, View.OnClickListener negativeListener){
            this.negativeText = negativeText;
            this.negativeListener = negativeListener;
            return this;
        }

        public Builder content(String content){
            this.content = content;
            return this;
        }

        public NormalDialog build(){
            NormalDialog normalDialog = new NormalDialog();
            normalDialog.negativeListener = negativeListener;
            normalDialog.positiveListener = positiveListener;
            Bundle bundle = new Bundle();
            bundle.putString(KEY_TITLE, title);
            bundle.putString(KEY_POSITIVE, positiveText);
            bundle.putString(KEY_NEGATIVE, negativeText);
            bundle.putString(KEY_CONTENT, content);
            normalDialog.setCancelable(cancelable);
            normalDialog.setArguments(bundle);
            return normalDialog;
        }

        public void show(FragmentManager manager){
            NormalDialog normalDialog = build();
            normalDialog.show(manager, "normal");
        }
    }
}

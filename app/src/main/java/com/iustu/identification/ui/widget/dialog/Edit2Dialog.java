package com.iustu.identification.ui.widget.dialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputLayout;
import com.iustu.identification.R;
import com.iustu.identification.ui.base.BaseDialogFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Liu Yuchuan on 2017/11/15.
 */

public class Edit2Dialog extends BaseDialogFragment{
    private static final String KEY_TITLE = "title";
    private static final String KEY_POSITIVE = "positive";
    private static final String KEY_NEGATIVE = "negative";
    private static final String KEY_CONTENT_1 = "content1";
    private static final String KEY_CONTENT_2 = "content2";
    private static final String KEY_HINT_1 = "hint1";
    private static final String KEY_HINT_2 = "hint2";

    private String title;
    private String positiveText;
    private String negativeText;
    private String content1;
    private String content2;
    private String hint1;
    private String hint2;

    private TextView titleTv;
    private TextView negativeTv;
    private TextView positiveTv;
    private TextInputLayout editLayout1;
    private TextInputLayout editLayout2;
    private EditText contentEdit1;
    private EditText contentEdit2;

    private Edit2DialogListener positiveListener;
    private Edit2DialogListener negativeListener;

    public interface Edit2DialogListener{
        boolean onClick(View v, TextInputLayout layout1, TextInputLayout layout2);
    }

    @Override
    protected int postContentView() {
        return R.layout.dialog_two_edit;
    }

    @Override
    protected void initView(View view) {
        getDialog().setCanceledOnTouchOutside(false);
        titleTv = view.findViewById(R.id.dialog_title_tv);
        positiveTv = view.findViewById(R.id.dialog_positive_tv);
        negativeTv = view.findViewById(R.id.dialog_negative_tv);
        contentEdit1 = view.findViewById(R.id.dialog_edit_et_1);
        contentEdit2 = view.findViewById(R.id.dialog_edit_et_2);
        editLayout1 = view.findViewById(R.id.dialog_til_1);
        editLayout2 = view.findViewById(R.id.dialog_til_2);
        // 限制只能输入中英文
        InputFilter inputFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
                Pattern p = Pattern.compile("[a-zA-Z|\u4e00-\u9fa5]+");
                Matcher m = p.matcher(charSequence.toString());
                if (!m.matches()) return "";
                return null;
            }
        };
        contentEdit1.setFilters(new InputFilter[]{inputFilter});
        Bundle bundle = getArguments();
        if(bundle != null){
            title = bundle.getString(KEY_TITLE, "");
            positiveText = bundle.getString(KEY_POSITIVE, "");
            negativeText = bundle.getString(KEY_NEGATIVE, "");
            content1 = bundle.getString(KEY_CONTENT_1, "");
            content2 = bundle.getString(KEY_CONTENT_2, "");
            hint1 = bundle.getString(KEY_HINT_1, "");
            hint2 = bundle.getString(KEY_HINT_2, "");
        }
        titleTv.setText(title);
        positiveTv.setText(positiveText);
        editLayout1.setHint(hint1);
        editLayout2.setHint(hint2);
        positiveTv.setOnClickListener(v -> {
            boolean dismiss = true;
            if(positiveListener != null){
                dismiss = positiveListener.onClick(v, editLayout1, editLayout2);
            }
            if(dismiss) {
                dismiss();
            }
        });
        negativeTv.setText(negativeText);
        negativeTv.setOnClickListener(v -> {
            if(negativeListener != null){
                negativeListener.onClick(v, editLayout1, editLayout2);
            }
            dismiss();
        });
        contentEdit1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editLayout1.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        contentEdit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editLayout2.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        contentEdit1.setText(content1);
        contentEdit2.setText(content2);
        contentEdit1.requestFocus();
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
        private String content1 = "";
        private String content2 = "";
        private String hint1 = "";
        private String hint2 = "";

        private Edit2DialogListener positiveListener;
        private Edit2DialogListener negativeListener;

        public Builder title(String title){
            this.title = title;
            return this;
        }

        public Builder positive(String positiveText, Edit2DialogListener positiveListener){
            this.positiveText = positiveText;
            this.positiveListener = positiveListener;
            return this;
        }

        public Builder negative(String negativeText, Edit2DialogListener negativeListener){
            this.negativeText = negativeText;
            this.negativeListener = negativeListener;
            return this;
        }

        public Builder content1(String content){
            this.content1 = content;
            return this;
        }

        public Builder content2(String content){
            this.content2 = content;
            return this;
        }

        public Builder hint1(String hint){
            this.hint1 = hint;
            return this;
        }

        public Builder hint2(String hint){
            this.hint2 = hint;
            return this;
        }

        public Edit2Dialog build(){
            Edit2Dialog normalDialog = new Edit2Dialog();
            normalDialog.negativeListener = negativeListener;
            normalDialog.positiveListener = positiveListener;
            Bundle bundle = new Bundle();
            bundle.putString(KEY_TITLE, title);
            bundle.putString(KEY_POSITIVE, positiveText);
            bundle.putString(KEY_NEGATIVE, negativeText);
            bundle.putString(KEY_CONTENT_1, content1);
            bundle.putString(KEY_CONTENT_2, content2);
            bundle.putString(KEY_HINT_1, hint1);
            bundle.putString(KEY_HINT_2, hint2);
            normalDialog.setArguments(bundle);
            return normalDialog;
        }

        public void show(FragmentManager manager){
            Edit2Dialog editDialog = build();
            editDialog.show(manager, "edit");
        }
    }
}


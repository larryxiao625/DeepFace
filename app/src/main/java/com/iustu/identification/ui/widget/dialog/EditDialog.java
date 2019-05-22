package com.iustu.identification.ui.widget.dialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputLayout;
import com.iustu.identification.R;
import com.iustu.identification.ui.base.BaseDialogFragment;

/**
 * Created by Liu Yuchuan on 2017/11/15.
 */

public class EditDialog extends BaseDialogFragment{
    private static final String KEY_TITLE = "title";
    private static final String KEY_POSITIVE = "positive";
    private static final String KEY_NEGATIVE = "negative";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_HINT = "hint";
    private static final String KEY_INPUT_TYPE = "input type";

    private String title;
    private String positiveText;
    private String negativeText;
    private String content;
    private String hintText;
    private int inputType;

    private TextView titleTv;
    private TextView negativeTv;
    private TextView positiveTv;
    private EditText contentEdit;
    private TextInputLayout textInputLayout;

    private EditDialogListener positiveListener;
    private EditDialogListener negativeListener;

    public interface EditDialogListener{
        boolean onClick(View v, String content, TextInputLayout layout);
    }

    @Override
    protected int postContentView() {
        return R.layout.dialog_edit;
    }

    @Override
    protected void initView(View view) {
        getDialog().setCanceledOnTouchOutside(false);
        titleTv = view.findViewById(R.id.dialog_title_tv);
        positiveTv = view.findViewById(R.id.dialog_positive_tv);
        negativeTv = view.findViewById(R.id.dialog_negative_tv);
        contentEdit = view.findViewById(R.id.dialog_edit_et);
        textInputLayout = view.findViewById(R.id.dialog_til);
        Bundle bundle = getArguments();
        if(bundle != null){
            title = bundle.getString(KEY_TITLE, "");
            positiveText = bundle.getString(KEY_POSITIVE, "");
            negativeText = bundle.getString(KEY_NEGATIVE, "");
            content = bundle.getString(KEY_CONTENT, "");
            hintText = bundle.getString(KEY_HINT, "");
            inputType = bundle.getInt(KEY_INPUT_TYPE, InputType.TYPE_CLASS_TEXT);
        }
        contentEdit.setInputType(inputType);
        titleTv.setText(title);
        positiveTv.setText(positiveText);
        positiveTv.setOnClickListener(v -> {
            boolean dismiss = true;
            if(positiveListener != null){
                dismiss = positiveListener.onClick(v, contentEdit.getText().toString(), textInputLayout);
            }
            if(dismiss) {
                dismiss();
            }
        });
        negativeTv.setText(negativeText);
        negativeTv.setOnClickListener(v -> {
            if(negativeListener != null){
                negativeListener.onClick(v, contentEdit.getText().toString().trim(), textInputLayout);
            }
            dismiss();
        });
        textInputLayout.setHint(hintText);
        contentEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        contentEdit.setText(content);
        contentEdit.requestFocus();
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
        private String hint = "";
        private int inputType = InputType.TYPE_CLASS_TEXT;

        private EditDialogListener positiveListener;
        private EditDialogListener negativeListener;

        public Builder title(String title){
            this.title = title;
            return this;
        }

        public Builder positive(String positiveText, EditDialogListener positiveListener){
            this.positiveText = positiveText;
            this.positiveListener = positiveListener;
            return this;
        }

        public Builder negative(String negativeText, EditDialogListener negativeListener){
            this.negativeText = negativeText;
            this.negativeListener = negativeListener;
            return this;
        }

        public Builder content(String content){
            this.content = content;
            return this;
        }

        public Builder hint(String hint){
            this.hint = hint;
            return this;
        }

        public Builder inputType(int inputType){
            this.inputType = inputType;
            return this;
        }

        public EditDialog build(){
            EditDialog normalDialog = new EditDialog();
            normalDialog.negativeListener = negativeListener;
            normalDialog.positiveListener = positiveListener;
            Bundle bundle = new Bundle();
            bundle.putString(KEY_TITLE, title);
            bundle.putString(KEY_POSITIVE, positiveText);
            bundle.putString(KEY_NEGATIVE, negativeText);
            bundle.putString(KEY_CONTENT, content);
            bundle.putString(KEY_HINT, hint);
            bundle.putInt(KEY_INPUT_TYPE, inputType);
            normalDialog.setArguments(bundle);
            return normalDialog;
        }

        public void show(FragmentManager manager){
            EditDialog editDialog = build();
            editDialog.show(manager, "edit");
        }
    }
}

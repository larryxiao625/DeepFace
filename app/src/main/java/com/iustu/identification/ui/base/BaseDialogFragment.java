package com.iustu.identification.ui.base;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import java.lang.reflect.Field;

/**
 * Created by Liu Yuchuan on 2017/11/13.
 */

public abstract class BaseDialogFragment extends DialogFragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(postContentView(), container, false);
        initView(view);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    @Override
    public void onDestroyView() {
        view = null;
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        Window window = dialog.getWindow();
        if(window != null){
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    protected abstract int postContentView();

    protected void initView(View view){

    }

    @Override
    public void dismiss() {
        dismissAllowingStateLoss();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
//        super.show(manager, tag);
        //noNeed
//        try {
//            Field mDismissed = DialogFragment.class.getDeclaredField("mDismissed");
//            Field mShownByMe = DialogFragment.class.getDeclaredField("mShownByMe");
//            mDismissed.setAccessible(true);
//            mShownByMe.setAccessible(true);
//            mDismissed.set(this, false);
//            mShownByMe.set(this, true);
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
        manager.beginTransaction()
                .add(this, tag)
                .commitAllowingStateLoss();
    }
}

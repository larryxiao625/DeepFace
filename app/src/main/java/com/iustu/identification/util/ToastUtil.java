package com.iustu.identification.util;

import android.support.annotation.StringRes;
import android.widget.Toast;

import com.iustu.identification.App;

/**
 * Created by 972694341@qq.com on 2017/10/11.
 */

public class ToastUtil {
    private static Toast mToast;


    private ToastUtil(){}

    public static void show(String text){
        if(mToast == null){
            mToast = Toast.makeText(App.getContext(), text, Toast.LENGTH_SHORT);
        }else {
            mToast.setText(text);
        }
        mToast.show();
    }
    public static void showLong(String text){
        if(mToast == null){
            mToast = Toast.makeText(App.getContext(), text, Toast.LENGTH_LONG);
        }else {
            mToast.setText(text);
        }
        mToast.show();
    }

    public static void show(@StringRes int resId){
        show(App.getContext().getString(resId));
    }


    public static void cancel(){
        if(mToast != null){
            mToast.cancel();
        }
    }
}

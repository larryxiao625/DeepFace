package com.iustu.identification.util;

import android.util.Log;

import com.iustu.identification.api.message.Message;

/**
 * Created by Liu Yuchuan on 2017/11/20.
 */

public class ExceptionUtil {
    public static void getThrowableMessage(Throwable t){
        StringBuilder sb = new StringBuilder();
        sb.append(t.getClass().getSimpleName());
        sb.append("->");
        sb.append(t.getMessage());
//        sb.append('\n');
//        for (StackTraceElement stackTraceElement : t.getStackTrace()) {
//            sb.append(stackTraceElement);
//        }
        Log.e("error", sb.toString());
    }

    public static void toastNetError(Message message){
        ToastUtil.show("连接服务器失败(错误" + message.getCode() + ")");
    }

    public static void toastOptFail(String message, Message response){
        ToastUtil.show(message + "(错误码)" + response.getCode());
    }

    public static void toastServerError(){
        ToastUtil.show("连接服务器失败");
    }

    public static void getThrowableMessage(String TAG, Throwable t){
        StringBuilder sb = new StringBuilder();
        sb.append(t.getClass().getSimpleName());
        sb.append("->");
        sb.append(t.getMessage());
//        sb.append('\n');
//        for (StackTraceElement stackTraceElement : t.getStackTrace()) {
//            sb.append(stackTraceElement);
//        }
        Log.e(TAG, sb.toString());
    }
}

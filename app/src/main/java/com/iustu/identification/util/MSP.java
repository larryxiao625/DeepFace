package com.iustu.identification.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.iustu.identification.App;

/**
 * Created by Liu Yuchuan on 2017/11/5.
 *
 * 操作SharePreference的工具类
 */

public class MSP {
    public static final String SP_PARAMETERS = "lib_parameters";    // 人脸库管理界面中参数的配置
    public static final String SP_CHOSEN = "lib_chosen";     // 人脸库管理界面中被选中的人脸库
    public static final String SP_ACCOUNT = "lib_account";   // 保存登录的账户
    public static final String IS_FIRST_TIME="is_first_time";   //检测是否是第一次进入应用
    private static SharedPreferences preferences;

    private static String lastName;

    public static SharedPreferences getInstance(String name){
        if(preferences == null || !name.equals(lastName)){
            preferences = App
                    .getContext()
                    .getSharedPreferences(name, Context.MODE_PRIVATE);
            lastName = name;
        }
        return preferences;
    }
}

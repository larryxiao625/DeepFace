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
    private static SharedPreferences preferences;

    private static String lastName;

    public static SharedPreferences getInstance(String name){
        if(preferences == null || !name.equals(lastName)){
            preferences = App
                    .getContext()
                    .getSharedPreferences(name, Context.MODE_PRIVATE);
        }
        return preferences;
    }
}

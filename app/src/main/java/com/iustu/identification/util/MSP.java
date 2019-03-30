package com.iustu.identification.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.iustu.identification.App;

/**
 * Created by Liu Yuchuan on 2017/11/5.
 */

public class MSP {
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

package com.iustu.identification;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.example.agin.facerecsdk.FacerecUtil;
import com.iustu.identification.util.SDKUtil;
import android.content.Context;

import com.iustu.identification.util.SqliteHelper;
import com.iustu.identification.util.SqliteUtil;
import com.tencent.bugly.Bugly;

import org.litepal.LitePalApplication;

import java.io.File;

/**
 * Created by Liu Yuchuan on 2017/11/4.
 */

public class App extends LitePalApplication {
    static {
        System.loadLibrary("facerecsdk_java");
    }

    static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
//        CrashReport.initCrashReport(getApplicationContext());
        Bugly.init(getApplicationContext(), "9c3bdbe293", false );
        SqliteHelper.init(getApplicationContext());
        try {
            SqliteUtil.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        context = this;
    }



    public static Context getContext(){
        return context;
    }
}
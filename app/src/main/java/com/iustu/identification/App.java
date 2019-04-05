package com.iustu.identification;

import android.content.Context;
import com.iustu.identification.util.SqliteHelper;
import com.iustu.identification.util.SqliteUtil;
import com.tencent.bugly.Bugly;

import org.litepal.LitePalApplication;

/**
 * Created by Liu Yuchuan on 2017/11/4.
 */

public class App extends LitePalApplication {
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
        context=getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
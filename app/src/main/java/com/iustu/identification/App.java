package com.iustu.identification;

import android.content.Context;

import com.facebook.stetho.Stetho;
import com.fanjun.keeplive.KeepLive;
import com.fanjun.keeplive.config.ForegroundNotification;
import com.fanjun.keeplive.config.KeepLiveService;
import com.iustu.identification.ui.main.camera.prenster.CapturePicService;
import com.iustu.identification.util.AlarmUtil;
import com.iustu.identification.util.SDKUtil;
import com.iustu.identification.util.SqliteHelper;
import com.iustu.identification.util.SqliteUtil;
import com.tencent.bugly.crashreport.CrashReport;

import org.litepal.LitePalApplication;


/**
 * Created by Liu Yuchuan on 2017/11/4.
 */

public class App extends LitePalApplication{
    static {
        System.loadLibrary("facerecsdk_java");
    }

    static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        //Stetho.initializeWithDefaults(this);
        CrashReport.initCrashReport(getApplicationContext());
        //CrashReport.testJavaCrash();
        //Bugly.init(getApplicationContext(), "9c3bdbe293", false );
        SqliteHelper.init(getApplicationContext());
        try {
            SqliteUtil.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Stetho.initializeWithDefaults(this);
        context = this;
        keepAlive();

    }

    @Override
    public void onTerminate() {
        SDKUtil.destory();
        AlarmUtil.destory();
        super.onTerminate();
    }

    public static Context getContext(){
        return context;
    }

    // 应用保活
    public void keepAlive(){
        ForegroundNotification foregroundNotification=new ForegroundNotification("DeepFace","DeepFace正在实时检测",R.mipmap.ic_launcher);
        KeepLive.startWork(this, KeepLive.RunMode.ROGUE, foregroundNotification, new KeepLiveService() {
            @Override
            public void onWorking() {

            }

            @Override
            public void onStop() {

            }
        });
    }
}
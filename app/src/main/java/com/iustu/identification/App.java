package com.iustu.identification;

import android.content.Context;

import com.facebook.stetho.Stetho;
import com.fanjun.keeplive.KeepLive;
import com.fanjun.keeplive.config.ForegroundNotification;
import com.fanjun.keeplive.config.KeepLiveService;
import com.iustu.identification.ui.main.camera.prenster.CapturePicService;
import com.iustu.identification.util.SqliteHelper;
import com.iustu.identification.util.SqliteUtil;
import com.tencent.bugly.Bugly;

import org.litepal.LitePalApplication;


/**
 * Created by Liu Yuchuan on 2017/11/4.
 */

public class App extends LitePalApplication{
    static {
        System.loadLibrary("facerecsdk_java");
    }

    static Context context;


    static {
        System.loadLibrary("facerecsdk_java");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
//        CrashReport.initCrashReport(getApplicationContext());
        Bugly.init(getApplicationContext(), "9c3bdbe293", false );
        SqliteHelper.init(getApplicationContext());
        try {
            SqliteUtil.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        context = this;
        keepAlive();

    }


    public static Context getContext(){
        return context;
    }

    // 应用保活
    public void keepAlive(){
        ForegroundNotification foregroundNotification=new ForegroundNotification("DeepFace","应用正在实时检测",R.mipmap.ic_launcher);
        KeepLive.startWork(this, KeepLive.RunMode.ENERGY, foregroundNotification, new KeepLiveService() {
            @Override
            public void onWorking() {

            }

            @Override
            public void onStop() {

            }
        });
    }
}
package com.iustu.identification;

<<<<<<< HEAD
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.example.agin.facerecsdk.FacerecUtil;
import com.iustu.identification.util.SDKUtil;
=======
import android.content.Context;
>>>>>>> 7e633185081deb8a3fd272c85adcba079a609d14
import com.iustu.identification.util.SqliteHelper;
import com.iustu.identification.util.SqliteUtil;
import com.tencent.bugly.Bugly;

import org.litepal.LitePalApplication;

import java.io.File;

/**
 * Created by Liu Yuchuan on 2017/11/4.
 */

public class App extends LitePalApplication {
<<<<<<< HEAD

    static {
        System.loadLibrary("facerecsdk_java");
    }
=======
    static Context context;
>>>>>>> 7e633185081deb8a3fd272c85adcba079a609d14
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
<<<<<<< HEAD

        initSdk();
    }

    private void initSdk() {
        // Facerec初始化
        FacerecUtil.init(this);

        String file = Environment.getExternalStorageDirectory().getAbsolutePath();
        // 设置生成的证书文件的路径
        FacerecUtil.generateLicense(file + "/license_key.txt");
        updateFile(file + "/license_key.txt");
        // 设置license的路径
        FacerecUtil.setLicensePath(file);
        if (FacerecUtil.facerecsdkValid()) {
            Log.d("testSdk","sdk合法");
        }

        SDKUtil.init();
    }

    private void updateFile(String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        getApplicationContext().sendBroadcast(scanIntent);
=======
        context=getApplicationContext();
    }

    public static Context getContext(){
        return context;
>>>>>>> 7e633185081deb8a3fd272c85adcba079a609d14
    }
}
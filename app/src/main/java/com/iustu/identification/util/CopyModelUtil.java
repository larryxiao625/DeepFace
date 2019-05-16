package com.iustu.identification.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * created by sgh, 2019-4-5
 * 用于将训练模型从assects目录下拷贝到sd卡的根目录下
 */
public class CopyModelUtil {
    private static String modelPath = "model";     // 这个目录是相对于assets的，所以直接model就完事儿了
    private static String sdPath = Environment.getExternalStorageDirectory().getPath() + "/";

    // 将assects下的model文件夹中的内容拷贝到手机sd卡
    public static void copyAssetsToSD(Context context) {
        Log.d("asstes", String.valueOf(new File(sdPath).canWrite()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    String[] models = context.getAssets().list(modelPath);
                    for (String model : models) {
                        File outFile = new File(sdPath + model);
                        Log.e("", "copyAssetsToSD: ++++++++++++++++" + sdPath + model);
                        if (outFile.exists())
                            continue;
                        InputStream inputStream = context.getAssets().open(modelPath + "/" + model);
                        FileOutputStream outputStream = new FileOutputStream(outFile);
                        byte[] buffer = new byte[1024];
                        int byteCount;
                        while ((byteCount = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, buffer.length);
                        }
                        outputStream.flush();
                        inputStream.close();
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

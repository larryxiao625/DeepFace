package com.iustu.identification.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

/**
 * 获取设备唯一标识符的工具类
 */
public class UUIDutil {
    static private String UUID;
    static private String UUIDMountFileString="/sdcard/UUID.txt";

    /**
     * 判断是否是第一次在手机上使用这个应用
     * @return 返回结果
     */
    public static boolean isFirstUse(){
        File UUIDFile=new File(UUIDMountFileString);
        if(UUIDFile.exists()){
            return false;
        }
        return true;
    }

    /**
     * 第一次设置唯一标识符
     * @throws IOException
     */
    public static void setUUID() throws IOException {
        UUID= java.util.UUID.randomUUID().toString();
        File file=new File(UUIDMountFileString);
        file.createNewFile();
        FileOutputStream fos=new FileOutputStream(UUIDMountFileString);
        fos.write(UUID.getBytes());
        fos.close();
    }

    public static String getUUID() {
        try {
            FileInputStream fis=new FileInputStream(UUIDMountFileString);
            byte[] bytes=new byte[fis.available()];
            fis.read(bytes);
            UUID=new String(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("UUID",UUID);
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }
}

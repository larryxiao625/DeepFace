package com.iustu.identification.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by Liu Yuchuan on 2017/11/18.
 */

public class FileUtil {
    private static final String regex = ".+(.JPEG|.jpeg|.JPG|.jpg|.GIF|.gif|.BMP|.bmp|.PNG|.png)$";

    private static final Pattern PATTERN = Pattern.compile(regex);

    public static String getType(File file){
        if(file.isDirectory()){
            return "文件夹，"+ file.listFiles().length + "个子文件（夹）";
        }
        String [] names = file.getName().split("\\.");
        if(names.length > 1)
            return names[names.length - 1] + "文件";

        return "文件";
    }

    public static String getDesc(File file){
        if(file.isDirectory()){
            return "文件夹，"+ file.listFiles().length + "个子文件（夹）";
        }

        if(Pattern.matches(file.getName(), regex)){
            String[] names = file.getName().split("\\.");
            return names[names.length] + "文件，" + calculateSpace(file);
        }

        String ret = "文件，";
        String [] names = file.getName().split("\\.");
        if(names.length > 1){
            ret = names[names.length - 1] + ret;
        }
        return ret + calculateSpace(file);
    }

    public static String calculateSpace(File file){
        long l = file.length();
        if(l < 100){
            return l + "b";
        }
        double ans = l / 1024.0;
        if(ans < 100){
            return String.format(Locale.ENGLISH,"%.2fkb", ans);
        }

        ans /= 1024;
        if(ans < 100){
            return String.format(Locale.ENGLISH,"%.2fmb", ans);
        }

        return String.format(Locale.ENGLISH,"%.2fgb", ans/1024);
    }

    public static boolean isImg(File file){
        return PATTERN.matcher(file.getName()).matches();
    }

    // 用来在SD卡根路径下建立文件夹
    public static void createAppDirectory(String path) {
        String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(sdcard + "/DeepFace");
        if (!file.exists())
            file.mkdir();
    }

    public static void copy(String from, String to) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStream = new FileInputStream(from);
                    FileOutputStream outputStream = new FileOutputStream(to);
                    byte[] buffer = new byte[1024];
                    int byteCount;
                    while ((byteCount = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, buffer.length);
                    }
                    outputStream.flush();
                    inputStream.close();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public static void delete(String finalPath) {
        new Thread(() -> {
            File file = new File(finalPath);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    File f = files[i];
                    f.delete();
                }
            }
            file.delete();
        }).start();
    }

    public static void modify(String s, String newName) {
        File file = new File(s);
        File n = new File(newName);
        file.renameTo(n);
    }
}

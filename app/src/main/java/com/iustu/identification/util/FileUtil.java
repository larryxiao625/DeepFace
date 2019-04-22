package com.iustu.identification.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.iustu.identification.entity.PersionInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    /**
     * 用来在SD卡根路径下建立文件夹
     */
    public static void createAppDirectory() {
        String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(sdcard + "/DeepFace");
        if (!file.exists())
            file.mkdir();
        File temp = new File(sdcard + "/DeepFace/temp");
        if (!temp.exists())
            temp.mkdir();
        File cut = new File(sdcard + "/DeepFace/Cut");
        if (!cut.exists())
            cut.mkdir();
    }

    /**
     * 用来在添加人员、批量导入的时候将图片导入到人脸库的路径下
     * @param from 原图路径
     * @param to 目标路径
     */
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

    /**
     * 用来在检测人脸前生成压缩后的图片供检测
     * @param from 原图片路径
     * @param to 压缩图片路径，为temp文件夹下
     */
    public static void copyCompressedBitmap(String from, String to){
        try {
            FileInputStream fs = new FileInputStream(new File(from));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            BitmapFactory.decodeStream(fs, null, options);
            fs.close();
            options.inSampleSize = cauculateInSampleSize(options);
            options.inJustDecodeBounds = false;
            FileInputStream fss = new FileInputStream(new File(from));
            Bitmap bitmap = BitmapFactory.decodeStream(fss, null, options);
            File file = new File(to);
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            fss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 计算将图片压缩为240 x 160的压缩比例
    private static int cauculateInSampleSize(BitmapFactory.Options options) {
        int targetHeight = 160;
        int targetWidget = 240;
        int inSampleSize = 1;
        if (options.outHeight > targetHeight || options.outWidth > targetWidget) {
            int heightRatio = Math.round((float) options.outHeight / (float) targetHeight);
            int widthRatio = Math.round((float) options.outWidth / (float) targetWidget);
            inSampleSize = Math.min(heightRatio, widthRatio);
        }
        Log.d("bitmap", "cauculateInSampleSize: " + inSampleSize);
        return inSampleSize;
    }


    /**
     * 删除文件夹及其里面的文件，在删除人脸库的时候调用
     * @param finalPath 人脸库的路径
     */
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

    /**
     * 从人脸库删除成员的时候将其所有的照片删除
     * @param p
     */
    public static void deletePersionPhotos(PersionInfo p) {
        new Thread(() -> {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DeepFace/" + p.libName;
            String[] photos = p.photoPath.split(";");
            for (int i = 0; i < photos.length; i ++) {
                String photoPath = path + photos[i];
                File file = new File(photoPath);
                file.deleteOnExit();
            }
        }).start();
    }

    /**
     * 修改文件夹或者文件的名字，在修改人脸库的时候调用
     * @param s 旧名字
     * @param newName 新名字
     */
    public static void modify(String s, String newName) {
        File file = new File(s);
        File n = new File(newName);
        file.renameTo(n);
    }

    /**
     * 清空temp文件夹下的所有图片
     */
    public static void deleteTemp() {
        new Thread(() -> {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DeepFace/temp/");
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                f.delete();
            }
        }).start();
    }
}

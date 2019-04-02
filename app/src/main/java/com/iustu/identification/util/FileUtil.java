package com.iustu.identification.util;

import java.io.File;
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
}

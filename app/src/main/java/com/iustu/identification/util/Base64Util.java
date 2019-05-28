package com.iustu.identification.util;

import android.util.Base64;

import com.iustu.identification.bean.ParameterConfig;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 将图片转换为Base64编码工具类
 */
public class Base64Util {
    /**
     * 转换为Base63编码
     * @param picPath 文件路径
     * @return
     */
    public static String convertBase64(String picPath) {
        if(picPath.isEmpty()){
            return null;
        }
        BufferedInputStream bfs=null;
        byte[] data;
        String result=null;
        try {
            bfs=new BufferedInputStream(new FileInputStream(picPath));
            data=new byte[bfs.available()];
            bfs.read(data);
            result = Base64.encodeToString(data,Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(bfs!=null){
                try {
                    bfs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}

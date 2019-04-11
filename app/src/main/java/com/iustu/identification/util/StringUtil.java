package com.iustu.identification.util;

import android.util.Log;

import com.iustu.identification.entity.PersionInfo;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * created by sgh, 2019-04-11
 */
public class StringUtil {

    /**
     * 该方法使用在批量导入图片的时候，用来提取图片中的人员信息
     * @param pictures 需要批量导入的图片的路径
     * @return 所有人员信息,但该人员信息只包含photoPath、name、other、gender、identity
     */
    public static ArrayList<PersionInfo> clipPictures(ArrayList<String> pictures) {
        ArrayList<PersionInfo> result = new ArrayList<>();
        for(String picture : pictures) {
            PersionInfo persionInfo = new PersionInfo();
            persionInfo.photoPath = picture;
            picture = picture.substring(0, picture.length() - 4);     // 去掉.jpg或者.png
            String[] s = picture.split("/");
            String info = s[s.length - 1];    // 获取图片中包含的信息
            s = info.split("_");
            if (s.length != 4) {
                ToastUtil.show("请按照 姓名_证件号码_性别_备注 的格式命名图片");
                return null;
            }
            persionInfo.name = s[0];
            persionInfo.identity = s[1];
            persionInfo.gender = s[2];
            persionInfo.other = s[3];
            Log.d("Stringutil", "clipPictures: " + persionInfo.photoPath);
            result.add(persionInfo);
        }

        return result;
    }
}

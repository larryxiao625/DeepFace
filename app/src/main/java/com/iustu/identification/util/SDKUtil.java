package com.iustu.identification.util;

import com.example.agin.facerecsdk.AttributeHandler;
import com.example.agin.facerecsdk.DetectHandler;
import com.example.agin.facerecsdk.HandlerFactory;
import com.example.agin.facerecsdk.SearchHandler;
import com.example.agin.facerecsdk.VerifyHandler;

/**
 * created by sgh, 2019-4-5
 *
 * 用来操作sdk的工具类
 */
public class SDKUtil {
    private static DetectHandler detectHandler;            // 人脸检测句柄
    private static VerifyHandler verifyHandler;            // 特征提取句柄
    private static SearchHandler searchHandler;           // 人脸搜索句柄
    private static AttributeHandler attributeHandler;     // 属性检测句柄

    // 初始化方法
    public static void init() {
        // 初始化人脸检测句柄
        detectHandler = (DetectHandler) HandlerFactory.createDetector("/sdcard/detect-Framework3-cpu-xxxx.model");
        detectHandler.setArgument("min_size 100\n pyramid_threshold 12\n factor 0.709\n thresholds 0.6 0.7 0.7\n");
        detectHandler.initial();

        //初始化特征提取句柄
        verifyHandler = (VerifyHandler) HandlerFactory.createVerify("/sdcard/feature-M1-Framework1-cpu-8289.model");
        verifyHandler.initial();

        // 初始化属性检测句柄
        attributeHandler = (AttributeHandler) HandlerFactory.createAttribute("\"/sdcard/detect-Framework3-cpu-xxxx.model\"");
        attributeHandler.initial();
    }
}

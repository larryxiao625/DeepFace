package com.iustu.identification.util;

import com.iustu.identification.bean.ChosenLibConfig;
import com.iustu.identification.bean.ParameterConfig;
import com.iustu.identification.entity.Account;

/**
 * created by sgh, 2019-4-2
 * 用来在登录的时候保存初始化的数据
 */
public class DataCache {
    private static ParameterConfig parameterConfig;         // 参数设置界面的数据
    private static Account account;             // 当前登录的账户
    private static ChosenLibConfig chosenLibConfig;           // 记录已被选中的人脸库

    // 该方法需要在登录成功时的回调中调用
    public static void initCache() {
        parameterConfig = ParameterConfig.getFromSP();
        chosenLibConfig = ChosenLibConfig.getFromSP();
    }
}

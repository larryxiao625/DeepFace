package com.iustu.identification.util;

import com.iustu.identification.bean.ParameterConfig;
import com.iustu.identification.entity.Account;

import java.util.HashSet;
import java.util.Set;

/**
 * created by sgh, 2019-4-2
 * 用来在登录的时候保存初始化的数据
 */
public class DataCache {
    private static ParameterConfig parameterConfig;         // 参数设置界面的数据
    private static Account account;                         // 当前登录的账户
    private static HashSet<String> chosenLibConfig;         // 记录已被选中的人脸库

    // 该方法需要在登录成功时的回调中调用
    public static void initCache(Account maccount) {
        parameterConfig = ParameterConfig.getFromSP();
        chosenLibConfig = (HashSet<String>) MSP.getInstance(MSP.SP_CHOSEN).getStringSet(MSP.SP_CHOSEN, new HashSet<String>());
        account = maccount;
    }

    // 该方法在App退出前调用，用来将内容写回
    public static void saveCache() {
        parameterConfig.save();
        MSP.getInstance(MSP.SP_CHOSEN).edit().putStringSet(MSP.SP_CHOSEN, chosenLibConfig);
    }

    public static ParameterConfig getParameterConfig() {
        return parameterConfig;
    }


    public static Account getAccount() {
        return account;
    }


    public static HashSet<String> getChosenLibConfig() {
        return chosenLibConfig;
    }

}

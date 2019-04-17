package com.iustu.identification.util;

import com.iustu.identification.bean.ParameterConfig;
import com.iustu.identification.bean.PreviewSizeConfig;
import com.iustu.identification.entity.Account;

import java.util.HashSet;

/**
 * created by sgh, 2019-4-2
 * 用来在登录的时候保存初始化的数据
 */
public class DataCache {
    private static ParameterConfig parameterConfig;         // 参数设置界面的数据
    private static Account account;                         // 当前登录的账户
    private static Account admin;           // 记录管理员账户
    private static HashSet<String> chosenLibConfig;         // 记录已被选中的人脸库
    private static HashSet<String> changedLib = new HashSet<>();         // 保存由"正在使用"转为"未使用"的libName
    private static PreviewSizeConfig previewSizeConfig;     //保存摄像头分辨率

    // 该方法需要在登录成功时的回调中调用
    public static void initCache(Account maccount) {
        admin = Account.getFromSP();
        parameterConfig = ParameterConfig.getFromSP();
        chosenLibConfig = (HashSet<String>) MSP.getInstance(MSP.SP_CHOSEN).getStringSet(MSP.SP_CHOSEN, new HashSet<String>());
        previewSizeConfig= PreviewSizeConfig.getFramSp();
        account = maccount;
        previewSizeConfig.save();
    }

    // 该方法在App退出前调用，用来将内容写回
    public static void saveCache() {
        previewSizeConfig.save();
        parameterConfig.save();
        admin.save();
        MSP.getInstance(MSP.SP_CHOSEN).edit().putStringSet(MSP.SP_CHOSEN, chosenLibConfig).apply();
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

    public static HashSet<String> getChangedLib() {
        return changedLib;
    }

    public static Account getAdmin() {
        return admin;
    }

}

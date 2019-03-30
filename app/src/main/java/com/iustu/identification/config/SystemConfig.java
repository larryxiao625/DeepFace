package com.iustu.identification.config;

import android.content.SharedPreferences;

import com.iustu.identification.api.ApiManager;
import com.iustu.identification.util.MSP;

/**
 * Created by Liu Yuchuan on 2017/11/13.
 */

public class SystemConfig {
    private static final String KEY_IP_ADDRESS = "ipAddress";
    private static final String KEY_WLAN_4G_SWITCH = "wlan4gSwitch";

    private static SystemConfig mInstance;

    private static final String name = "systemConfig";

    private SystemConfig(){
        SharedPreferences preferences = MSP.getInstance(name);
        ipAddress = preferences.getString(KEY_IP_ADDRESS, "http://121.49.110.28:9001/");
        wlan4gSwitchOn = preferences.getBoolean(KEY_WLAN_4G_SWITCH, false);
    }

    public static SystemConfig getInstance(){
        if(mInstance == null){
            mInstance = new SystemConfig();
        }
        return mInstance;
    }

    private String ipAddress;
    private boolean wlan4gSwitchOn;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        saveApi();
    }

    public boolean isWlan4gSwitchOn() {
        return wlan4gSwitchOn;
    }

    public void setWlan4gSwitchOn(boolean wlan4gSwitchOn) {
        this.wlan4gSwitchOn = wlan4gSwitchOn;
    }

    private void saveApi(){
        MSP.getInstance(name)
                .edit()
                .putString(KEY_IP_ADDRESS, ipAddress)
                .apply();
        ApiManager.getInstance()
                .updateBaseUrl(ipAddress);
    }

    public void save(){
        MSP.getInstance(name)
                .edit()
                .putString(KEY_IP_ADDRESS, ipAddress)
                .putBoolean(KEY_WLAN_4G_SWITCH, wlan4gSwitchOn)
                .apply();
    }
}

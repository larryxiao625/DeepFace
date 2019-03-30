package com.iustu.identification.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.iustu.identification.App;

/**
 * Created by Liu Yuchuan on 2017/12/15.
 */

public class NetWorkManager {
    public static final int NETWORK_WIFI = 0;
    public static final int NETWORK_MOBILE = 1;
    public static final int NETWORK_DISCONNECTED = 2;

    private NetWorkManager(){}

    public static int getNetWorkState(){

        ConnectivityManager manager = (ConnectivityManager) App.getContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(manager == null){
            return NETWORK_DISCONNECTED;
        }
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo == null){
            return NETWORK_DISCONNECTED;
        }

        if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
            return NETWORK_WIFI;
        }

        if(networkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
            return NETWORK_MOBILE;
        }

        return NETWORK_DISCONNECTED;
    }

    public static void enableWifi(boolean enable){
        WifiManager wifiManager = (WifiManager) App.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiManager == null){
            return;
        }

        if(wifiManager.isWifiEnabled() ^ enable){
            wifiManager.setWifiEnabled(enable);
        }
    }
}

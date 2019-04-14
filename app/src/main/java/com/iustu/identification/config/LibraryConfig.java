package com.iustu.identification.config;

import android.content.SharedPreferences;
import android.util.Log;

import com.iustu.identification.util.MSP;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Liu Yuchuan on 2017/11/29.
 */

public class LibraryConfig {
    private static final String KEY_CHOSEN_LIBS = "chosenLibs";

    private static final String name = "libraryConfig";

    private static HashSet<Integer> mChooseList;

    private static LibraryConfig mInstance;

    private LibraryConfig(){
        SharedPreferences preferences = MSP.getInstance(name);
        //mChooseList = preferences.getStringSet(KEY_CHOSEN_LIBS, new HashSet<>());
        //mChooseList = preferences.getString()
        //chosenLibs.remove(null);
    }

    public static LibraryConfig getInstance(){
        if(mInstance == null){
            synchronized (LibraryConfig.class){
                mInstance = new LibraryConfig();
            }
        }

        return mInstance;
    }

    public void save(){
        MSP.getInstance(name)
                .edit()
                //.putStringSet(KEY_CHOSEN_LIBS, new HashSet<>(chosenLibs))
                .apply();
    }

    public List<String> getChosenLibs() {
        return null;
        //return chosenLibs;
    }
}

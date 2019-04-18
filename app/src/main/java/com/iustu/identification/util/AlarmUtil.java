package com.iustu.identification.util;

import android.media.AudioManager;
import android.media.SoundPool;

import com.iustu.identification.R;

import java.util.HashMap;

/**
 * created by sgh, 2019-04-17
 * 用来管理警报
 */
public class AlarmUtil {
    SoundPool soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
    HashMap<Integer, Integer> soundPoolMap = new HashMap<Integer, Integer>();
    {
        //soundPoolMap.put(1, soundPool.load(this, R.raw.dingdong1, 1));
    }

    public static void alarm() {

    }
}

package com.iustu.identification.util;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.util.Log;

import com.iustu.identification.R;
import com.iustu.identification.bean.ParameterConfig;

import java.util.HashMap;

/**
 * created by sgh, 2019-04-17
 * 用来管理警报
 */
public class AlarmUtil {
    private static Context context;
    private static Vibrator vibrator;
    private static SoundPool soundPool;
    private static HashMap<Integer, Integer> soundPoolMap;
    public static void init (Context con) {
        soundPoolMap = new HashMap<Integer, Integer>();
        context = con;
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
        vibrator = (Vibrator)context.getSystemService(Service.VIBRATOR_SERVICE);
    }

    public static void alarm() {
        int type = DataCache.getParameterConfig().getAlarmType();
        switch (type) {
            case ParameterConfig.ONLYMP3:
                alarmMP3();
                break;
            case ParameterConfig.ONLYSHAKE:
                alarmShake();
                break;
            case ParameterConfig.MP3ANDSHAKE:
                alarmShakeAndMP3();
                break;
        }
    }
    private static void alarmMP3() {
        soundPoolMap.put(1, soundPool.load(context, R.raw.myalarm, 1));
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                synchronized (AlarmUtil.class) {
                    soundPool.play(soundPoolMap.get(1), 1, 1, 0, 1, 2.0f);
                }
            }
        });
    }

    private static void alarmShake() {
        synchronized (AlarmUtil.class) {
            vibrator.vibrate(2000);
        }
    }

    private static void alarmShakeAndMP3() {
        soundPoolMap.put(1, soundPool.load(context, R.raw.myalarm, 1));
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                synchronized (AlarmUtil.class) {
                    soundPool.play(soundPoolMap.get(1), 1, 1, 0, 1, 2.0f);
                    vibrator.vibrate(2000);
                }
            }
        });
    }

    public static void destory() {
        context = null;
        soundPool.unload(soundPoolMap.get(1));
        soundPool = null;
        soundPoolMap = null;
    }
}

package com.iustu.identification.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.agin.facerecsdk.AttributeHandler;
import com.example.agin.facerecsdk.BlurHandler;
import com.example.agin.facerecsdk.DetectHandler;
import com.example.agin.facerecsdk.DetectResult;
import com.example.agin.facerecsdk.FacerecUtil;
import com.example.agin.facerecsdk.FeatureResult;
import com.example.agin.facerecsdk.HandlerFactory;
import com.example.agin.facerecsdk.SearchDBItem;
import com.example.agin.facerecsdk.SearchHandler;
import com.example.agin.facerecsdk.SearchResultItem;
import com.example.agin.facerecsdk.TrackerHandler;
import com.example.agin.facerecsdk.VerifyHandler;
import com.iustu.identification.App;
import com.iustu.identification.bean.ParameterConfig;
import com.iustu.identification.entity.PersionInfo;

import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


/**
 * created by sgh, 2019-4-5
 *
 * 用来操作sdk的工具类
 */
public class SDKUtil {
    public static final int MORE_ONE_FACE = 224;        // 添加照片的时候判断含有多个人脸
    public static final int NOFACE = 223;            // 添加照片的时候判断没有人脸
    public static final int ISTHESAME = 222;          // 添加照片的时候判断为同一个人，允许添加
    public static final int NOTTHESAME = 225;         // 添加照片的时候判断为不同人，不允许添加
    public static final int HASADDED = 221;        // 在添加人脸的时候表示人脸库中已经含有该人脸
    private static Activity context;
    private static DetectHandler detectHandler;            // 人脸检测句柄
    private static VerifyHandler verifyHandler;            // 特征提取句柄
    private static AttributeHandler attributeHandler;     // 属性检测句柄

    private static HashMap<String, SearchHandler> searchHandlerCache = new HashMap<>();  // 用来记录被创建过的搜索句柄，key为libName

    private static TrackerHandler trackerHandler;   //人脸跟踪句柄
    private static BlurHandler blurHandler;     //模糊图片检测句柄

    // 初始化方法
    public static void init() {
        // 初始化人脸检测句柄
        detectHandler = (DetectHandler) HandlerFactory.createDetector("/sdcard/detect-Framework3-cpu-xxxx.model");
        detectHandler.setThreadNum(HandlerFactory.FrameWorkType.NCNN, 4);
        detectHandler.initial();

        //初始化特征提取句柄
        verifyHandler = (VerifyHandler) HandlerFactory.createVerify("/sdcard/feature-M1-Framework1-cpu-8289.model");
        verifyHandler.initial();

        // 初始化属性检测句柄
        attributeHandler = (AttributeHandler) HandlerFactory.createAttribute("/sdcard/attr-Framework1-cpu-0a15-bc0a.model");
        attributeHandler.initial();

        // 初始化人脸跟踪检测句柄
        trackerHandler= (TrackerHandler) HandlerFactory.createTracker(2000000);

        //  初始化人脸模糊检测句柄
        blurHandler= (BlurHandler) HandlerFactory.createBlur("/sdcard/blur-M1_8289-Framework1-cpu-60ae.model");
    }

    public static void initSdk(Context context) {
        SDKUtil.context = (Activity)context;
        // Facerec初始化
        FacerecUtil.init(context);

        String file = Environment.getExternalStorageDirectory().getAbsolutePath();
        // 设置生成的证书文件的路径
        FacerecUtil.generateLicense(file + "/license_key.txt");
        updateFile(file + "/license_key.txt");
        // 设置license的路径
        FacerecUtil.setLicensePath(file);
        if (FacerecUtil.facerecsdkValid()) {
            Log.d("testSdk","sdk合法");
            Log.d("initSdk", FacerecUtil.getLicenseValidTime() + "");
            SDKUtil.init();
        }else if(!FacerecUtil.facerecsdkValid()){
            Toast.makeText(App.getContext(),"SDK不合法，请检查",Toast.LENGTH_SHORT).show();
        }else if(FacerecUtil.getLicenseValidTime()<=0){
            Toast.makeText(App.getContext(),"SDK已过期，请重新授权",Toast.LENGTH_SHORT).show();
        }
    }

    // 销毁句柄，在Application的onDestory方法中调用
    public static void destory() {
        detectHandler.destroy();
        verifyHandler.destroy();
        attributeHandler.destroy();
        ArrayList<SearchHandler> searchHandlers = (ArrayList<SearchHandler>) searchHandlerCache.values();
        for (SearchHandler handler : searchHandlers) {
            handler.destroy();
        }
    }
    private static void updateFile(String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        context.getApplicationContext().sendBroadcast(scanIntent);
    }

    public static DetectHandler getDetectHandler() {
        detectHandler.setArgument(DataCache.getParameterConfig().gengrateArguments());
        return detectHandler;
    }

    public static int feature(DetectResult detectResult, FeatureResult featureResult) {
        synchronized (SDKUtil.class) {
            int result = verifyHandler.extractFeature(detectResult, featureResult);
            return result;
        }
    }

    public static VerifyHandler getVerifyHandler() {
        synchronized (SDKUtil.class) {
            return verifyHandler;
        }
    }

    /**
     * 获取图片的Feature的方法,在往人脸库添加新成员的时候调用{@link RxUtil}
     * @param persionInfo 需要添加的人
     */
    public static int sdkDoPerson(PersionInfo persionInfo) {
        DetectResult detectResult=new DetectResult();
        SDKUtil.getDetectHandler().faceDetector(persionInfo.photoPath,detectResult);
        FeatureResult featureResult=new FeatureResult();
        int result = getVerifyHandler().extractFeature(detectResult,featureResult);
        if (result == -1)
            return result;
        if (featureResult.getFeat(0).size() < 0) {
            return -1;
        }
        float[] floats = featureResult.getFeat(0).get(0);
        persionInfo.feature = Arrays.asList(floats).toString();
        SearchDBItem searchDBItem = new SearchDBItem();
        searchDBItem.feat = floats;
        searchDBItem.image_id = persionInfo.image_id;
        SearchHandler searchHandler = null;
        if (searchHandlerCache.get(persionInfo.libName) == null) {
            searchHandler = (SearchHandler)HandlerFactory.createSearcher("/sdcard/DeepFace/" + persionInfo.libName, 0, 1);
            searchHandlerCache.put(persionInfo.libName, searchHandler);
        } else {
            searchHandler = searchHandlerCache.get(persionInfo.libName);
        }
        result = searchHandler.searchAdd(searchDBItem);
        if (result == -1)
            return result;
        return result;
    }

    /**
     * 在往人脸库批量导入的时候调用{@link RxUtil}
     * @param persionInfo 需要添加的人
     */
    public static boolean sdkDoBatchPersion(PersionInfo persionInfo) {
        String compressPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DeepFace/temp/" + persionInfo.photoPath;
        //String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DeepFace/" + persionInfo.libName + "/" + persionInfo.photoPath;
        DetectResult detectResult=new DetectResult();
        int result = SDKUtil.getDetectHandler().faceDetector(compressPath,detectResult);
        //int result = SDKUtil.getDetectHandler().faceDetector(path,detectResult);
        if (result <= 0)
            return false;
        FeatureResult featureResult=new FeatureResult();
        result = getVerifyHandler().extractFeature(detectResult,featureResult);
        if (result == -1) {
            return false;
        }

        if (featureResult.getFeat(0).size() < 0) {
            return false;
        }
        float[] floats = featureResult.getFeat(0).get(0);
        persionInfo.feature = Arrays.asList(floats).toString();
        String image_id = System.currentTimeMillis() + "";
        Log.d("batch", "sdkDoBatchPersion: " + image_id);
        persionInfo.image_id = image_id;
        SearchDBItem searchDBItem = new SearchDBItem();
        searchDBItem.feat = floats;
        searchDBItem.image_id = persionInfo.image_id;
        SearchHandler searchHandler = null;
        if (searchHandlerCache.get(persionInfo.libName) == null) {
            searchHandler = (SearchHandler)HandlerFactory.createSearcher("/sdcard/DeepFace/" + persionInfo.libName, 0, 1);
            searchHandlerCache.put(persionInfo.libName, searchHandler);
        } else {
            searchHandler = searchHandlerCache.get(persionInfo.libName);
        }
        result = searchHandler.searchAdd(searchDBItem);
        if (result == -1)
            return false;
        return true;
    }

    /**
     * 在添加照片的时候判断添加的照片是否与目标人员是否为同一人
     */
    public static int checkIsSamePersion(PersionInfo persionInfo, String photoPath) {
        String[] photos = persionInfo.photoPath.split(";");
        String photo = "/sdcard/DeepFace/" + persionInfo.libName + "/" + photos[0];
        DetectResult detectResult2=new DetectResult();  // 新照片的
        DetectResult detectResult1 = new DetectResult();  // 老照片的
        SDKUtil.getDetectHandler().faceDetector(photoPath,detectResult2);
        SDKUtil.getDetectHandler().faceDetector(photo,detectResult1);
        if (detectResult2.points.size() == 0)
            return NOFACE;
        if (detectResult2.points.size() > 1)
            return MORE_ONE_FACE;
        FeatureResult featureResult2 = new FeatureResult();
        FeatureResult featureResult1 = new FeatureResult();
        getVerifyHandler().extractFeature(detectResult2, featureResult2);
        getVerifyHandler().extractFeature(detectResult1, featureResult1);
        float[] floats2 = featureResult2.getFeat(0).get(0);
        float[] floats1 = featureResult1.getFeat(0).get(0);
        float score = getVerifyHandler().verifyFeature(floats1, floats2);
        return score > 0.1 ? NOTTHESAME : ISTHESAME;
    }


    /**
     * 人脸特征识别方法
     * @param detectResult 人脸识别结果
     * @return
     */
    public static FeatureResult featureResult(ArrayList<DetectResult> detectResult){
            FeatureResult featureResult=new FeatureResult();
            synchronized (SDKUtil.class) {
                verifyHandler.extractFeatureBatch(detectResult, featureResult);
            }
            return featureResult;
    }

    /**
     * 人脸识别方法
     * @param picPaths 待识别路径集合
     * @return
     */
    public static ArrayList<DetectResult> detectFace(ArrayList<String> picPaths){
        ArrayList<DetectResult> detectResults=new ArrayList<>();
            for(int i=0;i<picPaths.size();i++){
                DetectResult detectResult=new DetectResult();
                int faceNum=SDKUtil.getDetectHandler().faceDetector(picPaths.get(0),detectResult);
                Log.d("capturePic", "人脸检测结果是" + faceNum);
                if(faceNum!=0){
                    detectResults.add(detectResult);
                }
            }
            if(detectResults.isEmpty()){
                return null;
            }else {
                return detectResults;
            }
    }

    /**
     * 从人脸库删除人脸
     * @param persionInfo 需要删除的人脸库
     * @return 是否删除成功
     */
    public static int deletePerFromLibrary(PersionInfo persionInfo) {
        SearchHandler searchHandler = (SearchHandler)HandlerFactory.createSearcher("/sdcard/DeepFace/" + persionInfo.libName, 0, 1);
        int result = searchHandler.searchDelete(persionInfo.image_id);
        searchHandler.destroy();
        return result;
    }

}

package com.iustu.identification.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.example.agin.facerecsdk.AttributeHandler;
import com.example.agin.facerecsdk.DetectHandler;
import com.example.agin.facerecsdk.DetectResult;
import com.example.agin.facerecsdk.FacerecUtil;
import com.example.agin.facerecsdk.FeatureResult;
import com.example.agin.facerecsdk.HandlerFactory;
import com.example.agin.facerecsdk.SearchDBItem;
import com.example.agin.facerecsdk.SearchHandler;
import com.example.agin.facerecsdk.SearchResultItem;
import com.example.agin.facerecsdk.VerifyHandler;
import com.iustu.identification.entity.PersionInfo;

import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;


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
    private static SearchHandler searchHandler;           // 人脸搜索句柄
    private static AttributeHandler attributeHandler;     // 属性检测句柄
//    public static String path=Environment.getExternalStorageDirectory()+"/DeepFace/faceLib";

    // 初始化方法
    public static void init() {
        // 初始化人脸检测句柄
        detectHandler = (DetectHandler) HandlerFactory.createDetector("/sdcard/detect-Framework3-cpu-xxxx.model");
        //detectHandler.setArgument("min_size 100\n pyramid_threshold 12\n factor 0.709\n thresholds 0.6 0.7 0.7\n");
        detectHandler.initial();

        //初始化特征提取句柄
        verifyHandler = (VerifyHandler) HandlerFactory.createVerify("/sdcard/feature-M1-Framework1-cpu-8289.model");
        verifyHandler.initial();

        // 初始化属性检测句柄
        attributeHandler = (AttributeHandler) HandlerFactory.createAttribute("/sdcard/attr-Framework1-cpu-0a15-bc0a.model");
        attributeHandler.initial();

//        File file=new File(path);
//        if(!file.exists()){
//            file.mkdirs();
//        }
//        searchHandler= (SearchHandler) HandlerFactory.createSearcher(path,0,1);
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
        }
        SDKUtil.init();
    }

    // 销毁句柄，在Application的onDestory方法中调用
    public static void destory() {
        detectHandler.destroy();
        verifyHandler.destroy();
        attributeHandler.destroy();
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

    public static VerifyHandler getVerifyHandler() {
        return verifyHandler;
    }

//    public static SearchHandler getSearchHandler() {
//        return searchHandler;
//    }

    public static AttributeHandler getAttributeHandler() {
        return attributeHandler;
    }


    /**
     * 获取图片的Feature的方法,在往人脸库添加新成员的时候调用{@link RxUtil}
     * @param persionInfo 需要添加的人
     */
    public static int sdkDoPerson(PersionInfo persionInfo) {
        DetectResult detectResult=new DetectResult();
        SDKUtil.getDetectHandler().faceDetector(persionInfo.photoPath,detectResult);
        FeatureResult featureResult=new FeatureResult();
        verifyHandler.extractFeature(detectResult,featureResult);
        float[] floats = featureResult.getFeat(0).get(0);
        persionInfo.feature = Arrays.asList(floats).toString();
        SearchDBItem searchDBItem = new SearchDBItem();
        searchDBItem.feat = floats;
        searchDBItem.image_id = persionInfo.image_id;
        SearchHandler searchHandler = (SearchHandler)HandlerFactory.createSearcher("/sdcard/DeepFace/" + persionInfo.libName, 0, 1);
        ArrayList<SearchResultItem> searchResult = new ArrayList<>();
        // 首先检测人脸库中是否已经含有该人脸特征
        int searchRes = searchHandler.searchFind(floats, 1, searchResult, DataCache.getParameterConfig().getFilterScore());
        Log.d("sdk", "searchRes " + searchRes);
        Log.d("sdk", "serachResultLength" + searchResult.size());
        // 说明该人脸库中已经含有了该人脸
        if (searchResult.size() > 0) {
            if (!searchHandler.isDestroy())
                searchHandler.destroy();
            return HASADDED;
        }
        int result = searchHandler.searchAdd(searchDBItem);
        if (!searchHandler.isDestroy())
            searchHandler.destroy();
        return result;
    }

    /**
     * 在往人脸库批量导入的时候调用{@link RxUtil}
     * @param persionInfos 需要添加的人
     */
    public static void sdkDoBatchPersion(ArrayList<PersionInfo> persionInfos) {
        ArrayList<SearchDBItem> searchDBItems = new ArrayList<>();
        for (PersionInfo persionInfo : persionInfos) {
            DetectResult detectResult=new DetectResult();
            SDKUtil.getDetectHandler().faceDetector(persionInfo.photoPath,detectResult);
            FeatureResult featureResult=new FeatureResult();
            verifyHandler.extractFeature(detectResult,featureResult);
            float[] floats = featureResult.getFeat(0).get(0);
            persionInfo.feature = Arrays.asList(floats).toString();
            SearchDBItem searchDBItem = new SearchDBItem();
            searchDBItem.feat = floats;
            searchDBItem.image_id = persionInfo.image_id;
            searchDBItems.add(searchDBItem);
        }
        SearchHandler searchHandler = (SearchHandler)HandlerFactory.createSearcher("/sdcard/DeepFace/" + persionInfos.get(0).libName, 0, 1);
        searchHandler.searchBuildLib(searchDBItems);
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
        verifyHandler.extractFeature(detectResult2, featureResult2);
        verifyHandler.extractFeature(detectResult1, featureResult1);
        float[] floats2 = featureResult2.getFeat(0).get(0);
        float[] floats1 = featureResult1.getFeat(0).get(0);
        float score = verifyHandler.verifyFeature(floats1, floats2);
        Log.d("sdk", "checkIsSamePersion: " + score);
        return score > 0.1 ? NOTTHESAME : ISTHESAME;
    }


    /**
     * 人脸特征识别方法
     * @param detectResult 人脸识别结果
     * @return
     */
    public static FeatureResult featureResult(ArrayList<DetectResult> detectResult){
            FeatureResult featureResult=new FeatureResult();
            SDKUtil.getVerifyHandler().extractFeatureBatch(detectResult,featureResult);
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
                if(faceNum!=0){
                    detectResults.add(detectResult);
                }
            }
            return detectResults;
    }
}

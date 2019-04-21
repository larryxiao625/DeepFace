package com.iustu.identification.ui.main.camera.prenster;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.agin.facerecsdk.DetectResult;
import com.example.agin.facerecsdk.FeatureResult;
import com.example.agin.facerecsdk.HandlerFactory;
import com.example.agin.facerecsdk.SearchDBItem;
import com.example.agin.facerecsdk.SearchHandler;
import com.example.agin.facerecsdk.SearchResultItem;
import com.iustu.identification.bean.ParameterConfig;
import com.iustu.identification.util.DataCache;
import com.iustu.identification.util.FileUtil;
import com.iustu.identification.util.SDKUtil;
import com.iustu.identification.util.SqliteUtil;
import com.iustu.identification.util.TextUtil;
import com.jiangdg.usbcamera.UVCCameraHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static java.lang.Math.sqrt;

public class CapturePicService extends Service {
    UVCCameraHelper cameraHelper=UVCCameraHelper.getInstance();
    static String rootPath= Environment.getExternalStorageDirectory()+"/DeepFace";
    static String cutPath=Environment.getExternalStorageDirectory()+"/DeepFace/Cut/";
    static String tempPath=Environment.getExternalStorageDirectory()+"/DeepFace/temp/";
    private CameraPrenster cameraPrenster;
    volatile int captureNum=0;
    int picQuality=0;
    Calendar tempBestCalender;
    CaptureBind mBind;
    Disposable disposable;
    ArrayList<DetectResult> tempDetectResults=new ArrayList<>();
    ArrayList<SearchHandler> searchHandlers=new ArrayList<>();
    String tempBestPicPath;

    public class CaptureBind extends Binder{
        public CapturePicService getService(){
            return CapturePicService.this;
        }
        public void setOnMyDevConnectListener(CameraPrenster cameraPrenster){
            CapturePicService.this.cameraPrenster=cameraPrenster;
        }
    }
    File cutFile=new File(cutPath);
    FileOutputStream fos;
    @Override
    public void onCreate() {
        super.onCreate();
        mBind=new CaptureBind();
        HashSet<String> libPat= DataCache.getChosenLibConfig();
        Log.d("test", "onCreate: " + libPat.toString());
        for(String libPath:libPat){
            SearchHandler searchHandler= (SearchHandler) HandlerFactory.createSearcher(rootPath+"/"+libPath,0,1);
            searchHandlers.add(searchHandler);
        }
        capturePic();
//        ArrayList<String> capturesPic = new ArrayList<>();       // 保存抓拍到的图片
//        Observable.interval(200, TimeUnit.MILLISECONDS)
//                .map(new Function<Long, Object>() {
//                    @Override
//                    public Object apply(Long aLong) {
//                        Calendar calendar=Calendar.getInstance();
//                        String picPath=rootPath+"/"+System.currentTimeMillis()+".jpg";
//                        cameraHelper.capturePicture(picPath, picPath1 -> {
//                            captureNum ++;
//                            capturesPic.add(picPath);
//                        });
//                        if (captureNum == 5) {
//                            captureNum = 0;
//                            ArrayList<String> f = new ArrayList<>();
//                            for (int i = 0; i < capturesPic.size(); i ++) {
//                                f.add(capturesPic.get(i));
//                            }
//                            capturesPic.clear();
//                            return f;
//                        } else {
//                            return picPath;
//                        }
//                    }
//                }).subscribeOn(AndroidSchedulers.mainThread())
//                  .observeOn(Schedulers.io())
//                  .subscribe(new Observer<Object>() {
//
//                      @Override
//                      public void onSubscribe(Disposable d) {
//                          disposable = d;
//                      }
//
//                      @Override
//                      public void onNext(Object o) {
//                          if (o.getClass().equals(String.class)) {
//                              return;
//                          }
//                          ArrayList<String> compressedPictures = new ArrayList<>();
//                          Log.d("bestPic", "五张图片是 " + ((ArrayList<String>)o).toString());
//                          // 压缩五张图片
//                          for (int i = 0; i < ((ArrayList<String>)o).size(); i ++) {
//                              File file = new File(((ArrayList<String>)o).get(i));
//                              String fileName = file.getName();
//                              String finalpath = tempPath + "/" + fileName;
//                              Log.d("capturePic", "图片名称是" + fileName);
//                              FileUtil.copyCompressedBitmap(file.getAbsolutePath(), finalpath);
//                              compressedPictures.add(finalpath);
//                          }
//
//                          Log.d("capturePic", "压缩后的数量是" + compressedPictures.size());
//                          // 提取特征
//                          ArrayList<DetectResult> results;
//                          results = SDKUtil.detectFace(compressedPictures);
//                          Log.d("capturePic", "人脸检测结果是" + results.size());
//                          // 选取最优
//                          float max = -1.0f;
//                          int bestIndex = -1;
//                          if (results.size() == 0)
//                              return;
//                          for (int i = 0; i < results.size(); i ++) {
//                              DetectResult detectResult = results.get(i);
//                              float m = detectResult.points.get(0).x[1] - detectResult.points.get(0).x[0];
//                              Log.d("capturePic", "两眼距离是" + m);
//                              if (max <= m) {
//                                  bestIndex = i;
//                                  max = m;
//                              }
//                          }
//
//                          // 选出的最优图片的路径
//                          String bestPicture = ((ArrayList<String>)o).get(bestIndex);
//                          Log.d("bestPic", "最优图片是 " + bestPicture);
//
//                      }
//
//                      @Override
//                      public void onError(Throwable e) {
//
//                      }
//
//                      @Override
//                      public void onComplete() {
//
//                      }
//                  });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @SuppressLint("CheckResult")
    public void capturePic() {
        Observable.interval(200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable=d;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.d("Camera", TextUtil.getDateString2(Calendar.getInstance().getTime()));
                        Calendar calendar=Calendar.getInstance();
                        String picPath=rootPath+"/"+TextUtil.dateMessage(calendar.getTime())+".jpg";
                        cameraHelper.capturePicture(picPath, picPath1 -> {
                            captureNum++;
                            Bitmap bitmap=Bitmap.createBitmap(BitmapFactory.decodeFile(picPath1));
                            try {
                                bitmap.compress(Bitmap.CompressFormat.JPEG,50,new FileOutputStream(tempPath+TextUtil.dateMessage(calendar.getTime())+".jpg"));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            ArrayList<String> picPaths=new ArrayList<>();
                            picPaths.add(tempPath+TextUtil.dateMessage(calendar.getTime())+".jpg");
                            ArrayList<DetectResult> detectResults=SDKUtil.detectFace(picPaths);
                            if(detectResults.get(0).points.size()!=0) {
                                if (((detectResults.get(0).getPoints().get(0).x)[1] - (detectResults.get(0).getPoints().get(0).x)[0]) > picQuality) {
                                    picQuality = (int) ((detectResults.get(0).getPoints().get(0).x)[1] - (detectResults.get(0).getPoints().get(0).x)[0]);
                                    tempDetectResults.clear();
                                    tempDetectResults = detectResults;
                                    tempBestCalender = calendar;
                                    tempBestPicPath=tempPath+TextUtil.dateMessage(calendar.getTime())+".jpg";
                                }
                            }
                            if(captureNum==5){
                                getTheBestPic(tempBestPicPath,tempDetectResults,tempBestCalender,tempDetectResults.get(0).points.size());
                                captureNum=0;
                                picQuality=0;
                                tempBestCalender=null;
                                tempDetectResults.clear();
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getTheBestPic(String picPath,ArrayList<DetectResult> detectResults,Calendar calendar,int faceNum){
        if(faceNum!=0){
            Log.d("Camera","人脸数量"+detectResults.get(0).size());
            getCutPicture(picPath,detectResults.get(0),calendar,detectResults.get(0).points.size());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("CapturePicService","onBind");
        return mBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
        Log.d("Camera","onDestroy");
    }
    public void getVerify(DetectResult detectResult,Calendar calendar,String photoPath,String originalPhoto){
        ArrayList<DetectResult> temp=new ArrayList<>();
        temp.add(detectResult);
            FeatureResult featureResult=SDKUtil.featureResult(temp);
            Log.d("CameraVerify", String.valueOf(featureResult.getAllFeats().size()));
            if(featureResult.getAllFeats().size()!=0){
                for(ArrayList<float[]> arrayList:featureResult.getAllFeats()){
                    for(float[] floats:arrayList){
                        searchFace(floats,calendar,photoPath,originalPhoto);
                    }
                }
            }
    }
    public void searchFace(float[] feat,Calendar calendar,String photoPath,String originalPhoto){
        ArrayList<SearchResultItem> searchResultItems=new ArrayList<>();
        SearchResultItem searchResultItem = null;
        for(SearchHandler searchHandler:searchHandlers){
            searchHandler.searchFind(feat,1,searchResultItems, DataCache.getParameterConfig().getThresholdQuanity());
        }
        Log.d("CameraSearchResult", String.valueOf(searchResultItems.size()));
        if(!searchResultItems.isEmpty()) {
            for (SearchResultItem temp : searchResultItems) {
                Log.d("CameraSearch", String.valueOf(temp.score));
                if (searchResultItem == null) {
                    searchResultItem = temp;
                } else if (temp.score > searchResultItem.score) {
                    searchResultItem = temp;
                }
            }
            searchResultItem.score= (float) (sqrt(searchResultItem.score - 0.71) /sqrt(1.0 - 0.71)* 0.15 + 0.85);
            Log.d("CameraSearch", String.valueOf(searchResultItems.get(0).score));
            Log.d("CameraSearch",searchResultItems.get(0).image_id);
            SqliteUtil.insertComparedItem(searchResultItem,calendar.getTime(),photoPath, cameraPrenster, originalPhoto);
        }
    }

    public void getCutPicture(String originalPhoto, DetectResult detectResult,Calendar calendar,int num){
        if(!cutFile.exists()){
            cutFile.mkdirs();
        }
        for(int i=0;i<num;i++) {
            String cutPathName=cutPath+TextUtil.dateMessage(calendar.getTime())+"_"+i+".jpg";
            int height=(detectResult.getRects().get(i).bottom> ParameterConfig.getFromSP().getDpiHeight()? ParameterConfig.getFromSP().getDpiHeight():detectResult.getRects().get(i).bottom)-(detectResult.getRects().get(i).top<0? 0:detectResult.getRects().get(i).top);
            int width=(detectResult.getRects().get(i).right> ParameterConfig.getFromSP().getDpiWidth()? ParameterConfig.getFromSP().getDpiWidth():detectResult.getRects().get(i).right)-(detectResult.getRects().get(i).left<0? 0:detectResult.getRects().get(i).left);
            height= (int) (height*1.4);
            width= (int) (width*1.4);
            Log.d("CameraLeft", String.valueOf(detectResult.getRects().get(i).left));
            Log.d("CameraRight", String.valueOf(detectResult.getRects().get(i).right));
            Log.d("CameraTop", String.valueOf(detectResult.getRects().get(i).top));
            Log.d("CameraBottom", String.valueOf(detectResult.getRects().get(i).bottom));
            Log.d("CameraHeight", String.valueOf(height));
            Log.d("CameraWidth", String.valueOf(width));
            Bitmap bitmap = Bitmap.createBitmap(BitmapFactory.decodeFile(originalPhoto), (detectResult.getRects().get(i).left/1.2)<0? 0: (int) (detectResult.getRects().get(i).left /1.2), (detectResult.getRects().get(i).top/1.2<0)? 0: (int) (detectResult.getRects().get(i).top/1.2),width>(ParameterConfig.getFromSP().getDpiWidth()-detectResult.getRects().get(i).left)?(ParameterConfig.getFromSP().getDpiWidth()-detectResult.getRects().get(i).left):width,height>(ParameterConfig.getFromSP().getDpiHeight()-detectResult.getRects().get(i).top)?(ParameterConfig.getFromSP().getDpiHeight()-detectResult.getRects().get(i).top):height);
            try {
                File file=new File(cutPathName);
                fos=new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                getVerify(detectResult,calendar,cutPathName,originalPhoto);
                SqliteUtil.insertFaceCollectionItem(cutPathName, originalPhoto, calendar.getTime(),cameraPrenster);
                fos.flush();
                fos.close();
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }

}

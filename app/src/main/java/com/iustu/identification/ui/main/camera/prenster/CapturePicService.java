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
import com.example.agin.facerecsdk.SearchHandler;
import com.example.agin.facerecsdk.SearchResultItem;
import com.iustu.identification.bean.ParameterConfig;
import com.iustu.identification.util.AlarmUtil;
import com.iustu.identification.util.DataCache;
import com.iustu.identification.util.FileUtil;
import com.iustu.identification.util.SDKUtil;
import com.iustu.identification.util.SqliteUtil;
import com.iustu.identification.util.TextUtil;
import com.jiangdg.usbcamera.UVCCameraHelper;
import com.serenegiant.utils.ThreadPool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

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
    volatile HashMap<String, SearchHandler> searchHandlers=new HashMap<>();
    ArrayList<String> libNames = new ArrayList<>();

    String tempBestPicPath;
    volatile List<Calendar> calendars=new ArrayList<>();
    volatile List<String> capturePicPaths=new ArrayList<>();

    public class CaptureBind extends Binder{
        public CapturePicService getService(){
            return CapturePicService.this;
        }
        public void setOnMyDevConnectListener(CameraPrenster cameraPrenster){
            CapturePicService.this.cameraPrenster=cameraPrenster;
        }
    }
    FileOutputStream fos;
    ThreadCanshu threadCanshu;
    @Override
    public void onCreate() {
        super.onCreate();
        mBind=new CaptureBind();
        HashSet<String> libPat= DataCache.getChosenLibConfig();
        for(String libPath:libPat){
            if (searchHandlers.get(libPath) == null) {
                SearchHandler searchHandler= (SearchHandler) HandlerFactory.createSearcher(rootPath+"/"+libPath,0,1);
                searchHandlers.put(libPath, searchHandler);
            }
            libNames.add(libPath);
        }
        EventBus.getDefault().register(this);
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
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable=d;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Calendar calendar=Calendar.getInstance();
                        String fileName=TextUtil.dateMessage(calendar.getTime())+"_"+captureNum+".jpg";
                        String picPath=rootPath+"/"+fileName;
                        cameraHelper.capturePicture(picPath, picPath1 -> {
                            calendars.add(calendar);
                            capturePicPaths.add(picPath1);
                        });
                        picPath = null;
                        captureNum++;
                        if(captureNum==5){
                            List<String> tempCapturePicPath=new ArrayList<>();
                            tempCapturePicPath.addAll(capturePicPaths);
                            List<Calendar> tempCalender=new ArrayList<>();
                            tempCalender.addAll(calendars);
                            threadCanshu=new ThreadCanshu(tempCalender,tempCapturePicPath);
                            EventBus.getDefault().post(threadCanshu);
                            capturePicPaths.clear();
                            calendars.clear();
                            captureNum=0;
                        }
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
        EventBus.getDefault().unregister(this);
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FileUtil.deleteTemp();
        disposable.dispose();
    }
    public void getVerify(DetectResult detectResult,Calendar calendar,String photoPath,String originalPhoto){
        ArrayList<DetectResult> temp=new ArrayList<>();
        temp.add(detectResult);
            FeatureResult featureResult=SDKUtil.featureResult(temp);
            if(featureResult.getAllFeats().size()!=0){
                for(ArrayList<float[]> arrayList:featureResult.getAllFeats()){
                    for(float[] floats:arrayList){
                        searchFace(floats,calendar,photoPath,originalPhoto);
                    }
                }
            }
    }
    public void searchFace(float[] feat,Calendar calendar,String photoPath,String originalPhoto){
//        ArrayList<SearchHandler> handlers = new ArrayList<>();
//        ArrayList<String> libs = new ArrayList<>();
//        for(String libPath:libPat){
//            SearchHandler searchHandler= (SearchHandler) HandlerFactory.createSearcher(rootPath+"/"+libPath,0,1);
//            handlers.add(searchHandler);
//            libs.add(libPath);
//        }
        for(int i = 0; i < libNames.size(); i ++){
            SearchResultItem searchResultItem = null;
            ArrayList<SearchResultItem> searchResultItems=new ArrayList<>();
            searchHandlers.get(libNames.get(i)).searchFind(feat,1,searchResultItems, DataCache.getParameterConfig().getThresholdQuanity());
            if(!searchResultItems.isEmpty()) {
                for (SearchResultItem temp : searchResultItems) {
                    if (searchResultItem == null) {
                        searchResultItem = temp;
                    } else if (temp.score > searchResultItem.score) {
                        searchResultItem = temp;
                    }
                }
                searchResultItem.score= (float) (sqrt(searchResultItem.score - DataCache.getParameterConfig().getThresholdQuanity()) /sqrt(1.0 - DataCache.getParameterConfig().getThresholdQuanity())* 0.15 + 0.85);
                if(searchResultItem.score > DataCache.getParameterConfig().getFactor())
                    AlarmUtil.alarm();
                    SqliteUtil.insertComparedItem(libNames.get(i), searchResultItem,calendar.getTime(),photoPath, cameraPrenster, originalPhoto);
            }
        }

    }

    public void getCutPicture(String originalPhoto, DetectResult detectResult,Calendar calendar,int num){

        for(int i=0;i<num;i++) {
            String cutPathName=cutPath+TextUtil.dateMessage(calendar.getTime())+"_"+i+".jpg";
            int centerX = (detectResult.getRects().get(i).left + detectResult.getRects().get(i).right) / 2;
            int centerY = (detectResult.getRects().get(i).top + detectResult.getRects().get(i).bottom) / 2;
            // 获取新的坐标系下四个边的坐标
            int left = (int)((detectResult.getRects().get(i).left - centerX) * 1.5);
            int right = (int)((detectResult.getRects().get(i).right - centerX) * 1.5);
            int top = (int)((detectResult.getRects().get(i).top - centerY) * 1.5);
            int bottom = (int)((detectResult.getRects().get(i).bottom - centerY) * 1.5);
            // 将新的边的坐标平移到原来的坐标系
            left = left + centerX;
            right = right + centerX;
            top = top + centerY;
            bottom = bottom + centerY;

            int height=(bottom> ParameterConfig.getFromSP().getDpiHeight()? ParameterConfig.getFromSP().getDpiHeight():bottom)-(top<0? 0:top);
            int width=(right> ParameterConfig.getFromSP().getDpiWidth()? ParameterConfig.getFromSP().getDpiWidth():right)-(left<0? 0:left);
//            Bitmap bitmap = Bitmap.createBitmap(BitmapFactory.decodeFile(originalPhoto), (detectResult.getRects().get(i).left/1.2)<0? 0: (int) (detectResult.getRects().get(i).left /1.2), (detectResult.getRects().get(i).top/1.2<0)? 0: (int) (detectResult.getRects().get(i).top/1.2),width>(ParameterConfig.getFromSP().getDpiWidth()-detectResult.getRects().get(i).left/1.2)?(ParameterConfig.getFromSP().getDpiWidth()-detectResult.getRects().get(i).left/1.2):width,height>(ParameterConfig.getFromSP().getDpiHeight()-detectResult.getRects().get(i).top/1.2)?(ParameterConfig.getFromSP().getDpiHeight()-detectResult.getRects().get(i).top/1.2):height);
            Bitmap bitmap = Bitmap.createBitmap(BitmapFactory.decodeFile(originalPhoto), left<0? 0: left, top<0? 0: top,width,height);
            try {
                File file=new File(cutPathName);
                fos=new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                fos.flush();
                fos.close();
                getVerify(detectResult,calendar,cutPathName,originalPhoto);
                SqliteUtil.insertFaceCollectionItem(cutPathName, originalPhoto, calendar.getTime(),cameraPrenster);
                cutPathName = null;
                originalPhoto = null;
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void Event(ThreadCanshu threadCanshu){
        int picQuality=0;
        String tempBestPicPath = null;
        ArrayList<DetectResult> tempDetectResults = new ArrayList<>();
        Calendar tempBestCalender = null;
        List<String> deletePath=new ArrayList<>();
        List<Calendar> threadCalenders = threadCanshu.getThreadCalenders();
        List<String> picPaths = threadCanshu.getPicPaths();
        if (threadCalenders != null) {
            for (int i = 0; i < picPaths.size(); i++) {
                try {
                    Bitmap bitmap = Bitmap.createBitmap(BitmapFactory.decodeFile(picPaths.get(i)));
                    FileOutputStream tempFos = new FileOutputStream(tempPath + TextUtil.dateMessage(threadCalenders.get(i).getTime())+"_"+i+".jpg");
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, tempFos);
                    tempFos.flush();
                    tempFos.close();
//                    FileUtil.copyCompressedBitmap(picPaths.get(i),tempPath + TextUtil.dateMessage(threadCalenders.get(i).getTime())+"_"+i+".jpg");
                    ArrayList<String> inputPicPaths = new ArrayList<>();
                    inputPicPaths.add(tempPath + TextUtil.dateMessage(threadCalenders.get(i).getTime()) + "_" + i + ".jpg");
                    ArrayList<DetectResult> detectResults = SDKUtil.detectFace(inputPicPaths);
                    if (detectResults!=null) {
                        if (((detectResults.get(0).getPoints().get(0).x)[1] - (detectResults.get(0).getPoints().get(0).x)[0]) > picQuality) {
                            if(i!=0) {
                                deletePath.add(picPaths.get(i - 1));
                            }
                            picQuality = (int) ((detectResults.get(0).getPoints().get(0).x)[1] - (detectResults.get(0).getPoints().get(0).x)[0]);
                            tempDetectResults.clear();
                            tempDetectResults = detectResults;
                            tempBestCalender = threadCalenders.get(i);
                            tempBestPicPath = picPaths.get(i);
                        }else {
                            deletePath.add(picPaths.get(i));
                        }
                    }
                    if (i == (picPaths.size()-1)&&!tempDetectResults.isEmpty()) {
                        getCutPicture(tempBestPicPath, tempDetectResults.get(0), tempBestCalender, tempDetectResults.get(0).points.size());
                        for(int delete=0;delete<deletePath.size();delete++){
                            FileUtil.delete(deletePath.get(delete));
                        }
                        deletePath.clear();
                        deletePath = null;
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            picPaths.clear();
            picPaths = null;
            tempBestPicPath = null;
        }
    }

    public class ThreadCanshu{
        public List<Calendar> threadCalenders;
        public List<String> picPaths ;

        public ThreadCanshu(List<Calendar> threadCalenders, List<String> picPaths) {
            this.threadCalenders = threadCalenders;
            this.picPaths = picPaths;
        }

        public List<Calendar> getThreadCalenders() {
            return threadCalenders;
        }

        public void setThreadCalenders(List<Calendar> threadCalenders) {
            this.threadCalenders = threadCalenders;
        }

        public List<String> getPicPaths() {
            return picPaths;
        }

        public void setPicPaths(List<String> picPaths) {
            this.picPaths = picPaths;
        }
    }
}

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
import com.iustu.identification.util.DataCache;
import com.iustu.identification.util.SDKUtil;
import com.iustu.identification.util.SqliteUtil;
import com.iustu.identification.util.TextUtil;
import com.jiangdg.usbcamera.UVCCameraHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static java.lang.Math.sqrt;

public class CapturePicService extends Service {
    UVCCameraHelper cameraHelper=UVCCameraHelper.getInstance();
    static String rootPath= Environment.getExternalStorageDirectory()+"/DeepFace";
    static String cutPath=Environment.getExternalStorageDirectory()+"/DeepFace/Cut/";
    static String tempPath=Environment.getExternalStorageDirectory()+"/DeepFace/temp/";
    private CameraPrenster cameraPrenster;
    int captureNum=0;
    int picQuality=0;
    CaptureBind mBind;
    Disposable disposable;
    ArrayList<SearchHandler> searchHandlers=new ArrayList<>();
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
    File cutFile=new File(cutPath);
    FileOutputStream fos;
    ThreadCanshu threadCanshu;
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
        EventBus.getDefault().register(this);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        capturePic();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @SuppressLint("CheckResult")
    public void capturePic() {
        createDir(rootPath);
        createDir(tempPath);
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
                        captureNum++;
                        Calendar calendar=Calendar.getInstance();
                        String fileName=TextUtil.dateMessage(calendar.getTime())+"_"+captureNum+".jpg";
                        String picPath=rootPath+"/"+fileName;
                        cameraHelper.capturePicture(picPath, picPath1 -> {
                            calendars.add(calendar);
                            capturePicPaths.add(picPath1);
                        });
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
            Log.d("Camera","人脸数量"+detectResults.get(0).size());
            getCutPicture(picPath,detectResults.get(0),calendar,detectResults.get(0).points.size());
        }
    }
    public void createDir(String dirPath) {
        File file=new File(dirPath);
        if(!file.exists()){
            Log.d("Camera", String.valueOf(file.mkdirs()));
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
            Log.d("CameraOriginalPhoto",originalPhoto);
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

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void Event(ThreadCanshu threadCanshu){
        Log.d("EventBus2", String.valueOf(threadCanshu));
        String tempBestPicPath = null;
        ArrayList<DetectResult> tempDetectResults = new ArrayList<>();
        Calendar tempBestCalender = null;
        List<Calendar> threadCalenders = threadCanshu.getThreadCalenders();
        List<String> picPaths = threadCanshu.getPicPaths();
        if (threadCalenders != null) {
            for (int i = 0; i < picPaths.size(); i++) {
                try {
                    Bitmap bitmap = Bitmap.createBitmap(BitmapFactory.decodeFile(picPaths.get(i)));
                    FileOutputStream tempFos = new FileOutputStream(tempPath + TextUtil.dateMessage(threadCalenders.get(i).getTime())+"_"+i+".jpg");
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, tempFos);
                    ArrayList<String> inputPicPaths = new ArrayList<>();
                    inputPicPaths.add(tempPath + TextUtil.dateMessage(threadCalenders.get(i).getTime()) + "_" + i + ".jpg");
                    tempFos.flush();
                    tempFos.close();
                    ArrayList<DetectResult> detectResults = SDKUtil.detectFace(inputPicPaths);
                    if (!detectResults.isEmpty()) {
                        if (((detectResults.get(0).getPoints().get(0).x)[1] - (detectResults.get(0).getPoints().get(0).x)[0]) > picQuality) {
                            picQuality = (int) ((detectResults.get(0).getPoints().get(0).x)[1] - (detectResults.get(0).getPoints().get(0).x)[0]);
                            tempDetectResults.clear();
                            tempDetectResults = detectResults;
                            tempBestCalender = threadCalenders.get(i);
                            tempBestPicPath = picPaths.get(i);
                        }
                    }
                    Log.d("CameraCapture", String.valueOf(i));
                    if (i == 4) {
                        getCutPicture(tempBestPicPath, tempDetectResults.get(0), tempBestCalender, tempDetectResults.get(0).points.size());
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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

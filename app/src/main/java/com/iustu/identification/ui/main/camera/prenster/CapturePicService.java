package com.iustu.identification.ui.main.camera.prenster;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.util.Log;

import com.example.agin.facerecsdk.DetectResult;
import com.example.agin.facerecsdk.FeatureResult;
import com.example.agin.facerecsdk.HandlerFactory;
import com.example.agin.facerecsdk.SearchHandler;
import com.example.agin.facerecsdk.SearchResultItem;
import com.iustu.identification.api.Api;
import com.iustu.identification.bean.ParameterConfig;
import com.iustu.identification.util.AlarmUtil;
import com.iustu.identification.util.Base64Util;
import com.iustu.identification.util.DataCache;
import com.iustu.identification.util.FileUtil;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
//import leakcanary.LeakSentry;

import static java.lang.Math.sqrt;

public class CapturePicService extends Service {
    UVCCameraHelper cameraHelper=UVCCameraHelper.getInstance();
    static String rootPath= Environment.getExternalStorageDirectory()+"/DeepFace";
    static String cutPath=Environment.getExternalStorageDirectory()+"/DeepFace/Cut/";
    static String tempPath=Environment.getExternalStorageDirectory()+"/DeepFace/temp/";
    private CameraPrenster cameraPrenster;
    volatile int captureNum=0;
    volatile int captureCount = 0;       // 计数器
    int picQuality=0;
    Calendar tempBestCalender;
    CaptureBind mBind;
    Disposable disposable;
    volatile HashMap<String, SearchHandler> searchHandlers=new HashMap<>();
    volatile HashSet<String> imageIdCache = new HashSet<>();      // 保存ImageID
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
        HashSet<String> libPat= DataCache.getChosenLibConfig(); //从配置文件中调取选取的人脸库
        for(String libPath:libPat){
            if (searchHandlers.get(libPath) == null) {
                SearchHandler searchHandler= (SearchHandler) HandlerFactory.createSearcher(rootPath+"/"+libPath,0,1); //初始化人脸搜索句柄
                searchHandlers.put(libPath, searchHandler);
            }
            libNames.add(libPath);
        }
        EventBus.getDefault().register(this);
        capturePic();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /**
     * 使用轮询实现1秒4张照片，并通过EventBus将四张照片完成后，一起交付到线程池进行后续检测
     */
    @SuppressLint("CheckResult")
    public void capturePic() {
        Observable.interval(250, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable=d;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (captureCount == 4){
                            imageIdCache.clear();
                            captureCount = 0;
                        }
                        captureCount ++;

                        Calendar calendar=Calendar.getInstance();
                        String fileName=TextUtil.dateMessage(calendar.getTime())+"_"+captureNum+".jpg";
                        String picPath=rootPath+"/"+fileName;
                        cameraHelper.capturePicture(picPath, picPath1 -> {
                            calendars.add(calendar);
                            capturePicPaths.add(picPath1);
                        });
                        try {
                            if (DataCache.getParameterConfig().getNeedNoSame()) {
                                captureNum++;
                                if (captureNum == 4) {
                                    CopyOnWriteArrayList<String> tempCapturePicPath = new CopyOnWriteArrayList<>();
                                    tempCapturePicPath.addAll(capturePicPaths);
                                    List<Calendar> tempCalender = new ArrayList<>();
                                    tempCalender.addAll(calendars);
                                    threadCanshu = new ThreadCanshu(tempCalender, tempCapturePicPath);
                                    EventBus.getDefault().post(threadCanshu);
                                    capturePicPaths.clear();
                                    calendars.clear();
                                    captureNum = 0;
                                }
                            } else {
                                CopyOnWriteArrayList<String> tempCapturePicPath = new CopyOnWriteArrayList<>();
                                for(String s : capturePicPaths) {
                                    tempCapturePicPath.add(s);
                                }
                                //tempCapturePicPath.addAll(capturePicPaths);
                                List<Calendar> tempCalender = new ArrayList<>();
                                tempCalender.addAll(calendars);
                                threadCanshu = new ThreadCanshu(tempCalender, tempCapturePicPath);
                                EventBus.getDefault().post(threadCanshu);
                                capturePicPaths.clear();
                                calendars.clear();
                                captureNum = 0;
                            }
                        }catch (OutOfMemoryError e){
                            e.printStackTrace();
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
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
//        LeakSentry.INSTANCE.getRefWatcher().watch(this);
        FileUtil.deleteCache();
        FileUtil.deleteTemp();
        disposable.dispose();
    }
    public void getVerify(int index, DetectResult detectResult,Calendar calendar,String photoPath,String originalPhoto){
        ArrayList<DetectResult> temp=new ArrayList<>();
        temp.add(detectResult);
        FeatureResult featureResult=SDKUtil.featureResult(temp);
        searchFace(featureResult.getFeat(0).get(index), calendar, photoPath, originalPhoto, imageIdCache);
    }
    public void searchFace(float[] feat,Calendar calendar,String photoPath,String originalPhoto, HashSet<String> idCache){
        for(int i = 0; i < libNames.size(); i ++){
            SearchResultItem searchResultItem = null;
            ArrayList<SearchResultItem> searchResultItems=new ArrayList<>();
            searchHandlers.get(libNames.get(i)).searchFind(feat,1,searchResultItems, DataCache.getParameterConfig().getThresholdQuanity());
            if(!searchResultItems.isEmpty()) {
                for (SearchResultItem temp : searchResultItems) {
                    if (idCache.contains(temp.image_id)) {
                        FileUtil.deleteWithCache(photoPath);
                        FileUtil.deleteWithCache(originalPhoto);
                        return;
                    }
                    idCache.add(temp.image_id);
                    imageIdCache.add(temp.image_id);
                    if (searchResultItem == null) {
                        searchResultItem = temp;
                    } else if (temp.score > searchResultItem.score) {
                        searchResultItem = temp;
                    }
                }
                double score= (sqrt(searchResultItem.score - DataCache.getParameterConfig().getThresholdQuanity()) /sqrt(1.0 - DataCache.getParameterConfig().getThresholdQuanity())* 0.15 + 0.85);
                searchResultItem.score = round(score, 2, BigDecimal.ROUND_HALF_UP);
                if(searchResultItem.score > DataCache.getParameterConfig().getFactor()) {
                    AlarmUtil.alarm();
                    SqliteUtil.insertComparedItem(libNames.get(i), searchResultItem,calendar.getTime(),photoPath, cameraPrenster, originalPhoto);
                }

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
            Bitmap bitmap = Bitmap.createBitmap(BitmapFactory.decodeFile(originalPhoto, FileUtil.getCompressOptions(originalPhoto)), left<0? 0: left, top<0? 0: top,width,height);
            try {
                File file=new File(cutPathName);
                fos=new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                fos.flush();
                fos.close();
                if(DataCache.getParameterConfig().getFactor()!=0) {
                    getVerify(i, detectResult, calendar, cutPathName, originalPhoto);
                }
                SqliteUtil.insertFaceCollectionItem(cutPathName, originalPhoto, calendar.getTime(),cameraPrenster);
                cutPathName = null;
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void Event(ThreadCanshu threadCanshu){
        int picQuality = DataCache.getParameterConfig().getMinEyesDistance();
        String tempBestPicPath = null;
        ArrayList<DetectResult> tempDetectResults = new ArrayList<>();
        Calendar tempBestCalender = null;
        CopyOnWriteArrayList<String> deletePath=new CopyOnWriteArrayList<>();
        List<Calendar> threadCalenders = threadCanshu.getThreadCalenders();
        List<String> picPaths = threadCanshu.getPicPaths();
        if (threadCalenders != null) {
            int index = -1;      // 记录当前最优图片所在的位置
            for (int i = 0; i < picPaths.size(); i++) {
                try {
                    Bitmap bitmap = Bitmap.createBitmap(BitmapFactory.decodeFile(picPaths.get(i), FileUtil.getCompressOptions(picPaths.get(i))));
                    FileOutputStream tempFos = new FileOutputStream(tempPath + TextUtil.dateMessage(threadCalenders.get(i).getTime())+"_"+i+".jpg");
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, tempFos);
                    tempFos.flush();
                    tempFos.close();
                    ArrayList<String> inputPicPaths = new ArrayList<>();
                    inputPicPaths.add(tempPath + TextUtil.dateMessage(threadCalenders.get(i).getTime()) + "_" + i + ".jpg");
                    ArrayList<DetectResult> detectResults = SDKUtil.detectFace(inputPicPaths);
                    if (detectResults!=null) {
                        if (((detectResults.get(0).getPoints().get(0).x)[1] - (detectResults.get(0).getPoints().get(0).x)[0]) > picQuality && ((detectResults.get(0).getPoints().get(0).x)[1] - (detectResults.get(0).getPoints().get(0).x)[0]) >= DataCache.getParameterConfig().getMinEyesDistance()) {
                            if (index > -1)
                                deletePath.add(picPaths.get(index));
                            index = i;
                            picQuality = (int) ((detectResults.get(0).getPoints().get(0).x)[1] - (detectResults.get(0).getPoints().get(0).x)[0]);  //通过瞳距得到相片质量
                            tempDetectResults.clear();
                            tempDetectResults = detectResults;
                            tempBestCalender = threadCalenders.get(i);
                            tempBestPicPath = picPaths.get(i);
                        }else {
                            deletePath.add(picPaths.get(i)); //质量较差的照片
                        }
                    }
                    if (i == (picPaths.size()-1)&&!tempDetectResults.isEmpty()) {
                        getCutPicture(tempBestPicPath, tempDetectResults.get(0), tempBestCalender, tempDetectResults.get(0).points.size()); //根据算法返回结果裁剪人脸并保存
                        FileUtil.deleteList(deletePath); //删除质量较差的照片
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
    /**
     * 上传图片方法,传入参数为图片路径
     */
    public void uploadImage(Calendar calendar,String picPath){
        String snapTime=TextUtil.getDateString(TextUtil.FORMAT_MILLISECOND,calendar.getTime());
        String picBase64= Base64Util.convertBase64(picPath);
        Api.uploadImageCallBackObservable(snapTime,picBase64);
    }
    /**
     * EventBus使用的内部类
     */
    public class ThreadCanshu{
        public List<Calendar> threadCalenders;
        public CopyOnWriteArrayList<String> picPaths ;

        public ThreadCanshu(List<Calendar> threadCalenders, CopyOnWriteArrayList<String> picPaths) {
            this.threadCalenders = threadCalenders;
            this.picPaths = picPaths;
        }

        public List<Calendar> getThreadCalenders() {
            return threadCalenders;
        }

        public void setThreadCalenders(List<Calendar> threadCalenders) {
            this.threadCalenders = threadCalenders;
        }

        public CopyOnWriteArrayList<String> getPicPaths() {
            return picPaths;
        }

    }

    // 第三位小数四舍五入
    public static float round(double value, int scale, int roundingMode) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(scale, roundingMode);
        float d = bd.floatValue();
        bd = null;
        return d;
    }
}

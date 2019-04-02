package com.iustu.identification.ui.widget.camera;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.iustu.identification.BuildConfig;
import com.iustu.identification.R;
import com.iustu.identification.util.ExceptionUtil;
import com.iustu.identification.util.ImageUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Liu Yuchuan on 2017/11/16.
 */

@SuppressWarnings("SuspiciousNameCombination")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback , Camera.PreviewCallback{
    private static final String TAG = "CameraPreview";
    private SurfaceHolder mHolder;
    private Camera mCamera;
    int id;
    private float screenWidth;
    private float screenHeight;
    private float bottomBarHeight;
    private float titleHeight;

    private byte[] bytes;

    public Observable<byte[]> imgDataObservable(){
        return Observable.interval(500, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .map(aLong -> {
                    if(bytes == null){
                        return new byte[0];
                    }else {
                        long startTime = System.currentTimeMillis();
                        Camera.Parameters parameters = mCamera.getParameters();
                        Camera.Size size = parameters.getPreviewSize();
                        YuvImage image = new YuvImage(bytes, parameters.getPreviewFormat(),
                                size.width, size.height, null);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        image.compressToJpeg(new Rect(0, 0, size.width, size.height), 100, baos);
                        byte[] newData = baos.toByteArray();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(newData, 0, newData.length);
                        bitmap = ImageUtils.rotateBitmap(bitmap, 270);
                        baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 76, baos);
                        bitmap.recycle();
                        System.out.println("endTime " + (System.currentTimeMillis() - startTime));
                        return baos.toByteArray();
                    }
                });
    }

    public CameraPreview(Context context, int id) {
        super(context);
        this.id = id;
        mHolder = getHolder();
        mHolder.addCallback(this);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        bottomBarHeight = getResources().getDimension(R.dimen.y147);
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            titleHeight = getResources().getDimensionPixelSize(resourceId);
        }
    }

    public void getCameraInstance(int id){
        if (mCamera == null) {
            CameraHandlerThread mThread = new CameraHandlerThread("camera thread");
            synchronized (mThread) {
                mThread.openCamera(id);
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        getCameraInstance(id);
        startPreView();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        Log.e(TAG, "onSurfaceChange");
//        adjustDisplayRatio();
        findBestPreviewSize(mCamera.getParameters(), width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        Log.e(TAG, "surfaceDestroyed");
        mHolder.removeCallback(this);
        startPreViewCallback = null;
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    private void openCameraOriginal(int id) {
        try {
            mCamera = Camera.open(id);
        } catch (Exception e) {
            Log.d(TAG, "camera is not available");
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        bytes = data;
    }

    private class CameraHandlerThread extends HandlerThread {
        Handler mHandler;

        public CameraHandlerThread(String name) {
            super(name);
            start();
            mHandler = new Handler(getLooper());
        }

        synchronized void notifyCameraOpened() {
            notify();
        }

        void openCamera(int id) {
            mHandler.post(() -> {
                openCameraOriginal(id);
                notifyCameraOpened();
            });
            try {
                wait();
            } catch (InterruptedException e) {
                Log.w(TAG, "wait was interrupted");
            }
        }
    }
//
//    private void adjustDisplayRatio() {
//        ViewGroup parent = ((ViewGroup) getParent());
//        Rect rect = new Rect();
//        parent.getLocalVisibleRect(rect);
//        double width = rect.width();
//        double height = rect.height();
//        Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
//        double previewWidth = previewSize.height;
//        double previewHeight= previewSize.width;
//
//        double targetW, targetH, ratio;
//        if (width * previewHeight > height * previewWidth) {
//            ratio = width / previewWidth;
//            targetH = ratio * previewHeight;
//            double out =  (targetH - height) / 2;
//            layout(0, -(int) out, (int)width, (int)(height + out));
//        } else if(width * previewHeight < height * previewWidth){
//            ratio = height / previewHeight;
//            targetW = width * ratio;
//            double out = (targetW - width) / 2;
//            layout(-(int) out, 0, (int)(width + out), (int)height);
//        }
//    }

    public void takePicture(Camera.PictureCallback jpeg){
        if(mCamera != null){
            try {
                mCamera.takePicture(null, null, jpeg);
            }catch (RuntimeException e){
                ExceptionUtil.getThrowableMessage(e);
                e.printStackTrace();
//                mCamera.startPreview();
            }
        }
    }


    public Disposable startPreView(){
        if(mCamera == null){
            return null;
        }
        return Observable.create((ObservableOnSubscribe<Camera>) e -> {
            mCamera.setPreviewCallback(CameraPreview.this);
            mCamera.setDisplayOrientation(90);
//            adjustDisplayRatio();
            mCamera.setPreviewDisplay(mHolder);
            Camera.Parameters parameters = mCamera.getParameters();
            if(parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)){
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                mCamera.cancelAutoFocus();
            }
//            findBestPreviewSize(parameters);
            findBestPictureSize(parameters);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
            e.onNext(mCamera);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(camera -> {
                    if(startPreViewCallback != null){
                        startPreViewCallback.onStartPreview();
                    }
                }, Throwable::printStackTrace);
    }

    private void findBestPictureSize(Camera.Parameters parameters) {
        List<Camera.Size> sizeList = parameters.getSupportedPictureSizes();
        Collections.sort(sizeList, (o1, o2) -> {
            float screenSize = screenHeight * screenWidth;
            int s1 = o1.height * o1.width;
            int s2 = o2.height * o2.width;
            float d1 = Math.abs(s1 - screenSize);
            float d2 = Math.abs(s2 - screenSize);
            if(d1 < d2){
                return -1;
            }

            if(d1 > d2){
                return 1;
            }

            return s2 - s1;
        });
        parameters.setPictureSize(sizeList.get(0).width, sizeList.get(0).height);
    }

    private void findBestPreviewSize(Camera.Parameters parameters, float goalWidth, float goalHeight) {
        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
        List<Camera.Size> toFindList = new ArrayList<>();
        for (Camera.Size size : sizeList) {
            if(size.height >= goalWidth){
                float scale = goalWidth / size.height;
                float newHeight = size.width * scale;
                if(newHeight >= goalHeight){
                    toFindList.add(size);
                }
            }
        }

        if(toFindList.size() <= 0){

            if(BuildConfig.DEBUG) {
                StringBuilder builder = new StringBuilder();
                builder.append("无法找到合适的相机分辨率，支持分辨率如下：");
                for (Camera.Size size : sizeList) {
                    builder.append(size.width);
                    builder.append(", ");
                    builder.append(size.height);
                    builder.append("\n");
                }

                new AlertDialog.Builder(getContext())
                        .setMessage(builder.toString())
                        .show();
            }

            for (Camera.Size size : sizeList) {
                float scale = goalWidth / size.height;
                float newHeight = size.width * scale;
                if(newHeight >= goalHeight){
                    toFindList.add(size);
                }
            }

            if(toFindList.size() <= 0){
                throw new UnsupportedOperationException("找不到适合程序运行的相机分辨率");
            }

            Collections.sort(toFindList, (o1, o2) -> o2.height - o1.height);
        }else {
            Collections.sort(toFindList, (o1, o2) -> o1.height - o2.height);
        }


        parameters.setPreviewSize(toFindList.get(0).width, toFindList.get(0).height);
        FrameLayout frameLayout = (FrameLayout) getParent();
        if(frameLayout != null){
            frameLayout.post(()->{
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) frameLayout.getLayoutParams();
                Camera.Size size = toFindList.get(0);
                params.height = (int) (goalWidth / size.height * size.width);
                frameLayout.setLayoutParams(params);
            });
        }
    }

    private StartPreViewCallback startPreViewCallback;

    public interface StartPreViewCallback {
        void onStartPreview();
    }

    public void setStartPreViewCallback(StartPreViewCallback startPreViewCallback) {
        this.startPreViewCallback = startPreViewCallback;
    }
}

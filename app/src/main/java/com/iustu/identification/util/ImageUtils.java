package com.iustu.identification.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 Created by Liu Yuchuan on 2017/10/25.
 图片工具类
 */
public class ImageUtils {

    public static final int REQUEST_GALLERY = 1;

    public static void startChoose(Activity activity){
        Intent toGallery = new Intent(Intent.ACTION_GET_CONTENT);
        toGallery.setType("image/*");
        toGallery.addCategory(Intent.CATEGORY_OPENABLE);
        activity.startActivityForResult(toGallery, REQUEST_GALLERY);
    }

    public static void startChoose(Fragment fragment){
        Intent toGallery = new Intent(Intent.ACTION_GET_CONTENT);
        toGallery.setType("image/*");
        toGallery.addCategory(Intent.CATEGORY_OPENABLE);
        fragment.startActivityForResult(toGallery, REQUEST_GALLERY);
    }

    /**
     * 根据Uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    public static String getRealPathFromUri(Context context, Uri uri) {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= 19) { // api >= 19
            return getRealPathFromUriAboveApi19(context, uri);
        } else { // api < 19
            return getRealPathFromUriBelowAPI19(context, uri);
        }
    }

    /**
     * 适配api19以下(不包括api19),根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    private static String getRealPathFromUriBelowAPI19(Context context, Uri uri) {
        return getDataColumn(context, uri, null, null);
    }

    /**
     * 适配api19及以上,根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    @TargetApi(19)
    private static String getRealPathFromUriAboveApi19(Context context, Uri uri) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的 uri, 则通过document id来进行处理
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isMediaDocument(uri)) { // MediaProvider
                // 使用':'分割
                String id = documentId.split(":")[1];

                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = {id};
                filePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                filePath = getDataColumn(context, contentUri, null, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())){
            // 如果是 content 类型的 Uri
            filePath = getDataColumn(context, uri, null, null);
        } else if ("file".equals(uri.getScheme())) {
            // 如果是 file 类型的 Uri,直接获取图片对应的路径
            filePath = uri.getPath();
        }
        return filePath;
    }

    /**
     * 获取数据库表中的 _data 列，即返回Uri对应的文件路径
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;

        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

    //旋转角度
    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        if (degrees == 0 || null == bitmap) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return bmp;
    }

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Observable<File> savePhoto(String folder, byte [] data, int rotation, FileCallBack callBack){
        if(callBack != null){
            callBack.onStartSaveFile();
        }
        File file = new File(Environment.getExternalStorageDirectory(),  "Identification" + File.separator + folder);
        if(!file.exists() && !file.mkdirs()){
            if(callBack != null){
                callBack.onCreateFileFailed("创建文件夹 " + file.getAbsolutePath() + " 失败！");
            }
            return null;
        }
        File imgFile = new File(file, String.valueOf(System.currentTimeMillis()));
        return Observable.create((ObservableOnSubscribe<File>) e -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            if(rotation != 0){
                bitmap = rotateBitmap(bitmap, rotation);
            }

            FileOutputStream fis = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 76, fis);
            fis.flush();
            fis.close();
            if(callBack != null){
                callBack.onSaveFileSuccess();
            }
            e.onNext(imgFile);
            Log.e("Save rotation file " + rotation, "getRotation " + readPictureDegree(imgFile.getAbsolutePath()));
            e.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<File> modifiedSavePhoto(String folder, String oldPath, int rotation, FileCallBack callBack){
        if(callBack != null){
            callBack.onStartSaveFile();
        }
        File file = new File(Environment.getExternalStorageDirectory(),  "Identification" + File.separator + folder);
        if(!file.exists() && !file.mkdirs()){
            if(callBack != null){
                callBack.onCreateFileFailed("创建文件夹 " + file.getAbsolutePath() + " 失败！");
            }
            return null;
        }
        File imgFile = new File(file, String.valueOf(System.currentTimeMillis()));
        return Observable.create((ObservableOnSubscribe<File>) e -> {
            Bitmap bitmap = BitmapFactory.decodeFile(oldPath);

            if(rotation != 0){
                bitmap = rotateBitmap(bitmap, rotation);
            }

            FileOutputStream fis = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fis);
            fis.flush();
            fis.close();
            if(callBack != null){
                callBack.onSaveFileSuccess();
            }
            e.onNext(imgFile);
            e.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Bitmap cropBitmap(Bitmap bitmap,String rect, int width, int height){
        if(bitmap == null) return null;

        String [] boundStr = rect.split(",");
        if(boundStr.length != 4)
            return null;
        int bounds[] = new int[4];
        float w = bitmap.getWidth();
        float h = bitmap.getHeight();
        for(int i = 0; i < 4; i++){
            bounds[i] = Integer.parseInt(boundStr[i]);
            if(i % 2 == 0){
                bounds[i] *= w / width;
            }else {
                bounds[i] *= h / height;
            }
        }

        float nW = bounds[2] - bounds[0];
        float nH = bounds[3] - bounds[1];
        if(bounds[0] + nW > w){
            nW =  w - bounds[0];
        }
        if(nW < 0){
            nW =  w;
            bounds[0] = 0;
        }
        if(bounds[1] + nH > h){
            nH = h - bounds[1];
        }
        if(nH < 0){
            nH =  h;
            bounds[1] = 0;
        }
//
//        float scale;
//        if(outH / outW >= nH / nW){
//            scale = outW / nW;
//        }else {
//            scale = outH / nH;
//        }
//
//        if(Float.compare(scale, 1) != 0){
//            Matrix matrix = new Matrix();
//            matrix.postScale(scale, scale);
//            return Bitmap.createBitmap(bitmap, bounds[0], bounds[1],(int) nW, (int) nH, matrix, true);
//        }

        return Bitmap.createBitmap(bitmap, bounds[0], bounds[1],(int) nW, (int) nH);
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is MediaProvider
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is DownloadsProvider
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static class CropFace extends BitmapTransformation {
        private static final String ID = "com.bumptech.glide.transformations.CropFace";
        private int width, height;
        private String rect;

        public CropFace(int width, int height, String rect){
            this.width = width;
            this.height = height;
            this.rect = rect;
        }

        @Override
        public Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
            return cropBitmap(toTransform, rect, width, height);
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof CropFace && width == ((CropFace) o).width && height == ((CropFace) o).height && rect.equals(((CropFace) o).rect);
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = result * 31 + ID.hashCode();
            result = result * 31 + width;
            result = result * 31 + height;
            result = result * 31 + rect.hashCode();
            return result;
        }

        @Override
        public void updateDiskCacheKey(MessageDigest messageDigest){
            try {
                messageDigest.update(ID.getBytes(STRING_CHARSET_NAME));
                messageDigest.update(rect.getBytes(STRING_CHARSET_NAME));
                messageDigest.update(ByteBuffer.allocate(4).putInt(width).array());
                messageDigest.update(ByteBuffer.allocate(4).putInt(height).array());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
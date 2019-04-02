package com.iustu.identification.util;

import android.util.Log;

import com.iustu.identification.api.Api;
import com.iustu.identification.api.message.Message;
import com.iustu.identification.bean.FaceSet;
import com.iustu.identification.bean.Library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Liu Yuchuan on 2017/12/9.
 */

public class LibManager{
    private final List<Library> libraryList;
    private Map<String, String> idNameMap;
    private boolean isLoadData;

    private static LibManager instance;

    private CompositeDisposable compositeDisposable;

    private void addDisposable(Disposable disposable){
        if(compositeDisposable == null){
            compositeDisposable = new CompositeDisposable();
        }

        if(disposable != null){
            compositeDisposable.add(disposable);
        }
    }

    private LibManager(){
        isLoadData = false;
        libraryList = new ArrayList<>();
        idNameMap = new HashMap<>();
    }

    public static Map<String, String> getIdNameMap(){
        return getInstance().idNameMap;
    }

    public static List<Library> getLibraryList() {
        return getInstance().libraryList;
    }

    public static String getLibName(String id){
        return getInstance().idNameMap.get(id);
    }

    public static void loadData(){
        LibManager libManager = getInstance();
        Api.getFaceSetList()
                .doOnSubscribe(d->{
                    libManager.addDisposable(d);
                    if(libManager.onLibLoadListener != null){
                        libManager.onLibLoadListener.onStartLoad();
                    }
                    libManager.libraryList.clear();
                    libManager.idNameMap.clear();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listMessage -> {
                    if(listMessage.getCode() == Message.CODE_SUCCESS){
                        libManager.isLoadData = true;
                        if(libManager.onLibLoadListener != null){
                            for (int i = 0; i < listMessage.getBody().size(); i++) {
                                FaceSet faceSet = listMessage.getBody().get(i);
                                libManager.libraryList.add(new Library(faceSet, i));
                                libManager.idNameMap.put(faceSet.getId(), faceSet.getName());
                            }
                            libManager.onLibLoadListener.onSuccessLoad();
                        }
                    }else {
                        libManager.onLibLoadListener.onFailLoad();
                    }
                }, t->{
                    ExceptionUtil.getThrowableMessage(libManager.getClass().getSimpleName(), t);
                    libManager.onLibLoadListener.onFailLoad();
                });
    }

    public static boolean isLoadData() {
        return getInstance().isLoadData;
    }

    public static void dispose(){
        LibManager libManager = getInstance();
        if(libManager.compositeDisposable != null){
            libManager.compositeDisposable.clear();
        }
    }

    private static LibManager getInstance() {
        if(instance == null) {
            synchronized (LibManager.class) {
                instance = new LibManager();
            }
        }
        return instance;
    }

    private OnLibLoadListener onLibLoadListener;

    public interface OnLibLoadListener{
        void onStartLoad();
        void onSuccessLoad();
        void onFailLoad();
    }

    public static void setOnLoadListener(OnLibLoadListener onLoadListener){
        getInstance().onLibLoadListener = onLoadListener;
    }
}
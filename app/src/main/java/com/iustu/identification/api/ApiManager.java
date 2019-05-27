package com.iustu.identification.api;

import com.iustu.identification.BuildConfig;
import com.iustu.identification.bean.ParameterConfig;
import com.iustu.identification.config.ParametersConfig;
import com.iustu.identification.config.SystemConfig;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Liu Yuchuan on 2017/11/25.
 * Api管理 因为涉及更新最好不要持有api的引用
 */

public class ApiManager {
    private Retrofit mRetrofit;
    private ApiInterface mApi;

    private static ApiManager mInstance;

    private ApiManager(){
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        if(BuildConfig.DEBUG){
            //显示日志
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }else {
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .addInterceptor(logInterceptor)
                .build();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://"+ParameterConfig.getFromSP().getIpAddress()+"/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        mRetrofit = builder.build();
    }

    public static ApiManager getInstance(){
        if(mInstance == null){
            synchronized (ApiManager.class){
                mInstance = new ApiManager();
            }
        }
        return mInstance;
    }

    public void updateBaseUrl(String newUrl){
        mRetrofit = mRetrofit.newBuilder()
                .baseUrl(newUrl)
                .build();
        mApi = mRetrofit.create(ApiInterface.class);
    }

    /**
     * 获取api实例
     * 不要持有其引用
     * @return api
     */
    ApiInterface getApi(){
        if(mApi == null){
            synchronized (ApiInterface.class){
                mApi = mRetrofit.create(ApiInterface.class);
            }
        }

        return mApi;
    }
}

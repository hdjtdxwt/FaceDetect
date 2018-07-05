package com.epsit.ihealth.robot.retrofit;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2018/7/5/005.
 */

public class ApiManager {
    private ApiService mApiService;
    private static ApiManager sApiManager;

    //获取ApiManager的单例
    public static ApiManager getInstance() {
        if (sApiManager == null) {
            synchronized (ApiManager.class) {
                if (sApiManager == null) {
                    sApiManager = new ApiManager();
                }
            }
        }
        return sApiManager;
    }

    /**
     * 封装配置知乎API
     */
    public ApiService getApiService() {
        //不需要使用拦截器就不创建直接从if开始
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new NetWorkInterceptor())
                //添加网络拦截器
                .build();
        if (mApiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AppConfig.baseUrl)
                    //将client与retrofit关联
                    .client(client)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            //到这一步创建完成
            mApiService = retrofit.create(ApiService.class);
        }
        return mApiService;
    }
}

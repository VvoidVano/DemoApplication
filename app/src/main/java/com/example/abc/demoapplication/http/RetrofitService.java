package com.example.abc.demoapplication.http;




import com.example.abc.demoapplication.utils.Constants;
import com.example.abc.demoapplication.utils.LogUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by yangyj on 2018/2/12
 */

public class RetrofitService {


    private static volatile Retrofit retrofit;

    private RetrofitService() {}

    public static Retrofit getRetrofit() {

        if (retrofit == null) {
            synchronized (RetrofitService.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .addConverterFactory(GsonConverterFactory.create())//解析方法 gson
                            .client(new OkHttpClient.Builder()
                                    .connectTimeout(Constants.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                                    .addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                                @Override
                                public void log(String message) {
                                    LogUtils.LogI("RetrofitLog", message);//
                                }
                            }).setLevel(HttpLoggingInterceptor.Level.BODY)).build())
                            .baseUrl(Constants.APPBASE_URL)//主机地址
                            .build();
                }
            }
        }
        return retrofit;
    }




}

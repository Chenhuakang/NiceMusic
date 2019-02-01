package com.lzx.nicemusic.network;

import com.bumptech.glide.Glide;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

    private static volatile RetrofitHelper sRetrofitHelper;
    private static OkHttpClient sOkHttpClient;

    public static RetrofitHelper get() {
        if (sRetrofitHelper == null) {
            synchronized (Glide.class) {
                if (sRetrofitHelper == null) {
                    InitializeRetrofit();
                }
            }
        }
        return sRetrofitHelper;
    }

    private static void InitializeRetrofit() {
        if (sOkHttpClient == null) {
            synchronized (RetrofitHelper.class) {
                if (sOkHttpClient == null) {
                    sOkHttpClient = new OkHttpClient.Builder()
                            .retryOnConnectionFailure(true)
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
        sRetrofitHelper = new RetrofitHelper();
    }

    private RetrofitHelper() {
    }

    public <T> T createApi(Class<T> clazz) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MediaApi.basApi)
                .client(sOkHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(clazz);
    }
}

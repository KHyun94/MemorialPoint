package com.example.memorialpoint.Networks;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitCon {

    public RetrofitService retrofitService;

    public RetrofitCon(String ip){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ip)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

       retrofitService = retrofit.create(RetrofitService.class);


    }
}

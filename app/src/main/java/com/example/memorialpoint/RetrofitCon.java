package com.example.memorialpoint;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitCon {

    public RetrofitService retrofitService;

    public RetrofitCon(String ip){

        OkHttpClient okHttpClient = new OkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ip)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


       retrofitService = retrofit.create(RetrofitService.class);
    }
}

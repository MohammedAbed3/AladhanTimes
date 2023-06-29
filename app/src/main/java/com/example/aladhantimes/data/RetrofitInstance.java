package com.example.aladhantimes.data;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static Retrofit retrofit = null;

    static Retrofit getRetrofitInstance() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.aladhan.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;

    }

    public API getApi() {
        return getRetrofitInstance().create(API.class);
    }
}

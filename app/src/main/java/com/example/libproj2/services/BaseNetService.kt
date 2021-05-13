package com.example.libproj2.services

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class BaseNetService {
    protected fun getRetrofit(): Retrofit {
        val httpClient = OkHttpClient.Builder()
                .callTimeout(20, TimeUnit.MINUTES)
                .connectTimeout(20, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.MINUTES)
                .writeTimeout(30, TimeUnit.MINUTES)

        val url = "https://www.googleapis.com/"
        val url1 = "http://192.168.31.21:44331/"
        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()

        return retrofit
    }
}
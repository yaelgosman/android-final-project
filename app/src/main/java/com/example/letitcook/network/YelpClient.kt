package com.example.letitcook.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object YelpClient {
    private const val BASE_URL = "https://api.yelp.com/v3/"

    val apiService: YelpApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YelpApiService::class.java)
    }
}
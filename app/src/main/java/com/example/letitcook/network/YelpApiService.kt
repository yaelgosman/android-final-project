package com.example.letitcook.network

import com.example.letitcook.models.YelpSearchResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface YelpApiService {

    @GET("businesses/search")
    suspend fun searchRestaurants(
        @Header("Authorization") authHeader: String,
        @Query("term") searchTerm: String, // e.g., "food", "pizza"
        @Query("location") location: String, // e.g., "Tel Aviv"
        @Query("limit") limit: Int = 20
    ): YelpSearchResponse
}
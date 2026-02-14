package com.example.letitcook.data

import com.example.letitcook.data.repository.AuthResponse
import com.example.letitcook.data.repository.LoginRequest
import com.example.letitcook.data.repository.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(@Body credentials: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body credentials: RegisterRequest)

    @POST("auth/logout")
    suspend fun logout()

    @POST("auth/refresh-token")
    suspend fun refreshToken(): AuthResponse
}
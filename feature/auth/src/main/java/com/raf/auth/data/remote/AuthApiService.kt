package com.raf.auth.data.remote

import com.raf.auth.data.remote.model.LoginRequest
import com.raf.auth.data.remote.model.RegisterRequest
import com.raf.auth.data.remote.response.LoginResponse
import com.raf.auth.data.remote.response.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("users")
    suspend fun register(
        @Body registerRequest: RegisterRequest,
    ): Response<RegisterResponse>

    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest,
    ): Response<LoginResponse>
}
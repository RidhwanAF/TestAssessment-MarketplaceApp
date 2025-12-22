package com.raf.marketplace.data.remote

import com.raf.marketplace.data.remote.response.ProductResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface MarketplaceApiService {
    @GET("products")
    suspend fun getProducts(
        @Header("Authorization") token: String,
    ): Response<List<ProductResponse>>

    @GET("products/{id}")
    suspend fun getProductById(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
    ): Response<ProductResponse>
}
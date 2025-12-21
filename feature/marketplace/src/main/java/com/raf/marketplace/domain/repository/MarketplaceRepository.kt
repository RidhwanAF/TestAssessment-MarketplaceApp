package com.raf.marketplace.domain.repository

import com.raf.core.domain.model.ApiResult
import com.raf.marketplace.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface MarketplaceRepository {
    suspend fun fetchProducts(token: String): ApiResult<List<Product>>
    fun getProducts(): Flow<List<Product>>
}
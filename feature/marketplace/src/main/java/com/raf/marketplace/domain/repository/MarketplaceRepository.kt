package com.raf.marketplace.domain.repository

import com.raf.core.domain.model.ApiResult
import com.raf.marketplace.domain.model.Cart
import com.raf.marketplace.domain.model.Product
import com.raf.marketplace.domain.model.ProductFilter
import kotlinx.coroutines.flow.Flow

interface MarketplaceRepository {
    suspend fun fetchProducts(token: String): ApiResult<List<Product>>
    suspend fun fetchProductById(token: String, productId: Int): ApiResult<Product>

    fun getProducts(productFilter: ProductFilter? = null): Flow<List<Product>>
    fun getProductCategories(): Flow<List<String>>
    fun getProductsByIds(productIds: List<Int>): Flow<List<Product>>

    suspend fun addToCart(cart: Cart): Result<Unit>
    fun getAllItemFromCart(): Flow<List<Cart>>
    fun getItemCountFromCart(): Flow<Int>
    suspend fun updateQuantityByProductId(productId: Int, newQuantity: Int)
    suspend fun deleteItemCartByProductId(productId: Int): Result<Unit>
    suspend fun deleteAllItemFromCart(): Result<Unit>
}
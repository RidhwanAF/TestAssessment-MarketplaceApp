package com.raf.marketplace.data.repository

import android.content.Context
import android.util.Log
import com.raf.core.data.utility.NetworkHelper.isNetworkAvailable
import com.raf.core.domain.model.ApiResult
import com.raf.marketplace.data.local.room.MarketplaceDatabase
import com.raf.marketplace.data.remote.MarketplaceApiService
import com.raf.marketplace.data.repository.mapper.ProductMapper.toDatabase
import com.raf.marketplace.data.repository.mapper.ProductMapper.toDomain
import com.raf.marketplace.domain.model.Product
import com.raf.marketplace.domain.repository.MarketplaceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MarketplaceRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val apiService: MarketplaceApiService,
    database: MarketplaceDatabase,
) : MarketplaceRepository {

    private val productDao = database.productDao

    override suspend fun fetchProducts(token: String): ApiResult<List<Product>> {
        if (!isNetworkAvailable(context)) return ApiResult.Error("")
        return try {
            val result = apiService.getProducts("Bearer $token")
            if (result.isSuccessful) {
                val products = result.body()?.map { productResponse ->
                    productResponse.toDatabase()
                } ?: emptyList()
                productDao.insertProducts(products)

                val productsDomain = products.map { productEntity ->
                    productEntity.toDomain()
                }

                ApiResult.Success(productsDomain)
            } else {
                val errorMessage = result.errorBody()?.string()?.takeIf { it.isNotEmpty() }
                    ?: result.message()
                ApiResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch products", e)
            ApiResult.Error(e.localizedMessage ?: "Unknown Error")
        }
    }

    override fun getProducts(): Flow<List<Product>> {
        return try {
            productDao.getProducts().map { productsEntity ->
                productsEntity.map { productEntity ->
                    productEntity.toDomain()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get products", e)
            flowOf(emptyList())
        }
    }

    private companion object {
        private const val TAG = "MarketplaceRepositoryImpl"
    }
}
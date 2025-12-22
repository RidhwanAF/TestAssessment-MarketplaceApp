package com.raf.marketplace.data.repository

import android.content.Context
import android.util.Log
import com.raf.core.data.utility.NetworkHelper.isNetworkAvailable
import com.raf.core.domain.model.ApiResult
import com.raf.marketplace.data.local.room.MarketplaceDatabase
import com.raf.marketplace.data.remote.MarketplaceApiService
import com.raf.marketplace.data.repository.mapper.CartMapper.toDomain
import com.raf.marketplace.data.repository.mapper.CartMapper.toEntity
import com.raf.marketplace.data.repository.mapper.ProductMapper.toDatabase
import com.raf.marketplace.data.repository.mapper.ProductMapper.toDomain
import com.raf.marketplace.data.utility.ProductQueryHelper
import com.raf.marketplace.domain.model.Cart
import com.raf.marketplace.domain.model.Product
import com.raf.marketplace.domain.model.ProductFilter
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
    private val cartDao = database.cartDao

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

    override suspend fun fetchProductById(
        token: String,
        productId: Int,
    ): ApiResult<Product> {
        if (!isNetworkAvailable(context)) {
            return getProductFromLocalByIdIfAvailable(productId, "No Internet Connection")
        }

        return try {
            val result = apiService.getProductById("Bearer $token", productId)
            if (result.isSuccessful) {
                val product =
                    result.body()?.toDatabase()
                        ?: return getProductFromLocalByIdIfAvailable(productId, "Product not found")

                productDao.updateProduct(product)

                val productDomain = product.toDomain()
                ApiResult.Success(productDomain)
            } else {
                val errorMessage = result.errorBody()?.string()?.takeIf { it.isNotEmpty() }
                    ?: result.message()
                Log.e(TAG, "Failed to fetch product detail: $errorMessage")
                getProductFromLocalByIdIfAvailable(productId, errorMessage)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch product detail", e)
            getProductFromLocalByIdIfAvailable(productId, e.localizedMessage ?: "Unknown Error")
        }
    }

    private suspend fun getProductFromLocalByIdIfAvailable(
        productId: Int,
        errorMessage: String = "",
    ): ApiResult<Product> {
        val productEntity =
            productDao.getProductById(productId) ?: return ApiResult.Error(errorMessage)
        return ApiResult.Success(productEntity.toDomain())
    }

    override fun getProductsByIds(productIds: List<Int>): Flow<List<Product>> {
        return productDao.getProductsByIds(productIds).map { productsEntity ->
            productsEntity.map { productEntity -> productEntity.toDomain() }
        }
    }

    override fun getProducts(productFilter: ProductFilter?): Flow<List<Product>> {
        return try {
            Log.d(TAG, "getProducts: $productFilter")
            val filter = productFilter ?: ProductFilter()

            val simpleSQLiteQuery = ProductQueryHelper.createFilteredQuery(
                query = filter.query,
                categories = filter.categories,
                sortType = filter.productSortType
            )

            productDao.getProductsFiltered(simpleSQLiteQuery).map { productsEntity ->
                productsEntity.map { productEntity ->
                    productEntity.toDomain()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get products", e)
            flowOf(emptyList())
        }
    }

    override fun getProductCategories(): Flow<List<String>> {
        return productDao.getCategories()
    }

    override suspend fun addToCart(cart: Cart): Result<Unit> {
        try {
            cartDao.addOrUpdateItem(cart.toEntity())
            return Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add item to cart", e)
            return Result.failure(e)
        }
    }

    override fun getAllItemFromCart(): Flow<List<Cart>> {
        return try {
            cartDao.getAllItems().map { cartEntities -> cartEntities.map { it.toDomain() } }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get all items from cart", e)
            flowOf(emptyList())
        }
    }

    override fun getItemCountFromCart(): Flow<Int> {
        return try {
            cartDao.getItemCount()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get item count from cart", e)
            flowOf(0)
        }
    }

    override suspend fun updateQuantityByProductId(productId: Int, newQuantity: Int) {
        try {
            cartDao.updateQuantityByProductId(productId, newQuantity)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update quantity by product ID", e)
        }
    }

    override suspend fun deleteItemCartByProductId(productId: Int): Result<Unit> {
        return try {
            cartDao.deleteItemByProductId(productId)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete item from cart", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteAllItemFromCart(): Result<Unit> {
        return try {
            cartDao.deleteAllItems()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete all items from cart", e)
            Result.failure(e)
        }
    }

    private companion object {
        private const val TAG = "MarketplaceRepositoryImpl"
    }
}
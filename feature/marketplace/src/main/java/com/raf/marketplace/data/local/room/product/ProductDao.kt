package com.raf.marketplace.data.local.room.product

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Int): ProductEntity?

    @Query("SELECT * FROM products WHERE id IN (:productIds)")
    fun getProductsByIds(productIds: List<Int>): Flow<List<ProductEntity>>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateProduct(product: ProductEntity)

//    @Query("SELECT * FROM products")
//    fun getProducts(): Flow<List<ProductEntity>>

    @Query("SELECT category FROM products GROUP BY category")
    fun getCategories(): Flow<List<String>>

    @RawQuery(observedEntities = [ProductEntity::class])
    fun getProductsFiltered(query: SimpleSQLiteQuery): Flow<List<ProductEntity>>
}
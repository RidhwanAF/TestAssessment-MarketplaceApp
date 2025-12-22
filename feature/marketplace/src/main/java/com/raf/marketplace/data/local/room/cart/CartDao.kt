package com.raf.marketplace.data.local.room.cart

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Upsert
    suspend fun upsertItem(item: CartEntity)

    @Query("SELECT * FROM cart WHERE productId = :productId")
    suspend fun getItemByProductId(productId: Int): CartEntity?

    @Transaction
    suspend fun addOrUpdateItem(item: CartEntity) {
        val existingItem = getItemByProductId(item.productId)
        if (existingItem != null) {
            val newQuantity = existingItem.quantity + item.quantity
            val updatedItem = existingItem.copy(
                quantity = newQuantity,
                timestamp = existingItem.timestamp
            )
            upsertItem(updatedItem)
        } else {
            upsertItem(item)
        }
    }

    @Query("SELECT * FROM cart ORDER BY timestamp DESC")
    fun getAllItems(): Flow<List<CartEntity>>

    @Query("UPDATE cart SET quantity = :newQuantity WHERE productId = :productId")
    suspend fun updateQuantityByProductId(productId: Int, newQuantity: Int)

    @Query("SELECT COUNT(*) FROM cart")
    fun getItemCount(): Flow<Int>

    @Query("DELETE FROM cart WHERE productId = :productId")
    suspend fun deleteItemByProductId(productId: Int)

    @Query("DELETE FROM cart")
    suspend fun deleteAllItems()
}
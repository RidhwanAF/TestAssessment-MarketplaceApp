package com.raf.marketplace.data.local.room.product

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val rating: RatingEntity,
)

@Serializable
data class RatingEntity(
    val rate: Double,
    val count: Int,
)
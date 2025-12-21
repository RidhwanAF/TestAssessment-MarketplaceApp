package com.raf.marketplace.data.repository.mapper

import com.raf.marketplace.data.local.room.product.ProductEntity
import com.raf.marketplace.data.local.room.product.RatingEntity
import com.raf.marketplace.data.remote.response.ProductResponse
import com.raf.marketplace.data.remote.response.RatingResponse
import com.raf.marketplace.domain.model.Product
import com.raf.marketplace.domain.model.Rating

object ProductMapper {
    fun ProductResponse.toDatabase() = ProductEntity(
        id = id,
        title = title,
        price = price,
        description = description,
        category = category,
        image = image,
        rating = rating.toDatabase()
    )

    private fun RatingResponse.toDatabase() = RatingEntity(
        rate = rate,
        count = count
    )

    fun ProductEntity.toDomain() = Product(
        id = id,
        title = title,
        priceInDollar = price,
        description = description,
        category = category,
        image = image,
        rating = rating.toDomain()
    )

    private fun RatingEntity.toDomain() = Rating(
        rate = rate,
        count = count
    )
}
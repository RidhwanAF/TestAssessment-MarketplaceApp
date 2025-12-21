package com.raf.marketplace.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val rating: RatingResponse,
)

@Serializable
data class RatingResponse(
    val rate: Double,
    val count: Int,
)

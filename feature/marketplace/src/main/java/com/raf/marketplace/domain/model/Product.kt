package com.raf.marketplace.domain.model

data class Product(
    val id: Int,
    val title: String,
    val priceInDollar: Double,
    val description: String,
    val category: String,
    val image: String,
    val rating: Rating,
)

data class Rating(
    val rate: Double,
    val count: Int,
)

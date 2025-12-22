package com.raf.marketplace.domain.model

data class ProductFilter(
    val query: String = "",
    val categories: List<String> = emptyList(),
    val productSortType: Pair<ProductSortType, Boolean>? = null,
)

enum class ProductSortType {
    NAME,
    PRICE,
    RATING,
}

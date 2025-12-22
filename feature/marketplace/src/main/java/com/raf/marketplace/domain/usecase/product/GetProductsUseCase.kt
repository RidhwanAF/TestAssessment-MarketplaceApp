package com.raf.marketplace.domain.usecase.product

import com.raf.marketplace.domain.model.ProductFilter
import com.raf.marketplace.domain.repository.MarketplaceRepository

class GetProductsUseCase(private val marketplaceRepository: MarketplaceRepository) {
    operator fun invoke(productFilter: ProductFilter? = null) =
        marketplaceRepository.getProducts(productFilter)

    operator fun invoke(productIds: List<Int>) = marketplaceRepository.getProductsByIds(productIds)
}
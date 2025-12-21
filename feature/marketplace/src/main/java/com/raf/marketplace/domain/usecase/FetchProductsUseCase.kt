package com.raf.marketplace.domain.usecase

import com.raf.marketplace.domain.repository.MarketplaceRepository

class FetchProductsUseCase(private val marketplaceRepository: MarketplaceRepository) {
    suspend operator fun invoke(token: String) = marketplaceRepository.fetchProducts(token)
}
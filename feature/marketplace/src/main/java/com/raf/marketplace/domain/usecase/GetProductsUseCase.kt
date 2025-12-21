package com.raf.marketplace.domain.usecase

import com.raf.marketplace.domain.repository.MarketplaceRepository

class GetProductsUseCase(private val marketplaceRepository: MarketplaceRepository) {
    operator fun invoke() = marketplaceRepository.getProducts()
}
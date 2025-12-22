package com.raf.marketplace.domain.usecase

import com.raf.marketplace.domain.repository.MarketplaceRepository

class FetchProductByIdUseCase(private val marketplaceRepository: MarketplaceRepository) {
    suspend operator fun invoke(token: String, productId: Int) =
        marketplaceRepository.fetchProductById(token, productId)
}
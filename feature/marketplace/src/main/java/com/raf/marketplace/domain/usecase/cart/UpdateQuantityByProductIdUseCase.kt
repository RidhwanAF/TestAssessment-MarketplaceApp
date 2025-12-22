package com.raf.marketplace.domain.usecase.cart

import com.raf.marketplace.domain.repository.MarketplaceRepository

class UpdateQuantityByProductIdUseCase(private val marketplaceRepository: MarketplaceRepository) {
    suspend operator fun invoke(productId: Int, newQuantity: Int) =
        marketplaceRepository.updateQuantityByProductId(productId, newQuantity)
}
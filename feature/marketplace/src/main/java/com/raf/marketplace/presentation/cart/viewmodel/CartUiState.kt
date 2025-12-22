package com.raf.marketplace.presentation.cart.viewmodel

import com.raf.marketplace.domain.model.Cart
import com.raf.marketplace.domain.model.Product

data class CartUiState(
    val productsInCart: List<ProductInCartUi> = emptyList(),
    val totalPriceInDollar: Double = 0.0,
    val totalQuantity: Int = 0,
    val uiMessage: String? = null,
)

data class ProductInCartUi(
    val product: Product,
    val cart: Cart,
)

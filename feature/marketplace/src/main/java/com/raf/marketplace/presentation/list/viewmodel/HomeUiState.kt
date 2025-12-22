package com.raf.marketplace.presentation.list.viewmodel

import com.raf.marketplace.domain.model.Product

data class HomeUiState(
    val products: List<Product> = emptyList(),
    val categories: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val uiMessage: String? = null
)

package com.raf.marketplace.presentation.detail.viewmodel

import com.raf.marketplace.domain.model.Product

data class DetailUiState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val uiMessage: String? = null,
    val showFullDesc: Boolean = false
)
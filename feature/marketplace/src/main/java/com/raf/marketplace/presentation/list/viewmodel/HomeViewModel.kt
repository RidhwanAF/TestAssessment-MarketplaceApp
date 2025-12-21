package com.raf.marketplace.presentation.list.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raf.core.domain.contract.AuthProvider
import com.raf.core.domain.model.ApiResult
import com.raf.marketplace.domain.usecase.FetchProductsUseCase
import com.raf.marketplace.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authProvider: AuthProvider,
    private val fetchProductsUseCase: FetchProductsUseCase,
    private val getProductsUseCase: GetProductsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchProducts()
        getProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val token = authProvider.getAuthToken().first() ?: ""
            when (val result = fetchProductsUseCase(token)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    Log.d(TAG, "fetchProducts: ${result.data}")
                }

                is ApiResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    showUiMessage(result.message)
                    Log.e(TAG, "fetchProducts: ${result.message}")
                }

                is ApiResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun getProducts() {
        viewModelScope.launch {
            getProductsUseCase().collect { products ->
                _uiState.update {
                    it.copy(products = products)
                }
                Log.d(TAG, "getProducts: $products")
            }
        }
    }

    private fun showUiMessage(message: String) {
        if (message.isEmpty()) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(errorMessage = message)
            }
            delay(1500)
            _uiState.update {
                it.copy(errorMessage = null)
            }
        }
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}
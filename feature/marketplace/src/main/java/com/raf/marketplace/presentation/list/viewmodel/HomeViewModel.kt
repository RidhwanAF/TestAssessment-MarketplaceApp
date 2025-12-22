package com.raf.marketplace.presentation.list.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raf.core.domain.contract.AuthProvider
import com.raf.core.domain.model.ApiResult
import com.raf.marketplace.domain.model.ProductFilter
import com.raf.marketplace.domain.model.ProductSortType
import com.raf.marketplace.domain.usecase.cart.GetItemCountFromCartUseCase
import com.raf.marketplace.domain.usecase.product.FetchProductsUseCase
import com.raf.marketplace.domain.usecase.product.GetProductCategoriesUseCase
import com.raf.marketplace.domain.usecase.product.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authProvider: AuthProvider,
    private val fetchProductsUseCase: FetchProductsUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val getProductCategoriesUseCase: GetProductCategoriesUseCase,
    private val getItemCountFromCartUseCase: GetItemCountFromCartUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _productFilterState = MutableStateFlow(ProductFilter())
    val productFilterState = _productFilterState.asStateFlow()

    init {
        fetchProducts()
        getProducts()
        getProductCategories()
        getCartItemCount()
    }

    private fun getCartItemCount() {
        viewModelScope.launch {
            getItemCountFromCartUseCase().collect {
                _uiState.update { state ->
                    state.copy(cartItemCount = it)
                }
            }
        }
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

    @OptIn(FlowPreview::class)
    private fun getProducts() {
        viewModelScope.launch {
            _productFilterState.debounce(500).collectLatest { filter ->
                Log.d(TAG, "getProducts: $filter")
                getProductsUseCase(productFilter = filter).collect { products ->
                    _uiState.update {
                        it.copy(products = products)
                    }
                    Log.d(TAG, "getProducts: $products")
                }
            }
        }
    }

    private fun getProductCategories() {
        viewModelScope.launch {
            getProductCategoriesUseCase().collect { categories ->
                _uiState.update {
                    it.copy(categories = categories)
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _productFilterState.update {
            it.copy(query = query)
        }
    }

    fun onCategoryChange(category: String) {
        val categories = _productFilterState.value.categories.toMutableList()

        if (categories.contains(category)) {
            categories.remove(category)
        } else {
            categories.add(category)
        }

        _productFilterState.update {
            it.copy(categories = categories.toList())
        }
    }

    fun onProductFilterByChange(productSortType: ProductSortType) {
        val currentSortType = _productFilterState.value.productSortType
        val newSortType = if (productSortType == currentSortType?.first) {
            null
        } else {
            productSortType to (currentSortType?.second ?: true)
        }

        _productFilterState.update {
            it.copy(productSortType = newSortType)
        }
    }

    fun onProductSortByChange(productSortType: ProductSortType, isSortAsc: Boolean) {
        _productFilterState.update {
            it.copy(productSortType = productSortType to isSortAsc)
        }
    }

    private fun showUiMessage(message: String) {
        if (message.isEmpty()) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(uiMessage = message)
            }
            delay(1500)
            _uiState.update {
                it.copy(uiMessage = null)
            }
        }
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}
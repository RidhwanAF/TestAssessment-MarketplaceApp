package com.raf.marketplace.presentation.detail.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raf.core.domain.contract.AuthProvider
import com.raf.core.domain.model.ApiResult
import com.raf.marketplace.domain.usecase.FetchProductByIdUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = DetailViewModel.Factory::class)
class DetailViewModel @AssistedInject constructor(
    @Assisted val productId: Int,
    private val authProvider: AuthProvider,
    private val fetchProductByIdUseCase: FetchProductByIdUseCase,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(deviceId: Int): DetailViewModel
    }

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getProductDetailById()
    }

    private fun getProductDetailById() {
        viewModelScope.launch {
            Log.d(TAG, "getProductDetailById: $productId")
            _uiState.update { it.copy(isLoading = true) }
            val token = authProvider.getAuthToken().first() ?: ""

            when (val result = fetchProductByIdUseCase(token = token, productId = productId)) {
                is ApiResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }

                is ApiResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, product = result.data) }
                    Log.d(TAG, "getProductDetailById: ${result.data}")
                }

                is ApiResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    Log.d(TAG, "getProductDetailById: ${result.message}")
                    showUiMessage(result.message)
                }
            }
        }
    }

    fun toggleFullDescription() {
        _uiState.update { it.copy(showFullDesc = !it.showFullDesc) }
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
        private const val TAG = "DetailViewModel"
    }
}
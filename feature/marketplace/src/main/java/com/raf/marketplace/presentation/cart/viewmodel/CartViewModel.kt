package com.raf.marketplace.presentation.cart.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raf.marketplace.domain.usecase.cart.DeleteItemCartUseCase
import com.raf.marketplace.domain.usecase.cart.GetAllItemFromCartUseCase
import com.raf.marketplace.domain.usecase.cart.UpdateQuantityByProductIdUseCase
import com.raf.marketplace.domain.usecase.product.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getAllItemFromCartUseCase: GetAllItemFromCartUseCase,
    private val deleteItemCartUseCase: DeleteItemCartUseCase,
    private val updateItemCartByProductIdUseCase: UpdateQuantityByProductIdUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getAllCartItem()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getAllCartItem() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllItemFromCartUseCase().flatMapLatest { carts ->
                val productIds = carts.map { it.productId }

                if (productIds.isEmpty()) {
                    flowOf(Pair(emptyList(), emptyList()))
                } else {
                    getProductsUseCase(productIds).map { products ->
                        Pair(carts, products)
                    }
                }
            }.collect { (carts, products) ->
                val productMap = products.associateBy { it.id }
                val productInCartList = carts.mapNotNull { cart ->
                    val product = productMap[cart.productId]
                    if (product != null) {
                        ProductInCartUi(product = product, cart = cart)
                    } else {
                        null
                    }
                }
                val totalPrice =
                    productInCartList.sumOf { it.product.priceInDollar * it.cart.quantity }
                val totalQuantity = productInCartList.sumOf { it.cart.quantity }

                _uiState.update {
                    it.copy(
                        productsInCart = productInCartList,
                        totalPriceInDollar = totalPrice,
                        totalQuantity = totalQuantity
                    )
                }
            }
        }
    }

    fun deleteCartItem(productId: Int) {
        viewModelScope.launch {
            deleteItemCartUseCase(productId)
            _uiState.update { it.copy(removingItemById = null) }
        }
    }

    fun updateCartItem(productId: Int, isSum: Boolean) {
        viewModelScope.launch {
            val currentProductInCart =
                _uiState.value.productsInCart.find { it.product.id == productId } ?: return@launch

            val quantity = if (isSum) {
                currentProductInCart.cart.quantity + 1
            } else {
                currentProductInCart.cart.quantity - 1
            }

            if (quantity == 0) {
                deleteItemCartUseCase(productId)
                return@launch
            }

            updateItemCartByProductIdUseCase(productId, quantity)
        }
    }

    fun onRemovingItemFromCart(productId: Int?) {
        _uiState.update { it.copy(removingItemById = productId) }
    }

//    var job: Job? = null
//    fun showUiMessage(message: String) {
//        if (message.isEmpty()) return
//        _uiState.update {
//            it.copy(uiMessage = null)
//        }
//        job?.cancel()
//        job = viewModelScope.launch {
//            _uiState.update {
//                it.copy(uiMessage = message)
//            }
//            delay(1500)
//            _uiState.update {
//                it.copy(uiMessage = null)
//            }
//        }
//    }

    companion object {
        private const val TAG = "CartViewModel"
    }
}
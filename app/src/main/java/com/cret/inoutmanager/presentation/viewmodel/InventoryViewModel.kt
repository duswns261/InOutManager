package com.cret.inoutmanager.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.domain.usecase.ProductUseCases
import kotlinx.coroutines.launch

/**
 * 재고 화면의 상태를 보관하고, UI 이벤트를 UseCase 작업으로 변환합니다.
 * 이제 Repository 대신 UseCase 레이어와 통신하여 비즈니스 로직을 분리합니다.
 */
class InventoryViewModel(
    private val useCases: ProductUseCases,
) : ViewModel() {

    private val _products = mutableStateListOf<Product>()
    val products: List<Product> get() = _products

    init {
        viewModelScope.launch {
            useCases.getProducts().collect { savedProducts ->
                _products.clear()
                _products.addAll(savedProducts)
            }
        }
    }

    fun addProduct(name: String, location: String, quantityStr: String) {
        val qty = quantityStr.toIntOrNull() ?: 0
        viewModelScope.launch {
            useCases.addProduct(name, location, qty)
        }
    }

    fun decreaseQuantity(targetProduct: Product, amount: Int) {
        viewModelScope.launch {
            useCases.decreaseProductQuantity(targetProduct, amount)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            useCases.deleteProduct(product)
        }
    }

    companion object {
        /**
         * UseCase 의존성이 필요한 ViewModel을 AndroidX viewModels API에서 생성하기 위한 Factory입니다.
         */
        fun provideFactory(
            useCases: ProductUseCases,
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
                        return InventoryViewModel(useCases) as T
                    }
                    throw IllegalArgumentException(
                        "Unknown ViewModel class: ${modelClass.name}"
                    )
                }
            }
        }
    }
}

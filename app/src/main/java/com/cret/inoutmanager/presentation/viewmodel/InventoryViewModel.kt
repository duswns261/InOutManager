package com.cret.inoutmanager.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cret.inoutmanager.data.model.Product
import com.cret.inoutmanager.data.repository.ProductRepository
import kotlinx.coroutines.launch

/**
 * 재고 화면의 상태를 보관하고, UI 이벤트를 Repository 작업으로 변환합니다.
 */
class InventoryViewModel(
    private val repository: ProductRepository,
) : ViewModel() {

    private val _products = mutableStateListOf<Product>()
    val products: List<Product> get() = _products

    init {
        viewModelScope.launch {
            repository.allProducts.collect { savedProducts ->
                _products.clear()
                _products.addAll(savedProducts)
            }
        }
    }

    fun addProduct(name: String, location: String, quantityStr: String) {
        val qty = quantityStr.toIntOrNull() ?: 0
        val newProduct = Product(
            name = name,
            location = location,
            quantity = qty
        )

        viewModelScope.launch {
            repository.insert(newProduct)
        }
    }

    fun decreaseQuantity(targetProduct: Product, amount: Int) {
        // 출고 후 재고가 음수가 되지 않도록 앱 정책상 최소값을 0으로 제한합니다.
        val newQuantity = (targetProduct.quantity - amount).coerceAtLeast(0)
        val updatedProduct = targetProduct.copy(quantity = newQuantity)

        viewModelScope.launch {
            repository.update(updatedProduct)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.delete(product)
        }
    }

    companion object {
        /**
         * Repository 의존성이 필요한 ViewModel을 AndroidX viewModels API에서 생성하기 위한 Factory입니다.
         */
        fun provideFactory(
            repository: ProductRepository,
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
                        return InventoryViewModel(repository) as T
                    }
                    throw IllegalArgumentException(
                        "Unknown ViewModel class: ${modelClass.name}"
                    )
                }
            }
        }
    }
}

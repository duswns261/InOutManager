package com.cret.inoutmanger.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cret.inoutmanger.data.dao.ProductDao
import com.cret.inoutmanger.data.model.Product
import com.cret.inoutmanger.data.repository.ProductRepository
import kotlinx.coroutines.launch

// 생성자로 dao를 받습니다.
class InventoryViewModel(private val repository: ProductRepository) : ViewModel() {

    // 화면에 보여줄 리스트 (이제 더미 데이터 없이 비어있는 상태로 시작)
    private val _products = mutableStateListOf<Product>()
    val products: List<Product> get() = _products

    init {
        // 앱이 켜지면 DB를 감시하다가 데이터가 바뀌면 리스트를 갱신합니다.
        viewModelScope.launch {
            repository.allProducts.collect { savedProducts ->
                _products.clear()
                _products.addAll(savedProducts)
            }
        }
    }

    // 신규 등록 (DB에 저장)
    fun addProduct(name: String, location: String, quantityStr: String) {
        val qty = quantityStr.toIntOrNull() ?: 0
        val newProduct = Product(name = name, location = location, quantity = qty)

        viewModelScope.launch {
            repository.insert(newProduct)
        }
    }

    // 출고 (DB 업데이트)
    fun decreaseQuantity(targetProduct: Product, amount: Int) {
        val newQuantity = (targetProduct.quantity - amount).coerceAtLeast(0)
        val updatedProduct = targetProduct.copy(quantity = newQuantity)

        viewModelScope.launch {
            repository.update(updatedProduct)
        }
    }

    // [추가] 삭제 함수
    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.delete(product)
        }
    }
}
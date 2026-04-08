package com.cret.inoutmanager.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cret.inoutmanager.data.model.Product
import com.cret.inoutmanager.data.repository.ProductRepository
import kotlinx.coroutines.launch

class InventoryViewModel(
    private val repository: ProductRepository,
) : ViewModel() {

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
        val newProduct = Product(
            name = name,
            location = location,
            quantity = qty
        )

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

    // InventoryViewModel 이 생성자에서 ProductRepository 를 받기 때문에,
    // 안드로이드 프레임워크가 기본 생성자로 ViewModel 을 생성할 수 없기 때문에 직접 생성 규칙을 제공하는 팩토리 메서드를 만드는 코드입니다.
    // 안드로이드 공식 문서에서도 ViewModel 이 생성자 의존성을 받으면 ViewModelProvider.Factory 를 제공하라고 설명하고 있습니다.
    companion object {
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

// 질문, companion object, class 간 서로 활용할 수 있는 범위 확인 필요, 컴패니언에서는 외부 접근 불가, 외부 클래스에선 컴패니언 함수 접근 가능한 것으로 보임
// 확실히 알아볼 것!
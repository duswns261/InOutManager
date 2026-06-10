package com.cret.inoutmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.domain.usecase.ProductUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 재고 화면의 상태를 보관하고, UI 이벤트를 UseCase 작업으로 변환합니다.
 * StateFlow를 사용하여 UI 상태를 단방향(UDF)으로 관리합니다.
 */
class InventoryViewModel(
    private val useCases: ProductUseCases,
) : ViewModel() {

    // 내부의 가변적인 상태들을 담는 MutableStateFlow
    private val _internalUiState = MutableStateFlow(InventoryUiState())

    // 전체 UI 상태를 결합하여 외부에 노출하는 StateFlow
    val uiState: StateFlow<InventoryUiState> = combine(
        useCases.getProducts(),
        _internalUiState
    ) { products, internalState ->
        internalState.copy(products = products)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InventoryUiState(isLoading = true)
    )

    // --- UI 이벤트 처리 ---

    fun onShowAddDialog(show: Boolean) {
        _internalUiState.update { it.copy(showAddDialog = show) }
    }

    fun onOutboundProductSelected(product: Product?) {
        _internalUiState.update { 
            it.copy(
                selectedProductForOutbound = product,
                outboundQuantityInput = "",
                showConfirmDialog = false
            )
        }
    }

    fun onOutboundQuantityChanged(input: String) {
        _internalUiState.update { it.copy(outboundQuantityInput = input) }
    }

    fun onShowConfirmDialog(show: Boolean) {
        _internalUiState.update { it.copy(showConfirmDialog = show) }
    }

    // --- 비즈니스 로직 호출 ---

    fun addProduct(name: String, location: String, quantityStr: String) {
        val qty = quantityStr.toIntOrNull() ?: 0
        viewModelScope.launch {
            useCases.addProduct(name, location, qty)
            onShowAddDialog(false) // 성공 시 다이얼로그 닫기
        }
    }

    fun decreaseQuantity(targetProduct: Product, amountStr: String) {
        val amount = amountStr.toIntOrNull() ?: 0
        viewModelScope.launch {
            useCases.decreaseProductQuantity(targetProduct, amount)
            onOutboundProductSelected(null) // 성공 시 상태 초기화
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            useCases.deleteProduct(product)
        }
    }

    companion object {
        fun provideFactory(
            useCases: ProductUseCases,
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
                        return InventoryViewModel(useCases) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
        }
    }
}

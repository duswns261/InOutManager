package com.cret.inoutmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cret.inoutmanager.data.repository.ProductRepository

// 질문, 코드 분석
// Factory 생성을 InvetoryViewModel 에서 companion object 로 생성하도록 이동함.
//class InventoryViewModelFactory (
//    private val productRepository: ProductRepository
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return InventoryViewModel(productRepository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}

// 여러 뷰모델이 비슷한 생성 규칙을 공유, 팩토리 로직이 길거나 복잡한 상황 등 팩토리를 나눠야 할 경우와는 먼 상황이라고 생각하여 뷰모델에 합침.
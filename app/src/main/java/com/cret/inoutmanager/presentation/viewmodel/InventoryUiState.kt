package com.cret.inoutmanager.presentation.viewmodel

import com.cret.inoutmanager.domain.model.Product

data class InventoryUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    /**
     * 이미지 추가·교체·제거 자체는 성공했지만 이전 관리 파일 정리에는 실패했을 때 true로 설정됩니다.
     * 데이터 변경 성공 여부([errorMessage]와 무관)와 구분되는 관찰 가능한 신호이며, UI가 안내를
     * 표시한 뒤 [InventoryViewModel.consumeImageCleanupWarning]으로 소비합니다.
     */
    val imageCleanupWarning: Boolean = false,
)

package com.cret.inoutmanager.presentation.viewmodel

import com.cret.inoutmanager.domain.model.Product

/**
 * 재고 화면의 전체 UI 상태를 정의하는 데이터 클래스입니다.
 * 
 * @property products 현재 표시할 제품 목록
 * @property isLoading 데이터 로딩 중 여부
 * @property showAddDialog 신규 제품 등록 다이얼로그 노출 여부
 * @property selectedProductForOutbound 출고 작업을 위해 선택된 제품 (null이면 미노출)
 * @property showConfirmDialog 출고 최종 확인 다이얼로그 노출 여부
 * @property outboundQuantityInput 출고 수량 입력값
 */
data class InventoryUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val showAddDialog: Boolean = false,
    val selectedProductForOutbound: Product? = null,
    val showConfirmDialog: Boolean = false,
    val outboundQuantityInput: String = ""
) {
    /**
     * 출고 수량 입력 다이얼로그 노출 여부를 판단합니다.
     */
    val showOutboundInput: Boolean get() = selectedProductForOutbound != null && !showConfirmDialog
}

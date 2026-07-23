package com.cret.inoutmanager.domain.usecase

import com.cret.inoutmanager.domain.model.Product

/**
 * 이미지 추가·교체·제거 use case의 결과입니다. [product]는 DB 변경이 실제로 성공했을 때만
 * 반환되며, [cleanupSucceeded]는 이전 관리 이미지 정리 결과를 별도로 관찰할 수 있게 합니다.
 * cleanup 실패는 이미 성공한 [product] 상태를 되돌리지 않습니다.
 */
data class ProductImageMutationResult(
    val product: Product,
    val cleanupSucceeded: Boolean,
)

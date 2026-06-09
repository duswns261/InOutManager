package com.cret.inoutmanager.domain.usecase

import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.domain.repository.ProductRepository

/**
 * 제품의 재고 수량을 감소시키는 UseCase입니다.
 * 재고가 0 미만이 되지 않도록 하는 비즈니스 로직을 포함합니다.
 */
class DecreaseProductQuantityUseCase(
    private val repository: ProductRepository,
) {
    suspend operator fun invoke(product: Product, amount: Int) {
        // 음수 또는 0의 amount는 출고 요청으로 처리하지 않습니다.
        if (amount <= 0) return

        // 현재 재고가 음수인 비정상 데이터일 경우를 대비해 0 이상으로 보정합니다.
        val normalizedCurrentQuantity = product.quantity.coerceAtLeast(0)
        val newQuantity = (normalizedCurrentQuantity - amount).coerceAtLeast(0)

        // 기존 값과 계산 결과가 같으면 불필요한 DB 업데이트를 생략합니다.
        if (product.quantity == newQuantity) return

        repository.update(
            product.copy(quantity = newQuantity)
        )
    }
}

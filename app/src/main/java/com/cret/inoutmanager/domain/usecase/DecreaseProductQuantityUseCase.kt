package com.cret.inoutmanager.domain.usecase

import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.domain.repository.ProductRepository

/**
 * 제품의 재고 수량을 감소시키는 UseCase입니다.
 * 재고가 0 미만이 되지 않도록 하는 비즈니스 로직을 포함합니다.
 */
class DecreaseProductQuantityUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(product: Product, amount: Int) {
        val newQuantity = (product.quantity - amount).coerceAtLeast(0)
        val updatedProduct = product.copy(quantity = newQuantity)
        repository.update(updatedProduct)
    }
}

package com.cret.inoutmanager.domain.usecase

import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.domain.repository.ProductRepository

/**
 * 제품을 삭제하는 UseCase입니다.
 */
class DeleteProductUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(product: Product) {
        repository.delete(product)
    }
}

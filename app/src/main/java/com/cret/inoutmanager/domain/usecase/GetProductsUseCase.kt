package com.cret.inoutmanager.domain.usecase

import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

/**
 * 저장된 모든 제품 목록을 가져오는 UseCase입니다.
 */
class GetProductsUseCase(
    private val repository: ProductRepository
) {
    operator fun invoke(): Flow<List<Product>> {
        return repository.allProducts
    }
}

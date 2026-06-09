package com.cret.inoutmanager.domain.usecase

import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.domain.repository.ProductRepository

/**
 * 새로운 제품을 등록하는 UseCase입니다.
 */
class AddProductUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(name: String, location: String, quantity: Int) {
        val product = Product(
            name = name,
            location = location,
            quantity = quantity
        )
        repository.insert(product)
    }
}

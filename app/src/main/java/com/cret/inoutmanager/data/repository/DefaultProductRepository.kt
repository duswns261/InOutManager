package com.cret.inoutmanager.data.repository

import com.cret.inoutmanager.data.datasource.local.ProductLocalDataSource
import com.cret.inoutmanager.data.mapper.toDomain
import com.cret.inoutmanager.data.mapper.toEntity
import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * [ProductRepository]의 기본 구현체입니다.
 * [ProductLocalDataSource]를 통해 로컬 데이터를 관리하며, 
 * DAO를 직접 참조하지 않고 데이터 소스 계층을 통해 통신합니다.
 */
class DefaultProductRepository(
    private val localDataSource: ProductLocalDataSource
) : ProductRepository {

    override val allProducts: Flow<List<Product>> =
        localDataSource.getAllProducts()
            .map { entities ->
                entities.map { it.toDomain() }
            }

    override suspend fun insert(product: Product) {
        localDataSource.insertProduct(product.toEntity())
    }

    override suspend fun update(product: Product) {
        localDataSource.updateProduct(product.toEntity())
    }

    override suspend fun delete(product: Product) {
        localDataSource.deleteProduct(product.toEntity())
    }
}

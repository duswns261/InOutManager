package com.cret.inoutmanager.data.repository

import com.cret.inoutmanager.data.dao.ProductDao
import com.cret.inoutmanager.data.mapper.toDomain
import com.cret.inoutmanager.data.mapper.toEntity
import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultProductRepository(
    private val productDao: ProductDao
) : ProductRepository {

    override val allProducts: Flow<List<Product>> =
        productDao.getAllProducts()
            .map { entities ->
                entities.map { it.toDomain() }
            }

    override suspend fun insert(product: Product) {
        productDao.insertProduct(product.toEntity())
    }

    override suspend fun update(product: Product) {
        productDao.updateProduct(product.toEntity())
    }

    override suspend fun delete(product: Product) {
        productDao.deleteProduct(product.toEntity())
    }
}

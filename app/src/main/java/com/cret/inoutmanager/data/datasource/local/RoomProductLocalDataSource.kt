package com.cret.inoutmanager.data.datasource.local

import com.cret.inoutmanager.data.dao.ProductDao
import com.cret.inoutmanager.data.model.ProductEntity
import kotlinx.coroutines.flow.Flow

/**
 * [ProductLocalDataSource]의 Room 데이터베이스 구현체입니다.
 * [ProductDao]를 생성자 주입 받아 실제 DB 작업을 수행합니다.
 */
class RoomProductLocalDataSource(
    private val productDao: ProductDao
) : ProductLocalDataSource {

    override fun getAllProducts(): Flow<List<ProductEntity>> {
        return productDao.getAllProducts()
    }

    override suspend fun insertProduct(product: ProductEntity) {
        productDao.insertProduct(product)
    }

    override suspend fun updateProduct(product: ProductEntity) {
        productDao.updateProduct(product)
    }

    override suspend fun deleteProduct(product: ProductEntity) {
        productDao.deleteProduct(product)
    }
}

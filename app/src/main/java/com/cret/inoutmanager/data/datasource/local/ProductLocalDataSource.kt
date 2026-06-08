package com.cret.inoutmanager.data.datasource.local

import com.cret.inoutmanager.data.model.ProductEntity
import kotlinx.coroutines.flow.Flow

/**
 * 로컬 데이터베이스(Room)와의 상호작용을 캡슐화하는 인터페이스입니다.
 */
interface ProductLocalDataSource {
    fun getAllProducts(): Flow<List<ProductEntity>>
    suspend fun insertProduct(product: ProductEntity)
    suspend fun updateProduct(product: ProductEntity)
    suspend fun deleteProduct(product: ProductEntity)
}

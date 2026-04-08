package com.cret.inoutmanager.data.repository

import com.cret.inoutmanager.data.dao.ProductDao
import com.cret.inoutmanager.data.model.Product
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {

    // 1. 전체 데이터 가져오기 (Dao의 Flow를 그대로 전달)
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()

    // 2. 데이터 추가
    suspend fun insert(product: Product) {
        productDao.insertProduct(product)
    }

    // 3. 데이터 수정 (출고)
    suspend fun update(product: Product) {
        productDao.updateProduct(product)
    }

    // 4. 데이터 삭제
    suspend fun delete(product: Product) {
        productDao.deleteProduct(product)
    }
}
package com.cret.inoutmanager.domain.repository

import com.cret.inoutmanager.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    val allProducts: Flow<List<Product>>

    suspend fun insert(product: Product)

    suspend fun update(product: Product)

    suspend fun delete(product: Product)
}

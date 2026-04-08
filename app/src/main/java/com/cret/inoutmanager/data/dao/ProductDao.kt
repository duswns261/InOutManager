package com.cret.inoutmanager.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.cret.inoutmanager.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    // 모든 제품 가져오기 (실시간 업데이트를 위해 Flow 사용)
    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProducts(): Flow<List<Product>>

    // 제품 추가
    @Insert
    suspend fun insertProduct(product: Product)

    // 제품 수정 (출고 시 수량 변경용)
    @Update
    suspend fun updateProduct(product: Product)

    // [추가] 제품 삭제
    @Delete
    suspend fun deleteProduct(product: Product)
}
package com.cret.inoutmanger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity: 이 클래스를 DB 테이블로 쓰겠다는 뜻입니다.
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) // id는 자동으로 1씩 증가하며 생성됨
    val id: Int = 0,
    val name: String,
    val location: String,
    val quantity: Int
)

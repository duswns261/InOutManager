package com.cret.inoutmanager.data.mapper

import com.cret.inoutmanager.data.model.ProductEntity
import com.cret.inoutmanager.domain.model.Product

fun ProductEntity.toDomain(): Product {
    return Product(
        id = id,
        name = name,
        location = location,
        quantity = quantity
    )
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        location = location,
        quantity = quantity
    )
}

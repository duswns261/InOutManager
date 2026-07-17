package com.cret.inoutmanager.data.mapper

import com.cret.inoutmanager.data.model.ProductEntity
import com.cret.inoutmanager.domain.model.Product
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductMapperTest {

    @Test
    fun `entity with non-null imagePath maps to domain preserving imagePath`() {
        val entity = ProductEntity(
            id = 1,
            name = "펜",
            location = "A-1",
            quantity = 5,
            imagePath = "/data/images/pen.jpg"
        )

        val product = entity.toDomain()

        assertEquals("/data/images/pen.jpg", product.imagePath)
    }

    @Test
    fun `entity with null imagePath maps to domain preserving null`() {
        val entity = ProductEntity(
            id = 1,
            name = "펜",
            location = "A-1",
            quantity = 5,
            imagePath = null
        )

        val product = entity.toDomain()

        assertEquals(null, product.imagePath)
    }

    @Test
    fun `domain with non-null imagePath maps to entity preserving imagePath`() {
        val product = Product(
            id = 1,
            name = "펜",
            location = "A-1",
            quantity = 5,
            imagePath = "/data/images/pen.jpg"
        )

        val entity = product.toEntity()

        assertEquals("/data/images/pen.jpg", entity.imagePath)
    }

    @Test
    fun `domain with null imagePath maps to entity preserving null`() {
        val product = Product(
            id = 1,
            name = "펜",
            location = "A-1",
            quantity = 5,
            imagePath = null
        )

        val entity = product.toEntity()

        assertEquals(null, entity.imagePath)
    }
}

package com.cret.inoutmanager.domain.usecase

import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.domain.repository.ProductImageStorage
import com.cret.inoutmanager.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.File
import java.io.InputStream

class DeleteProductUseCaseTest {

    private class FakeProductRepository(
        private val deleteError: Throwable? = null,
    ) : ProductRepository {
        val deleted = mutableListOf<Product>()
        override val allProducts: Flow<List<Product>> = flowOf(emptyList())
        override suspend fun insert(product: Product) {}
        override suspend fun update(product: Product) {}
        override suspend fun delete(product: Product) {
            deleteError?.let { throw it }
            deleted += product
        }
    }

    private class FakeProductImageStorage : ProductImageStorage {
        val deleted = mutableListOf<File>()
        override fun createTemporaryFile(): File = File.createTempFile("test", ".jpg")
        override fun commit(temporaryFile: File): File = temporaryFile
        override fun importTemporaryFile(input: InputStream): File = File.createTempFile("test", ".jpg")
        override fun isUsableManagedImage(file: File): Boolean = file.exists()
        override fun delete(file: File) {
            deleted += file
        }
    }

    private fun product(imagePath: String?) = Product(
        id = 1,
        name = "펜",
        location = "A-1",
        quantity = 5,
        imagePath = imagePath,
    )

    @Test
    fun `invoke deletes the image file after repository delete succeeds`() = runTest {
        val repository = FakeProductRepository()
        val imageStorage = FakeProductImageStorage()
        val sut = DeleteProductUseCase(repository, imageStorage)
        val target = product(imagePath = "/data/product-images/a.jpg")

        sut(target)

        assertEquals(listOf(target), repository.deleted)
        assertEquals(listOf(File("/data/product-images/a.jpg")), imageStorage.deleted)
    }

    @Test
    fun `invoke does not delete image and rethrows when repository delete fails`() = runTest {
        val repository = FakeProductRepository(deleteError = RuntimeException("delete failed"))
        val imageStorage = FakeProductImageStorage()
        val sut = DeleteProductUseCase(repository, imageStorage)
        val target = product(imagePath = "/data/product-images/a.jpg")

        try {
            sut(target)
            fail("delete 실패가 예외로 전달되어야 합니다")
        } catch (e: RuntimeException) {
            assertEquals("delete failed", e.message)
        }

        assertTrue(imageStorage.deleted.isEmpty())
    }

    @Test
    fun `invoke skips image deletion when imagePath is null`() = runTest {
        val repository = FakeProductRepository()
        val imageStorage = FakeProductImageStorage()
        val sut = DeleteProductUseCase(repository, imageStorage)

        sut(product(imagePath = null))

        assertTrue(imageStorage.deleted.isEmpty())
    }

    @Test
    fun `invoke skips image deletion when imagePath is blank`() = runTest {
        val repository = FakeProductRepository()
        val imageStorage = FakeProductImageStorage()
        val sut = DeleteProductUseCase(repository, imageStorage)

        sut(product(imagePath = "   "))

        assertTrue(imageStorage.deleted.isEmpty())
    }

    @Test
    fun `invoke deletes the repository record before the image file`() = runTest {
        val callOrder = mutableListOf<String>()
        val repository = object : ProductRepository {
            override val allProducts: Flow<List<Product>> = flowOf(emptyList())
            override suspend fun insert(product: Product) {}
            override suspend fun update(product: Product) {}
            override suspend fun delete(product: Product) {
                callOrder += "repository"
            }
        }
        val imageStorage = object : ProductImageStorage {
            override fun createTemporaryFile(): File = File.createTempFile("test", ".jpg")
            override fun commit(temporaryFile: File): File = temporaryFile
            override fun importTemporaryFile(input: InputStream): File = File.createTempFile("test", ".jpg")
            override fun isUsableManagedImage(file: File): Boolean = file.exists()
            override fun delete(file: File) {
                callOrder += "storage"
            }
        }
        val sut = DeleteProductUseCase(repository, imageStorage)

        sut(product(imagePath = "/data/product-images/a.jpg"))

        assertEquals(listOf("repository", "storage"), callOrder)
    }
}

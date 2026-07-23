package com.cret.inoutmanager.domain.usecase

import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.domain.repository.ProductImageStorage
import com.cret.inoutmanager.domain.repository.ProductRepository
import java.io.File
import java.io.InputStream
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class RemoveProductImageUseCaseTest {

    private class FakeProductRepository(
        private val updateError: Throwable? = null,
    ) : ProductRepository {
        val updated = mutableListOf<Product>()
        override val allProducts: Flow<List<Product>> = flowOf(emptyList())
        override suspend fun insert(product: Product) {}
        override suspend fun update(product: Product) {
            updateError?.let { throw it }
            updated += product
        }
        override suspend fun delete(product: Product) {}
    }

    private class FakeProductImageStorage(
        private val deleteError: Throwable? = null,
        private val deleteResult: Boolean = true,
    ) : ProductImageStorage {
        val deleted = mutableListOf<File>()

        override fun createTemporaryFile(): File = File.createTempFile("test", ".jpg")
        override fun commit(temporaryFile: File): File = temporaryFile
        override fun importTemporaryFile(input: InputStream): File = File.createTempFile("test", ".jpg")
        override fun isUsableManagedImage(file: File): Boolean = file.exists()

        override fun delete(file: File): Boolean {
            deleted += file
            deleteError?.let { throw it }
            return deleteResult
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
    fun `invoke on a product with an existing image clears imagePath and deletes the managed file`() = runTest {
        val repository = FakeProductRepository()
        val imageStorage = FakeProductImageStorage()
        val sut = RemoveProductImageUseCase(repository, imageStorage)
        val previousImagePath = "/data/product-images/old.jpg"
        val target = product(imagePath = previousImagePath)

        val result = sut(target)

        assertNull(result.product.imagePath)
        assertTrue(result.cleanupSucceeded)
        assertEquals(listOf(target.copy(imagePath = null)), repository.updated)
        assertEquals(listOf(File(previousImagePath)), imageStorage.deleted)
    }

    @Test
    fun `invoke on a product without an image is a no-op`() = runTest {
        val repository = FakeProductRepository()
        val imageStorage = FakeProductImageStorage()
        val sut = RemoveProductImageUseCase(repository, imageStorage)
        val target = product(imagePath = null)

        val result = sut(target)

        assertNull(result.product.imagePath)
        assertTrue(result.cleanupSucceeded)
        assertTrue(repository.updated.isEmpty())
        assertTrue(imageStorage.deleted.isEmpty())
    }

    @Test
    fun `invoke rethrows and leaves the image untouched when DB update fails`() = runTest {
        val repository = FakeProductRepository(updateError = RuntimeException("update failed"))
        val imageStorage = FakeProductImageStorage()
        val sut = RemoveProductImageUseCase(repository, imageStorage)
        val target = product(imagePath = "/data/product-images/old.jpg")

        try {
            sut(target)
            fail("DB update 실패가 예외로 전달되어야 합니다")
        } catch (e: RuntimeException) {
            assertEquals("update failed", e.message)
        }

        assertTrue(repository.updated.isEmpty())
        assertTrue(imageStorage.deleted.isEmpty())
    }

    @Test
    fun `invoke still returns the updated product when cleanup throws`() = runTest {
        val repository = FakeProductRepository()
        val imageStorage = FakeProductImageStorage(deleteError = RuntimeException("cleanup failed"))
        val sut = RemoveProductImageUseCase(repository, imageStorage)
        val target = product(imagePath = "/data/product-images/old.jpg")

        val result = sut(target)

        assertNull(result.product.imagePath)
        assertEquals(listOf(target.copy(imagePath = null)), repository.updated)
        assertTrue(!result.cleanupSucceeded)
    }

    @Test
    fun `invoke reports cleanup failure when delete returns false without throwing`() = runTest {
        val repository = FakeProductRepository()
        val imageStorage = FakeProductImageStorage(deleteResult = false)
        val sut = RemoveProductImageUseCase(repository, imageStorage)
        val target = product(imagePath = "/data/product-images/old.jpg")

        val result = sut(target)

        // File.delete() == false는 예외를 던지지 않으므로 이 경로가 조용히 성공 처리되지 않는지 별도로 검증한다.
        assertNull(result.product.imagePath)
        assertEquals(listOf(target.copy(imagePath = null)), repository.updated)
        assertTrue(!result.cleanupSucceeded)
    }
}

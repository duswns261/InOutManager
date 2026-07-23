package com.cret.inoutmanager.domain.usecase

import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.domain.repository.ProductImageStorage
import com.cret.inoutmanager.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.File
import java.io.InputStream

class AddProductUseCaseTest {

    private class FakeProductRepository(
        private val insertError: Throwable? = null,
    ) : ProductRepository {
        val inserted = mutableListOf<Product>()
        override val allProducts: Flow<List<Product>> = flowOf(emptyList())

        override suspend fun insert(product: Product) {
            insertError?.let { throw it }
            inserted += product
        }

        override suspend fun update(product: Product) {}
        override suspend fun delete(product: Product) {}
    }

    private class FakeProductImageStorage : ProductImageStorage {
        val committed = mutableListOf<File>()
        val deleted = mutableListOf<File>()
        var commitResult: (File) -> File = { it }

        override fun createTemporaryFile(): File = File.createTempFile("test", ".jpg")

        override fun commit(temporaryFile: File): File {
            committed += temporaryFile
            return commitResult(temporaryFile)
        }

        override fun importTemporaryFile(input: InputStream): File = File.createTempFile("test", ".jpg")
        override fun isUsableManagedImage(file: File): Boolean = file.exists()

        override fun delete(file: File): Boolean {
            deleted += file
            return true
        }
    }

    @Test
    fun `invoke without image inserts product with null imagePath and does not touch storage`() = runTest {
        val repository = FakeProductRepository()
        val imageStorage = FakeProductImageStorage()
        val sut = AddProductUseCase(repository, imageStorage)

        sut(name = "펜", location = "A-1", quantity = 5, temporaryImageFile = null)

        assertEquals(1, repository.inserted.size)
        assertNull(repository.inserted.single().imagePath)
        assertTrue(imageStorage.committed.isEmpty())
    }

    @Test
    fun `invoke with image commits the temporary file and inserts its absolute path`() = runTest {
        val repository = FakeProductRepository()
        val imageStorage = FakeProductImageStorage()
        val permanentFile = File.createTempFile("permanent", ".jpg")
        imageStorage.commitResult = { permanentFile }
        val sut = AddProductUseCase(repository, imageStorage)
        val temporaryFile = File.createTempFile("temp", ".jpg")

        sut(name = "펜", location = "A-1", quantity = 5, temporaryImageFile = temporaryFile)

        assertEquals(listOf(temporaryFile), imageStorage.committed)
        assertEquals(permanentFile.absolutePath, repository.inserted.single().imagePath)
    }

    @Test
    fun `invoke deletes the committed file and rethrows when DB insert fails`() = runTest {
        val repository = FakeProductRepository(insertError = RuntimeException("insert failed"))
        val imageStorage = FakeProductImageStorage()
        val permanentFile = File.createTempFile("permanent", ".jpg")
        imageStorage.commitResult = { permanentFile }
        val sut = AddProductUseCase(repository, imageStorage)
        val temporaryFile = File.createTempFile("temp", ".jpg")

        try {
            sut(name = "펜", location = "A-1", quantity = 5, temporaryImageFile = temporaryFile)
            fail("insert 실패가 예외로 전달되어야 합니다")
        } catch (e: RuntimeException) {
            assertEquals("insert failed", e.message)
        }

        assertEquals(listOf(permanentFile), imageStorage.deleted)
        assertTrue(repository.inserted.isEmpty())
    }

    @Test
    fun `invoke deletes the original temporary file and rethrows when commit fails`() = runTest {
        val repository = FakeProductRepository()
        val imageStorage = FakeProductImageStorage()
        imageStorage.commitResult = { throw RuntimeException("commit failed") }
        val sut = AddProductUseCase(repository, imageStorage)
        val temporaryFile = File.createTempFile("temp", ".jpg")

        try {
            sut(name = "펜", location = "A-1", quantity = 5, temporaryImageFile = temporaryFile)
            fail("commit 실패가 예외로 전달되어야 합니다")
        } catch (e: RuntimeException) {
            assertEquals("commit failed", e.message)
        }

        assertEquals(listOf(temporaryFile), imageStorage.deleted)
        assertTrue(repository.inserted.isEmpty())
    }
}

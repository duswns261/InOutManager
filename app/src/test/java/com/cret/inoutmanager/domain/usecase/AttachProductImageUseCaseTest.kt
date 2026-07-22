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
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class AttachProductImageUseCaseTest {

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
        private val commitError: Throwable? = null,
        private val deleteError: Throwable? = null,
    ) : ProductImageStorage {
        val committed = mutableListOf<File>()
        val deleted = mutableListOf<File>()
        var commitResult: (File) -> File = { it }

        override fun createTemporaryFile(): File = File.createTempFile("test", ".jpg")

        override fun commit(temporaryFile: File): File {
            commitError?.let { throw it }
            committed += temporaryFile
            return commitResult(temporaryFile)
        }

        override fun importTemporaryFile(input: InputStream): File = File.createTempFile("test", ".jpg")
        override fun isUsableManagedImage(file: File): Boolean = file.exists()

        override fun delete(file: File) {
            deleted += file
            deleteError?.let { throw it }
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
    fun `invoke on a product without an image commits and updates without deleting anything`() = runTest {
        val repository = FakeProductRepository()
        val imageStorage = FakeProductImageStorage()
        val permanentFile = File.createTempFile("permanent", ".jpg")
        imageStorage.commitResult = { permanentFile }
        val sut = AttachProductImageUseCase(repository, imageStorage)
        val temporaryFile = File.createTempFile("temp", ".jpg")
        val target = product(imagePath = null)

        val result = sut(target, temporaryFile)

        assertEquals(permanentFile.absolutePath, result.imagePath)
        assertEquals(listOf(target.copy(imagePath = permanentFile.absolutePath)), repository.updated)
        assertTrue(imageStorage.deleted.isEmpty())
    }

    @Test
    fun `invoke on a product with an existing image replaces it and deletes the previous file`() = runTest {
        val repository = FakeProductRepository()
        val imageStorage = FakeProductImageStorage()
        val newPermanentFile = File.createTempFile("new-permanent", ".jpg")
        imageStorage.commitResult = { newPermanentFile }
        val sut = AttachProductImageUseCase(repository, imageStorage)
        val temporaryFile = File.createTempFile("temp", ".jpg")
        val previousImagePath = "/data/product-images/old.jpg"
        val target = product(imagePath = previousImagePath)

        val result = sut(target, temporaryFile)

        assertEquals(newPermanentFile.absolutePath, result.imagePath)
        assertEquals(listOf(target.copy(imagePath = newPermanentFile.absolutePath)), repository.updated)
        assertEquals(listOf(File(previousImagePath)), imageStorage.deleted)
    }

    @Test
    fun `invoke deletes the temporary file and rethrows when commit fails, leaving product untouched`() = runTest {
        val repository = FakeProductRepository()
        val imageStorage = FakeProductImageStorage(commitError = RuntimeException("commit failed"))
        val sut = AttachProductImageUseCase(repository, imageStorage)
        val temporaryFile = File.createTempFile("temp", ".jpg")
        val target = product(imagePath = "/data/product-images/old.jpg")

        try {
            sut(target, temporaryFile)
            fail("commit 실패가 예외로 전달되어야 합니다")
        } catch (e: RuntimeException) {
            assertEquals("commit failed", e.message)
        }

        assertEquals(listOf(temporaryFile), imageStorage.deleted)
        assertTrue(repository.updated.isEmpty())
    }

    @Test
    fun `invoke deletes the committed file and rethrows when DB update fails, preserving the old image`() = runTest {
        val repository = FakeProductRepository(updateError = RuntimeException("update failed"))
        val imageStorage = FakeProductImageStorage()
        val newPermanentFile = File.createTempFile("new-permanent", ".jpg")
        imageStorage.commitResult = { newPermanentFile }
        val sut = AttachProductImageUseCase(repository, imageStorage)
        val temporaryFile = File.createTempFile("temp", ".jpg")
        val previousImagePath = "/data/product-images/old.jpg"
        val target = product(imagePath = previousImagePath)

        try {
            sut(target, temporaryFile)
            fail("DB update 실패가 예외로 전달되어야 합니다")
        } catch (e: RuntimeException) {
            assertEquals("update failed", e.message)
        }

        // 새로 commit된 파일만 정리 대상이고, 기존 이미지 경로는 삭제 목록에 없어야 한다.
        assertEquals(listOf(newPermanentFile), imageStorage.deleted)
        assertTrue(repository.updated.isEmpty())
    }

    @Test
    fun `invoke does not delete the previous image when the new path equals the old path`() = runTest {
        val repository = FakeProductRepository()
        val imageStorage = FakeProductImageStorage()
        val samePath = File.createTempFile("same", ".jpg")
        imageStorage.commitResult = { samePath }
        val sut = AttachProductImageUseCase(repository, imageStorage)
        val temporaryFile = File.createTempFile("temp", ".jpg")
        val target = product(imagePath = samePath.absolutePath)

        sut(target, temporaryFile)

        assertTrue(imageStorage.deleted.isEmpty())
    }

    @Test
    fun `invoke still returns the updated product when cleaning up the previous image fails`() = runTest {
        val repository = FakeProductRepository()
        val imageStorage = FakeProductImageStorage(deleteError = RuntimeException("cleanup failed"))
        val newPermanentFile = File.createTempFile("new-permanent", ".jpg")
        imageStorage.commitResult = { newPermanentFile }
        val sut = AttachProductImageUseCase(repository, imageStorage)
        val temporaryFile = File.createTempFile("temp", ".jpg")
        val target = product(imagePath = "/data/product-images/old.jpg")

        val result = sut(target, temporaryFile)

        // 이전 파일 정리가 실패해도 이미 성공한 DB update 결과는 되돌리지 않는다.
        assertEquals(newPermanentFile.absolutePath, result.imagePath)
        assertEquals(listOf(target.copy(imagePath = newPermanentFile.absolutePath)), repository.updated)
    }
}

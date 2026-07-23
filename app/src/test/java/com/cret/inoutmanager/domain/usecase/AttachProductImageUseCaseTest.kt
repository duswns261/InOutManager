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
        private val deleteResult: Boolean = true,
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
    fun `invoke on a product without an image commits and updates without deleting anything`() = runTest {
        val repository = FakeProductRepository()
        val imageStorage = FakeProductImageStorage()
        val permanentFile = File.createTempFile("permanent", ".jpg")
        imageStorage.commitResult = { permanentFile }
        val sut = AttachProductImageUseCase(repository, imageStorage)
        val temporaryFile = File.createTempFile("temp", ".jpg")
        val target = product(imagePath = null)

        val result = sut(target, temporaryFile)

        assertEquals(permanentFile.absolutePath, result.product.imagePath)
        assertTrue(result.cleanupSucceeded)
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

        assertEquals(newPermanentFile.absolutePath, result.product.imagePath)
        assertTrue(result.cleanupSucceeded)
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

        val result = sut(target, temporaryFile)

        assertTrue(imageStorage.deleted.isEmpty())
        assertTrue(result.cleanupSucceeded)
    }

    @Test
    fun `invoke still returns the updated product when cleaning up the previous image throws`() = runTest {
        val repository = FakeProductRepository()
        val imageStorage = FakeProductImageStorage(deleteError = RuntimeException("cleanup failed"))
        val newPermanentFile = File.createTempFile("new-permanent", ".jpg")
        imageStorage.commitResult = { newPermanentFile }
        val sut = AttachProductImageUseCase(repository, imageStorage)
        val temporaryFile = File.createTempFile("temp", ".jpg")
        val target = product(imagePath = "/data/product-images/old.jpg")

        val result = sut(target, temporaryFile)

        // 이전 파일 정리가 실패해도 이미 성공한 DB update 결과는 되돌리지 않되, cleanup 실패는 관찰 가능해야 한다.
        assertEquals(newPermanentFile.absolutePath, result.product.imagePath)
        assertEquals(listOf(target.copy(imagePath = newPermanentFile.absolutePath)), repository.updated)
        assertTrue(!result.cleanupSucceeded)
    }

    @Test
    fun `invoke reports cleanup failure when delete returns false without throwing`() = runTest {
        val repository = FakeProductRepository()
        val imageStorage = FakeProductImageStorage(deleteResult = false)
        val newPermanentFile = File.createTempFile("new-permanent", ".jpg")
        imageStorage.commitResult = { newPermanentFile }
        val sut = AttachProductImageUseCase(repository, imageStorage)
        val temporaryFile = File.createTempFile("temp", ".jpg")
        val target = product(imagePath = "/data/product-images/old.jpg")

        val result = sut(target, temporaryFile)

        // File.delete() == false는 예외를 던지지 않으므로 이 경로가 조용히 성공 처리되지 않는지 별도로 검증한다.
        assertEquals(newPermanentFile.absolutePath, result.product.imagePath)
        assertTrue(!result.cleanupSucceeded)
    }

    @Test
    fun `invoke preserves the original commit failure and suppresses the cleanup exception when temp file cleanup throws`() = runTest {
        val repository = FakeProductRepository()
        val imageStorage = FakeProductImageStorage(
            commitError = RuntimeException("commit failed"),
            deleteError = IllegalStateException("cleanup threw"),
        )
        val sut = AttachProductImageUseCase(repository, imageStorage)
        val temporaryFile = File.createTempFile("temp", ".jpg")
        val target = product(imagePath = "/data/product-images/old.jpg")

        try {
            sut(target, temporaryFile)
            fail("commit 실패가 예외로 전달되어야 합니다")
        } catch (e: RuntimeException) {
            // rollback cleanup이 예외를 던져도 원래 commit 실패가 그대로 전달되고, cleanup 예외는 덮어쓰지 않고 suppressed로 보존되어야 한다.
            assertEquals("commit failed", e.message)
            assertEquals(1, e.suppressed.size)
            assertEquals("cleanup threw", e.suppressed[0].message)
        }
    }

    @Test
    fun `invoke preserves the original commit failure and attaches a cleanup-failed suppressed exception when temp file delete returns false`() = runTest {
        val repository = FakeProductRepository()
        val imageStorage = FakeProductImageStorage(
            commitError = RuntimeException("commit failed"),
            deleteResult = false,
        )
        val sut = AttachProductImageUseCase(repository, imageStorage)
        val temporaryFile = File.createTempFile("temp", ".jpg")
        val target = product(imagePath = "/data/product-images/old.jpg")

        try {
            sut(target, temporaryFile)
            fail("commit 실패가 예외로 전달되어야 합니다")
        } catch (e: RuntimeException) {
            // delete() == false는 예외를 던지지 않지만, 그 실패도 원래 오류에 suppressed로 보존되어야 한다(조용히 사라지면 안 됨).
            assertEquals("commit failed", e.message)
            assertEquals(1, e.suppressed.size)
            assertTrue(e.suppressed[0] is ProductImageCleanupFailedException)
        }
    }

    @Test
    fun `invoke preserves the original DB update failure and suppresses the cleanup exception when committed file cleanup throws`() = runTest {
        val repository = FakeProductRepository(updateError = RuntimeException("update failed"))
        val imageStorage = FakeProductImageStorage(deleteError = IllegalStateException("cleanup threw"))
        val newPermanentFile = File.createTempFile("new-permanent", ".jpg")
        imageStorage.commitResult = { newPermanentFile }
        val sut = AttachProductImageUseCase(repository, imageStorage)
        val temporaryFile = File.createTempFile("temp", ".jpg")
        val target = product(imagePath = "/data/product-images/old.jpg")

        try {
            sut(target, temporaryFile)
            fail("DB update 실패가 예외로 전달되어야 합니다")
        } catch (e: RuntimeException) {
            assertEquals("update failed", e.message)
            assertEquals(1, e.suppressed.size)
            assertEquals("cleanup threw", e.suppressed[0].message)
        }
    }

    @Test
    fun `invoke preserves the original DB update failure and attaches a cleanup-failed suppressed exception when committed file delete returns false`() = runTest {
        val repository = FakeProductRepository(updateError = RuntimeException("update failed"))
        val imageStorage = FakeProductImageStorage(deleteResult = false)
        val newPermanentFile = File.createTempFile("new-permanent", ".jpg")
        imageStorage.commitResult = { newPermanentFile }
        val sut = AttachProductImageUseCase(repository, imageStorage)
        val temporaryFile = File.createTempFile("temp", ".jpg")
        val target = product(imagePath = "/data/product-images/old.jpg")

        try {
            sut(target, temporaryFile)
            fail("DB update 실패가 예외로 전달되어야 합니다")
        } catch (e: RuntimeException) {
            assertEquals("update failed", e.message)
            assertEquals(1, e.suppressed.size)
            assertTrue(e.suppressed[0] is ProductImageCleanupFailedException)
        }
    }
}

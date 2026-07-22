package com.cret.inoutmanager.domain.usecase

import com.cret.inoutmanager.domain.repository.ProductImageStorage
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class ImportProductImageUseCaseTest {

    private class FakeProductImageStorage(
        private val importError: Throwable? = null,
    ) : ProductImageStorage {
        val imported = mutableListOf<String>()
        var importResult: File = File.createTempFile("imported", ".jpg")

        override fun createTemporaryFile(): File = File.createTempFile("test", ".jpg")
        override fun commit(temporaryFile: File): File = temporaryFile

        override fun importTemporaryFile(input: InputStream): File {
            importError?.let { throw it }
            imported += input.readBytes().decodeToString()
            return importResult
        }

        override fun isUsableManagedImage(file: File): Boolean = file.exists()
        override fun delete(file: File) {}
    }

    private class CountingStream(private val bytes: ByteArray) : InputStream() {
        var closed = false
            private set
        private val delegate = ByteArrayInputStream(bytes)

        override fun read(): Int = delegate.read()
        override fun close() {
            closed = true
            super.close()
        }
    }

    @Test
    fun `invoke opens the stream, delegates to storage, and closes the stream`() = runTest {
        val imageStorage = FakeProductImageStorage()
        val sut = ImportProductImageUseCase(imageStorage)
        val stream = CountingStream("picked-bytes".toByteArray())

        val result = sut { stream }

        assertEquals(imageStorage.importResult, result)
        assertEquals(listOf("picked-bytes"), imageStorage.imported)
        assertTrue(stream.closed)
    }

    @Test
    fun `invoke propagates storage import failure`() = runTest {
        val imageStorage = FakeProductImageStorage(importError = IOException("decode failed"))
        val sut = ImportProductImageUseCase(imageStorage)

        try {
            sut { ByteArrayInputStream("bad".toByteArray()) }
            fail("import 실패가 예외로 전달되어야 합니다")
        } catch (e: IOException) {
            assertEquals("decode failed", e.message)
        }
    }

    @Test
    fun `invoke closes the stream even when storage import fails`() = runTest {
        val imageStorage = FakeProductImageStorage(importError = IOException("decode failed"))
        val sut = ImportProductImageUseCase(imageStorage)
        val stream = CountingStream("bad".toByteArray())

        try {
            sut { stream }
            fail("import 실패가 예외로 전달되어야 합니다")
        } catch (e: IOException) {
            // expected
        }

        assertTrue(stream.closed)
    }

    @Test
    fun `invoke propagates a failure to open the stream`() = runTest {
        val imageStorage = FakeProductImageStorage()
        val sut = ImportProductImageUseCase(imageStorage)

        try {
            sut { throw IOException("uri no longer valid") }
            fail("stream open 실패가 예외로 전달되어야 합니다")
        } catch (e: IOException) {
            assertEquals("uri no longer valid", e.message)
        }

        assertTrue(imageStorage.imported.isEmpty())
    }
}

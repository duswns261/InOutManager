package com.cret.inoutmanager.data.image

import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class FileProductImageStorageTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private class ThrowingInputStream : InputStream() {
        override fun read(): Int = throw IOException("stream read failed")
    }

    private fun storage(canDecodeImage: (File) -> Boolean = { true }): FileProductImageStorage {
        val cacheRoot = tempFolder.newFolder("cache")
        val filesRoot = tempFolder.newFolder("files")
        return FileProductImageStorage(cacheRoot, filesRoot, canDecodeImage)
    }

    @Test
    fun `createTemporaryFile returns a path under the cache product-images directory`() {
        val sut = storage()

        val temporaryFile = sut.createTemporaryFile()

        assertTrue(temporaryFile.parentFile!!.exists())
        assertTrue(temporaryFile.parentFile!!.name == "product-images")
    }

    @Test
    fun `createTemporaryFile returns a unique path on every call`() {
        val sut = storage()

        val first = sut.createTemporaryFile()
        val second = sut.createTemporaryFile()

        assertNotEquals(first, second)
    }

    @Test
    fun `commit moves the temporary file into the permanent directory and preserves content`() {
        val sut = storage()
        val temporaryFile = sut.createTemporaryFile()
        temporaryFile.writeText("fake-image-bytes")

        val committedFile = sut.commit(temporaryFile)

        assertFalse(temporaryFile.exists())
        assertTrue(committedFile.exists())
        assertEquals("fake-image-bytes", committedFile.readText())
    }

    @Test
    fun `delete removes a file inside the managed temporary directory and returns true`() {
        val sut = storage()
        val temporaryFile = sut.createTemporaryFile()
        temporaryFile.writeText("x")

        val result = sut.delete(temporaryFile)

        assertFalse(temporaryFile.exists())
        assertTrue(result)
    }

    @Test
    fun `delete removes a file inside the managed permanent directory and returns true`() {
        val sut = storage()
        val temporaryFile = sut.createTemporaryFile()
        temporaryFile.writeText("x")
        val committedFile = sut.commit(temporaryFile)

        val result = sut.delete(committedFile)

        assertFalse(committedFile.exists())
        assertTrue(result)
    }

    @Test
    fun `delete ignores a file outside the managed directories and returns true as a no-op`() {
        val sut = storage()
        val externalFile = tempFolder.newFile("outside-managed-dirs.jpg")
        externalFile.writeText("do-not-touch")

        val result = sut.delete(externalFile)

        assertTrue(externalFile.exists())
        assertTrue(result)
    }

    @Test
    fun `delete returns false without throwing when a managed file exists but cannot actually be deleted`() {
        val sut = storage()
        val temporaryFile = sut.createTemporaryFile()
        // 빈 파일이 아닌 디렉터리를 관리 경로에 만들어 File#delete()가 false를 반환하는 상황을 재현합니다.
        val undeletableDir = File(temporaryFile.parentFile, "undeletable-dir").apply { mkdirs() }
        File(undeletableDir, "child.txt").writeText("blocks-delete")

        val result = sut.delete(undeletableDir)

        assertTrue("cleanup 실패를 성공처럼 숨기면 안 됩니다", !result)
        assertTrue(undeletableDir.exists())
    }

    @Test
    fun `constructing storage clears stale files left in the temporary directory`() {
        val cacheRoot = tempFolder.newFolder("cache")
        val filesRoot = tempFolder.newFolder("files")
        val staleDir = File(cacheRoot, "product-images").apply { mkdirs() }
        val staleFile = File(staleDir, "leftover-from-killed-process.jpg").apply { writeText("stale") }

        FileProductImageStorage(cacheRoot, filesRoot)

        assertFalse(staleFile.exists())
    }

    @Test
    fun `constructing storage does not touch files already committed to the permanent directory`() {
        val cacheRoot = tempFolder.newFolder("cache")
        val filesRoot = tempFolder.newFolder("files")
        val permanentDir = File(filesRoot, "product-images").apply { mkdirs() }
        val committedFile = File(permanentDir, "already-registered.jpg").apply { writeText("kept") }

        FileProductImageStorage(cacheRoot, filesRoot)

        assertTrue(committedFile.exists())
    }

    @Test
    fun `delete returns true as a no-op when the managed permanent file has already been lost`() {
        val sut = storage()
        val temporaryFile = sut.createTemporaryFile()
        temporaryFile.writeText("x")
        val committedFile = sut.commit(temporaryFile)
        committedFile.delete()
        assertFalse(committedFile.exists())

        val result = sut.delete(committedFile)

        assertFalse(committedFile.exists())
        assertTrue(result)
    }

    @Test
    fun `importTemporaryFile copies stream bytes into a new managed temporary file`() {
        val sut = storage(canDecodeImage = { true })

        val imported = sut.importTemporaryFile(ByteArrayInputStream("picked-image-bytes".toByteArray()))

        assertTrue(imported.exists())
        assertEquals("product-images", imported.parentFile!!.name)
        assertEquals("picked-image-bytes", imported.readText())
    }

    @Test
    fun `importTemporaryFile generates a unique file on every call`() {
        val sut = storage(canDecodeImage = { true })

        val first = sut.importTemporaryFile(ByteArrayInputStream("a".toByteArray()))
        val second = sut.importTemporaryFile(ByteArrayInputStream("b".toByteArray()))

        assertNotEquals(first, second)
    }

    @Test
    fun `importTemporaryFile deletes the partial file and rethrows when decode validation fails`() {
        val sut = storage(canDecodeImage = { false })

        try {
            sut.importTemporaryFile(ByteArrayInputStream("not-an-image".toByteArray()))
            fail("decode 실패가 예외로 전달되어야 합니다")
        } catch (e: IOException) {
            // expected
        }

        val leftoverFiles = tempFolder.root.walkTopDown().filter { it.isFile }.toList()
        assertTrue("decode 실패 후 부분 파일이 남아있으면 안 됩니다: $leftoverFiles", leftoverFiles.isEmpty())
    }

    @Test
    fun `importTemporaryFile deletes the partial file and rethrows when the stream fails`() {
        val sut = storage(canDecodeImage = { true })

        try {
            sut.importTemporaryFile(ThrowingInputStream())
            fail("stream 읽기 실패가 예외로 전달되어야 합니다")
        } catch (e: IOException) {
            // expected
        }

        val leftoverFiles = tempFolder.root.walkTopDown().filter { it.isFile }.toList()
        assertTrue("stream 실패 후 부분 파일이 남아있으면 안 됩니다: $leftoverFiles", leftoverFiles.isEmpty())
    }

    @Test
    fun `isUsableManagedImage returns true for a normal managed and decodable file`() {
        val sut = storage(canDecodeImage = { true })
        val temporaryFile = sut.createTemporaryFile()
        temporaryFile.writeText("x")

        assertTrue(sut.isUsableManagedImage(temporaryFile))
    }

    @Test
    fun `isUsableManagedImage returns false for a file outside managed directories`() {
        val sut = storage(canDecodeImage = { true })
        val externalFile = tempFolder.newFile("outside-managed-dirs.jpg")
        externalFile.writeText("x")

        assertFalse(sut.isUsableManagedImage(externalFile))
    }

    @Test
    fun `isUsableManagedImage returns false for a managed path that no longer exists`() {
        val sut = storage(canDecodeImage = { true })
        val temporaryFile = sut.createTemporaryFile()
        // 파일을 실제로 쓰지 않아 관리 경로에는 있지만 존재하지 않는 상태를 재현합니다.

        assertFalse(sut.isUsableManagedImage(temporaryFile))
    }

    @Test
    fun `isUsableManagedImage returns false when the managed file cannot be decoded`() {
        val sut = storage(canDecodeImage = { false })
        val temporaryFile = sut.createTemporaryFile()
        temporaryFile.writeText("corrupted")

        assertFalse(sut.isUsableManagedImage(temporaryFile))
    }
}

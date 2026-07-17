package com.cret.inoutmanager.data.image

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class FileProductImageStorageTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private fun storage(): FileProductImageStorage {
        val cacheRoot = tempFolder.newFolder("cache")
        val filesRoot = tempFolder.newFolder("files")
        return FileProductImageStorage(cacheRoot, filesRoot)
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
    fun `delete removes a file inside the managed temporary directory`() {
        val sut = storage()
        val temporaryFile = sut.createTemporaryFile()
        temporaryFile.writeText("x")

        sut.delete(temporaryFile)

        assertFalse(temporaryFile.exists())
    }

    @Test
    fun `delete removes a file inside the managed permanent directory`() {
        val sut = storage()
        val temporaryFile = sut.createTemporaryFile()
        temporaryFile.writeText("x")
        val committedFile = sut.commit(temporaryFile)

        sut.delete(committedFile)

        assertFalse(committedFile.exists())
    }

    @Test
    fun `delete ignores a file outside the managed directories`() {
        val sut = storage()
        val externalFile = tempFolder.newFile("outside-managed-dirs.jpg")
        externalFile.writeText("do-not-touch")

        sut.delete(externalFile)

        assertTrue(externalFile.exists())
    }
}

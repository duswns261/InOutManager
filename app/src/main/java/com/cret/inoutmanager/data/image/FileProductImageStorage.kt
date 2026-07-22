package com.cret.inoutmanager.data.image

import android.graphics.BitmapFactory
import com.cret.inoutmanager.domain.repository.ProductImageStorage
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.UUID

/**
 * [ProductImageStorage]를 앱 전용 파일시스템으로 구현합니다.
 * 임시 파일은 [cacheRoot] 하위 전용 디렉터리, 영구 파일은 [filesRoot] 하위 전용 디렉터리에 둡니다.
 * 파일명은 사용자 입력을 포함하지 않는 UUID 기반 값을 사용합니다.
 *
 * [canDecodeImage]는 실제 decode 가능 여부 판정을 담당하며, 기본값은 [BitmapFactory]를 사용합니다.
 * unit test에서는 Android framework 없이 검증할 수 있도록 이 판정을 대체할 수 있습니다.
 */
class FileProductImageStorage(
    cacheRoot: File,
    filesRoot: File,
    private val canDecodeImage: (File) -> Boolean = ::decodesAsBitmap,
) : ProductImageStorage {

    private val temporaryDir: File = File(cacheRoot, IMAGE_DIR_NAME)
    private val permanentDir: File = File(filesRoot, IMAGE_DIR_NAME)

    init {
        clearStaleTemporaryFiles()
    }

    /**
     * 이전 프로세스가 강제 종료돼 확정되지 못한 임시 파일이 남아있을 수 있어
     * 이 storage가 프로세스당 한 번 구성될 때만 정리합니다. 이후 생성되는 임시 파일은
     * 이 시점 뒤에 만들어지므로 진행 중인 촬영 흐름의 파일을 건드리지 않습니다.
     * filesDir의 확정 파일은 대상에서 제외합니다.
     */
    private fun clearStaleTemporaryFiles() {
        temporaryDir.listFiles()?.forEach { it.delete() }
    }

    override fun createTemporaryFile(): File {
        temporaryDir.mkdirs()
        return File(temporaryDir, "${UUID.randomUUID()}.jpg")
    }

    override fun commit(temporaryFile: File): File {
        permanentDir.mkdirs()
        val permanentFile = File(permanentDir, "${UUID.randomUUID()}.jpg")

        if (temporaryFile.renameTo(permanentFile)) {
            return permanentFile
        }

        // 임시 파일과 영구 디렉터리가 서로 다른 파일시스템/마운트에 있으면 rename이 실패할 수 있어
        // copy 후 원본을 정리하는 방식으로 대체합니다.
        return try {
            temporaryFile.copyTo(permanentFile, overwrite = true)
            temporaryFile.delete()
            permanentFile
        } catch (e: Exception) {
            permanentFile.delete()
            throw e
        }
    }

    override fun importTemporaryFile(input: InputStream): File {
        temporaryDir.mkdirs()
        val target = File(temporaryDir, "${UUID.randomUUID()}.jpg")
        try {
            target.outputStream().use { output -> input.copyTo(output) }
            if (target.length() == 0L || !canDecodeImage(target)) {
                throw IOException("Selected content could not be decoded as an image")
            }
            return target
        } catch (e: Exception) {
            target.delete()
            throw e
        }
    }

    override fun isUsableManagedImage(file: File): Boolean =
        isManaged(file) && file.exists() && canDecodeImage(file)

    override fun delete(file: File) {
        if (isManaged(file)) {
            file.delete()
        }
    }

    private fun isManaged(file: File): Boolean {
        val canonicalPath = runCatching { file.canonicalPath }.getOrNull() ?: return false
        return canonicalPath.startsWith(temporaryDir.canonicalPathWithSeparator()) ||
            canonicalPath.startsWith(permanentDir.canonicalPathWithSeparator())
    }

    private fun File.canonicalPathWithSeparator(): String {
        val canonical = runCatching { canonicalPath }.getOrDefault(path)
        return if (canonical.endsWith(File.separator)) canonical else canonical + File.separator
    }

    companion object {
        private const val IMAGE_DIR_NAME = "product-images"
    }
}

private fun decodesAsBitmap(file: File): Boolean {
    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(file.absolutePath, options)
    return options.outWidth > 0 && options.outHeight > 0
}

package com.cret.inoutmanager.domain.usecase

import com.cret.inoutmanager.domain.repository.ProductImageStorage
import java.io.File
import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * UI가 제공한 일회성 `() -> InputStream` 공급자를 IO dispatcher에서 열고 닫으며
 * [ProductImageStorage]를 통해 관리 대상 임시 파일로 가져옵니다.
 * Android `Uri`/`ContentResolver` 타입을 Domain에 노출하지 않습니다.
 */
class ImportProductImageUseCase(
    private val imageStorage: ProductImageStorage,
) {
    suspend operator fun invoke(openStream: () -> InputStream): File = withContext(Dispatchers.IO) {
        openStream().use { input -> imageStorage.importTemporaryFile(input) }
    }
}

package com.cret.inoutmanager.domain.usecase

import com.cret.inoutmanager.domain.repository.ProductImageStorage
import java.io.File

/**
 * UI가 CameraX 촬영 출력 대상으로 사용할 임시 이미지 파일 경로를 생성합니다.
 */
class CreateTemporaryProductImageUseCase(
    private val imageStorage: ProductImageStorage,
) {
    operator fun invoke(): File = imageStorage.createTemporaryFile()
}

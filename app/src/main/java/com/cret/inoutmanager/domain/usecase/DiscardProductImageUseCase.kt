package com.cret.inoutmanager.domain.usecase

import com.cret.inoutmanager.domain.repository.ProductImageStorage
import java.io.File

/**
 * 취소, 재촬영, 촬영 실패 시 더 이상 필요하지 않은 관리 대상 이미지 파일을 삭제합니다.
 */
class DiscardProductImageUseCase(
    private val imageStorage: ProductImageStorage,
) {
    operator fun invoke(file: File) {
        imageStorage.delete(file)
    }
}

package com.cret.inoutmanager.domain.usecase

import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.domain.repository.ProductImageStorage
import com.cret.inoutmanager.domain.repository.ProductRepository
import java.io.File

/**
 * 제품을 삭제하는 UseCase입니다.
 * DB 삭제가 성공한 뒤에만 연결된 관리 대상 이미지 파일을 정리하고,
 * DB 삭제가 실패하면 이미지 파일을 그대로 유지합니다.
 */
class DeleteProductUseCase(
    private val repository: ProductRepository,
    private val imageStorage: ProductImageStorage,
) {
    suspend operator fun invoke(product: Product) {
        repository.delete(product)
        product.imagePath
            ?.takeIf { it.isNotBlank() }
            ?.let { imageStorage.delete(File(it)) }
    }
}

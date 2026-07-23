package com.cret.inoutmanager.domain.usecase

import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.domain.repository.ProductImageStorage
import com.cret.inoutmanager.domain.repository.ProductRepository
import java.io.File

/**
 * 기존 제품의 이미지를 독립적으로 제거해 이미지 없는 상태로 되돌립니다.
 * `product.copy(imagePath = null)`을 먼저 저장하고, DB update가 성공한 뒤에만 이전 관리
 * 이미지를 삭제합니다. update가 실패하면 기존 Product/이미지를 그대로 둡니다.
 */
class RemoveProductImageUseCase(
    private val repository: ProductRepository,
    private val imageStorage: ProductImageStorage,
) {
    suspend operator fun invoke(product: Product): ProductImageMutationResult {
        val previousImagePath = product.imagePath
        if (previousImagePath.isNullOrBlank()) {
            return ProductImageMutationResult(product, cleanupSucceeded = true)
        }

        val updatedProduct = product.copy(imagePath = null)
        repository.update(updatedProduct)

        val cleanupSucceeded = runCatching { imageStorage.delete(File(previousImagePath)) }.getOrDefault(false)

        return ProductImageMutationResult(updatedProduct, cleanupSucceeded)
    }
}

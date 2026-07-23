package com.cret.inoutmanager.domain.usecase

import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.domain.repository.ProductImageStorage
import com.cret.inoutmanager.domain.repository.ProductRepository
import java.io.File

/**
 * 기존 제품에 새 이미지를 최초로 추가하거나 기존 이미지를 교체합니다.
 * 새 temp를 영구 저장소로 commit한 뒤 `product.copy(imagePath = ...)`를 저장해 추가와 교체를
 * 같은 계약으로 처리합니다. commit 또는 DB update가 실패하면 새로 생긴 파일만 정리하고
 * 기존 Product/기존 이미지는 그대로 둡니다.
 */
class AttachProductImageUseCase(
    private val repository: ProductRepository,
    private val imageStorage: ProductImageStorage,
) {
    suspend operator fun invoke(product: Product, temporaryImageFile: File): ProductImageMutationResult {
        val committedImageFile = try {
            imageStorage.commit(temporaryImageFile)
        } catch (e: Exception) {
            cleanUpAfterFailure(temporaryImageFile, e)
            throw e
        }

        val updatedProduct = product.copy(imagePath = committedImageFile.absolutePath)
        try {
            repository.update(updatedProduct)
        } catch (e: Exception) {
            cleanUpAfterFailure(committedImageFile, e)
            throw e
        }

        val cleanupSucceeded = cleanUpPreviousImage(previousImagePath = product.imagePath, newImageFile = committedImageFile)

        return ProductImageMutationResult(updatedProduct, cleanupSucceeded)
    }

    /**
     * DB update가 성공한 뒤에만 호출됩니다. 이전 이미지 정리가 실패해도 이미 성공한 DB 상태를
     * 되돌리지 않도록 예외를 흡수하되, 실패 여부는 반환값으로 그대로 관찰할 수 있게 합니다.
     * 정리에 실패하면 고아 파일이 남을 수 있지만 제품 데이터는 영향을 받지 않습니다.
     */
    private fun cleanUpPreviousImage(previousImagePath: String?, newImageFile: File): Boolean {
        if (previousImagePath.isNullOrBlank() || previousImagePath == newImageFile.absolutePath) return true
        return runCatching { imageStorage.delete(File(previousImagePath)) }.getOrDefault(false)
    }

    /**
     * commit 또는 DB update 실패 후 새로 생긴 [file]을 정리합니다. 정리 자체가 실패해도(예외 또는
     * `delete() == false`) [originalError]를 덮어쓰거나 가리지 않고, `addSuppressed`로 함께
     * 보존해 원래 실패는 그대로 propagate되면서 cleanup 실패도 관찰할 수 있게 합니다.
     */
    private fun cleanUpAfterFailure(file: File, originalError: Exception) {
        runCatching { imageStorage.delete(file) }
            .onSuccess { deleted -> if (!deleted) originalError.addSuppressed(ProductImageCleanupFailedException(file)) }
            .onFailure { cleanupError -> originalError.addSuppressed(cleanupError) }
    }
}

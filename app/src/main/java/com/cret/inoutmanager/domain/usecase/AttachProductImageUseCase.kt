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
    suspend operator fun invoke(product: Product, temporaryImageFile: File): Product {
        val committedImageFile = try {
            imageStorage.commit(temporaryImageFile)
        } catch (e: Exception) {
            imageStorage.delete(temporaryImageFile)
            throw e
        }

        val updatedProduct = product.copy(imagePath = committedImageFile.absolutePath)
        try {
            repository.update(updatedProduct)
        } catch (e: Exception) {
            imageStorage.delete(committedImageFile)
            throw e
        }

        cleanUpPreviousImage(previousImagePath = product.imagePath, newImageFile = committedImageFile)

        return updatedProduct
    }

    /**
     * DB update가 성공한 뒤에만 호출됩니다. 이전 이미지 정리가 실패해도 이미 성공한 DB 상태를
     * 되돌리지 않도록 예외를 흡수합니다. 정리에 실패하면 고아 파일이 남을 수 있지만 제품 데이터는
     * 영향을 받지 않습니다.
     */
    private fun cleanUpPreviousImage(previousImagePath: String?, newImageFile: File) {
        if (previousImagePath.isNullOrBlank() || previousImagePath == newImageFile.absolutePath) return
        runCatching { imageStorage.delete(File(previousImagePath)) }
    }
}

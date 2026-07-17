package com.cret.inoutmanager.domain.usecase

import com.cret.inoutmanager.domain.model.Product
import com.cret.inoutmanager.domain.repository.ProductImageStorage
import com.cret.inoutmanager.domain.repository.ProductRepository
import java.io.File

/**
 * 새로운 제품을 등록하는 UseCase입니다.
 * 임시 이미지가 있으면 영구 저장소로 확정한 뒤 제품과 함께 저장하고,
 * DB 저장에 실패하면 확정된 파일을 정리해 연결되지 않은 파일이 남지 않게 합니다.
 */
class AddProductUseCase(
    private val repository: ProductRepository,
    private val imageStorage: ProductImageStorage,
) {
    suspend operator fun invoke(
        name: String,
        location: String,
        quantity: Int,
        temporaryImageFile: File? = null,
    ) {
        val committedImageFile = temporaryImageFile?.let { imageStorage.commit(it) }

        val product = Product(
            name = name,
            location = location,
            quantity = quantity.coerceAtLeast(0),
            imagePath = committedImageFile?.absolutePath,
        )

        try {
            repository.insert(product)
        } catch (e: Exception) {
            committedImageFile?.let { imageStorage.delete(it) }
            throw e
        }
    }
}

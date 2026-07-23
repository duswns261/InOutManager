package com.cret.inoutmanager.domain.usecase

/**
 * 모든 제품 관련 UseCase들을 하나로 묶어주는 래퍼 데이터 클래스입니다.
 * ViewModel에서 여러 UseCase를 주입받을 때 생성자가 비대해지는 것을 방지합니다.
 */
data class ProductUseCases(
    val getProducts: GetProductsUseCase,
    val addProduct: AddProductUseCase,
    val decreaseProductQuantity: DecreaseProductQuantityUseCase,
    val deleteProduct: DeleteProductUseCase,
    val createTemporaryProductImage: CreateTemporaryProductImageUseCase,
    val discardProductImage: DiscardProductImageUseCase,
    val importProductImage: ImportProductImageUseCase,
    val attachProductImage: AttachProductImageUseCase,
    val removeProductImage: RemoveProductImageUseCase,
)

package com.cret.inoutmanager.di

import com.cret.inoutmanager.domain.repository.ProductImageStorage
import com.cret.inoutmanager.domain.repository.ProductRepository
import com.cret.inoutmanager.domain.usecase.AddProductUseCase
import com.cret.inoutmanager.domain.usecase.AttachProductImageUseCase
import com.cret.inoutmanager.domain.usecase.CreateTemporaryProductImageUseCase
import com.cret.inoutmanager.domain.usecase.DecreaseProductQuantityUseCase
import com.cret.inoutmanager.domain.usecase.DeleteProductUseCase
import com.cret.inoutmanager.domain.usecase.DiscardProductImageUseCase
import com.cret.inoutmanager.domain.usecase.GetProductsUseCase
import com.cret.inoutmanager.domain.usecase.ImportProductImageUseCase
import com.cret.inoutmanager.domain.usecase.ProductUseCases
import com.cret.inoutmanager.domain.usecase.RemoveProductImageUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideProductUseCases(
        repository: ProductRepository,
        imageStorage: ProductImageStorage,
    ): ProductUseCases =
        ProductUseCases(
            getProducts = GetProductsUseCase(repository),
            addProduct = AddProductUseCase(repository, imageStorage),
            decreaseProductQuantity = DecreaseProductQuantityUseCase(repository),
            deleteProduct = DeleteProductUseCase(repository, imageStorage),
            createTemporaryProductImage = CreateTemporaryProductImageUseCase(imageStorage),
            discardProductImage = DiscardProductImageUseCase(imageStorage),
            importProductImage = ImportProductImageUseCase(imageStorage),
            attachProductImage = AttachProductImageUseCase(repository, imageStorage),
            removeProductImage = RemoveProductImageUseCase(repository, imageStorage),
        )
}

package com.cret.inoutmanager.di

import com.cret.inoutmanager.domain.repository.ProductRepository
import com.cret.inoutmanager.domain.usecase.AddProductUseCase
import com.cret.inoutmanager.domain.usecase.DecreaseProductQuantityUseCase
import com.cret.inoutmanager.domain.usecase.DeleteProductUseCase
import com.cret.inoutmanager.domain.usecase.GetProductsUseCase
import com.cret.inoutmanager.domain.usecase.ProductUseCases
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
    fun provideProductUseCases(repository: ProductRepository): ProductUseCases =
        ProductUseCases(
            getProducts = GetProductsUseCase(repository),
            addProduct = AddProductUseCase(repository),
            decreaseProductQuantity = DecreaseProductQuantityUseCase(repository),
            deleteProduct = DeleteProductUseCase(repository)
        )
}

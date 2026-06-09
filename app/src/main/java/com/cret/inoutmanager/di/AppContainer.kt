package com.cret.inoutmanager.di

import android.content.Context
import com.cret.inoutmanager.data.database.AppDatabase
import com.cret.inoutmanager.data.datasource.local.ProductLocalDataSource
import com.cret.inoutmanager.data.datasource.local.RoomProductLocalDataSource
import com.cret.inoutmanager.data.repository.DefaultProductRepository
import com.cret.inoutmanager.domain.repository.ProductRepository
import com.cret.inoutmanager.domain.usecase.*

/**
 * 앱에서 필요한 의존성을 한곳에서 제공하기 위한 수동 DI 진입점입니다.
 */
interface AppContainer {
    val productRepository: ProductRepository
    val productUseCases: ProductUseCases
}

/**
 * Room DB, DataSource, Repository 및 UseCase 생성을 Activity/Composable 바깥으로 분리합니다.
 */
class DefaultAppContainer(private val context: Context) : AppContainer {
    private val database by lazy {
        AppDatabase.getDatabase(context)
    }

    private val productLocalDataSource: ProductLocalDataSource by lazy {
        RoomProductLocalDataSource(database.productDao())
    }

    override val productRepository: ProductRepository by lazy {
        DefaultProductRepository(productLocalDataSource)
    }

    override val productUseCases: ProductUseCases by lazy {
        ProductUseCases(
            getProducts = GetProductsUseCase(productRepository),
            addProduct = AddProductUseCase(productRepository),
            decreaseProductQuantity = DecreaseProductQuantityUseCase(productRepository),
            deleteProduct = DeleteProductUseCase(productRepository)
        )
    }
}

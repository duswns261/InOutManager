package com.cret.inoutmanager.di

import android.content.Context
import com.cret.inoutmanager.data.database.AppDatabase
import com.cret.inoutmanager.data.repository.DefaultProductRepository
import com.cret.inoutmanager.domain.repository.ProductRepository

/**
 * 앱에서 필요한 의존성을 한곳에서 제공하기 위한 수동 DI 진입점입니다.
 */
interface AppContainer {
    val productRepository: ProductRepository
}

/**
 * Room DB와 Repository 생성을 Activity/Composable 바깥으로 분리합니다.
 */
class DefaultAppContainer(private val context: Context) : AppContainer {
    private val database by lazy {
        AppDatabase.getDatabase(context)
    }

    override val productRepository: ProductRepository by lazy {
        DefaultProductRepository(database.productDao())
    }
}

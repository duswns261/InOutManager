package com.cret.inoutmanager.di

import android.content.Context
import com.cret.inoutmanager.data.database.AppDatabase
import com.cret.inoutmanager.data.repository.ProductRepository

interface AppContainer {
    val productRepository: ProductRepository
}
// 질문, 여기서 AppContainer 인터페이스를 둔 것은 DefaultAppContainer 클래스의 확장성을 고려한 것 같은데 다른 예시는?
class DefaultAppContainer(private val context: Context) : AppContainer {
    private val database by lazy {
        AppDatabase.getDatabase(context)
    }

    override val productRepository: ProductRepository by lazy {
        ProductRepository(database.productDao())
    }
}

// AppContainer는 앱에서 공용으로 쓸 객체들을 담아두는 보관함(의존성 보관함?)의 역할을 가짐.
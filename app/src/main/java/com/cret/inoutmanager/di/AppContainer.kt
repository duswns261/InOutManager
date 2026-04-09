package com.cret.inoutmanager.di

import android.content.Context
import com.cret.inoutmanager.data.database.AppDatabase
import com.cret.inoutmanager.data.repository.ProductRepository

interface AppContainer {
    val productRepository: ProductRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val database by lazy {
        AppDatabase.getDatabase(context)
    }

    override val productRepository: ProductRepository by lazy {
        ProductRepository(database.productDao())
    }
}
package com.cret.inoutmanager.di

import android.content.Context
import com.cret.inoutmanager.data.dao.ProductDao
import com.cret.inoutmanager.data.datasource.local.ProductLocalDataSource
import com.cret.inoutmanager.data.datasource.local.RoomProductLocalDataSource
import com.cret.inoutmanager.data.image.FileProductImageStorage
import com.cret.inoutmanager.data.repository.DefaultProductRepository
import com.cret.inoutmanager.domain.repository.ProductImageStorage
import com.cret.inoutmanager.domain.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideProductLocalDataSource(productDao: ProductDao): ProductLocalDataSource =
        RoomProductLocalDataSource(productDao)

    @Provides
    @Singleton
    fun provideProductRepository(localDataSource: ProductLocalDataSource): ProductRepository =
        DefaultProductRepository(localDataSource)

    @Provides
    @Singleton
    fun provideProductImageStorage(@ApplicationContext context: Context): ProductImageStorage =
        FileProductImageStorage(cacheRoot = context.cacheDir, filesRoot = context.filesDir)
}

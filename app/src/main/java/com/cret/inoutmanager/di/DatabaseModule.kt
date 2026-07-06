package com.cret.inoutmanager.di

import android.content.Context
import androidx.room.Room
import com.cret.inoutmanager.data.dao.ProductDao
import com.cret.inoutmanager.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getDatabase(context)

    @Provides
    fun provideProductDao(database: AppDatabase): ProductDao =
        database.productDao()
}

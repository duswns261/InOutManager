package com.cret.inoutmanager.di

import com.cret.inoutmanager.reporting.ProductPhotoCaptureReporter
import com.cret.inoutmanager.reporting.firebase.FirebaseProductPhotoCaptureReporter
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReportingModule {

    @Provides
    @Singleton
    fun provideFirebaseCrashlytics(): FirebaseCrashlytics = FirebaseCrashlytics.getInstance()

    @Provides
    @Singleton
    fun provideProductPhotoCaptureReporter(
        crashlytics: FirebaseCrashlytics,
    ): ProductPhotoCaptureReporter = FirebaseProductPhotoCaptureReporter(crashlytics)
}

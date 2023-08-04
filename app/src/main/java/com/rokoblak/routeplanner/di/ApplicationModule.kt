package com.rokoblak.routeplanner.di

import com.rokoblak.routeplanner.data.service.AppNetworkMonitor
import com.rokoblak.routeplanner.data.service.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ApplicationModule {

    @Singleton
    @Binds
    abstract fun bindNetworkMonitor(impl: AppNetworkMonitor): NetworkMonitor
}

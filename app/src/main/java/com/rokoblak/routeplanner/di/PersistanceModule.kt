package com.rokoblak.routeplanner.di

import com.rokoblak.routeplanner.data.util.AppStorage
import com.rokoblak.routeplanner.data.util.PersistedStorage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PersistanceModule {

    @Singleton
    @Binds
    abstract fun bindPersistedStorage(impl: AppStorage): PersistedStorage
}

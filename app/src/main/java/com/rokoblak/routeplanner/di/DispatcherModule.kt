package com.rokoblak.routeplanner.di

import app.cash.molecule.RecompositionMode
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(SingletonComponent::class)
class DispatcherModule {
    @DefaultDispatcher
    @Provides
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @IoDispatcher
    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @MainDispatcher
    @Provides
    fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @AndroidUiDispatcherMainContext
    @Provides
    fun providesAndroidUiDispatcherMainContext(): CoroutineContext = androidx.compose.ui.platform.AndroidUiDispatcher.Main

    @Provides
    fun providesMoleculeRecompositionMode(): RecompositionMode = RecompositionMode.ContextClock

    @MainScopeWithSupervisor
    @Provides
    fun providesMainScopeWithSupervisor(
        @MainDispatcher mainDispatcher: CoroutineDispatcher
    ): CoroutineScope {
        return CoroutineScope(mainDispatcher + SupervisorJob())
    }
}

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DefaultDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainScopeWithSupervisor

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class AndroidUiDispatcherMainContext

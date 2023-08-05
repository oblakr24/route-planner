package com.rokoblak.routeplanner.di

import com.rokoblak.routeplanner.data.service.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ApplicationModule::class]
)
abstract class TestApplicationModule {

    @Singleton
    @Binds
    abstract fun bindNetworkMonitor(impl: FakeNetworkMonitor): NetworkMonitor
}

class FakeNetworkMonitor @Inject constructor() : NetworkMonitor {
    override val connected: Flow<Boolean> = flowOf(true)
}
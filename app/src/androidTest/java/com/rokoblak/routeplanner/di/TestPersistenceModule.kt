package com.rokoblak.routeplanner.di

import com.rokoblak.routeplanner.data.util.PersistedStorage
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [PersistenceModule::class]
)
abstract class TestPersistenceModule {

    @Singleton
    @Binds
    abstract fun bindPersistedStorage(impl: FakePersistedStorage): PersistedStorage
}

class FakePersistedStorage @Inject constructor() : PersistedStorage {

    private var darkModeFlow =
        MutableStateFlow(PersistedStorage.Prefs(null))

    override fun prefsFlow(): Flow<PersistedStorage.Prefs> = darkModeFlow

    override suspend fun updateDarkMode(enabled: Boolean?) {
        darkModeFlow.update {
            it.copy(darkMode = enabled)
        }
    }

    override suspend fun clear() {
        darkModeFlow.update {
            PersistedStorage.Prefs(null)
        }
    }
}
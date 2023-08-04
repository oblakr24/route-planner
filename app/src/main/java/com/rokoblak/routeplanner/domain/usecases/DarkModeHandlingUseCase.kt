package com.rokoblak.routeplanner.domain.usecases

import com.rokoblak.routeplanner.data.util.PersistedStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface DarkModeHandlingUseCase {
    fun darkModeEnabled(): Flow<Boolean?>

    suspend fun updateDarkMode(enabled: Boolean?)
}

class AppDarkModeHandlingUseCase @Inject constructor(
    private val storage: PersistedStorage,
) : DarkModeHandlingUseCase {
    override fun darkModeEnabled(): Flow<Boolean?> = storage.prefsFlow().map { it.darkMode }

    override suspend fun updateDarkMode(enabled: Boolean?) {
        storage.updateDarkMode(enabled)
    }
}
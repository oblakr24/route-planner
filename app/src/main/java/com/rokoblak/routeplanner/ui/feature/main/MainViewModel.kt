package com.rokoblak.routeplanner.ui.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rokoblak.routeplanner.domain.usecases.DarkModeHandlingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    darkModeUseCase: DarkModeHandlingUseCase,
) : ViewModel() {

    private val darkModeEnabledFlow = darkModeUseCase.darkModeEnabled().onStart { emit(null) }

    val uiState: StateFlow<MainScreenUIState> = darkModeEnabledFlow.map { darkModeEnabled ->
        createState(darkModeEnabled)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MainScreenUIState(null))

    private fun createState(
        darkModeEnabled: Boolean?,
    ): MainScreenUIState {
        return MainScreenUIState(
            isDarkTheme = darkModeEnabled,
        )
    }
}
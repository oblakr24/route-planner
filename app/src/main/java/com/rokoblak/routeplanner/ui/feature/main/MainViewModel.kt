package com.rokoblak.routeplanner.ui.feature.main

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.rokoblak.routeplanner.domain.usecases.DarkModeHandlingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val darkModeUseCase: DarkModeHandlingUseCase,
) : ViewModel() {

    private val scope = CoroutineScope(viewModelScope.coroutineContext + AndroidUiDispatcher.Main)

    val uiState: StateFlow<MainScreenUIState> by lazy {
        scope.launchMolecule(mode = RecompositionMode.ContextClock) {
            MainPresenter(darkModeUseCase.darkModeEnabled())
        }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    private fun MainPresenter(
        darkModeEnabledFlow: Flow<Boolean?>,
    ): MainScreenUIState {
        val darkModeEnabled = darkModeEnabledFlow.collectAsState(initial = null).value
        return MainScreenUIState(
            isDarkTheme = darkModeEnabled,
        )
    }
}
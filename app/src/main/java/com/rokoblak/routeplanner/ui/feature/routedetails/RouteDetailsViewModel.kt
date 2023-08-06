package com.rokoblak.routeplanner.ui.feature.routedetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rokoblak.routeplanner.data.repo.model.LoadableResult
import com.rokoblak.routeplanner.domain.model.ExpandedRouteDetails
import com.rokoblak.routeplanner.domain.usecases.RouteDetailsUseCase
import com.rokoblak.routeplanner.ui.feature.routedetails.composables.RouteScaffoldUIState
import com.rokoblak.routeplanner.ui.navigation.RouteNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val routeNavigator: RouteNavigator,
    private val useCase: RouteDetailsUseCase,
) : ViewModel(), RouteNavigator by routeNavigator {

    private val input = RouteDetailsRoute.getIdFrom(savedStateHandle)

    private val expandCollapseStates = MutableStateFlow(mapOf<String, Boolean>())

    val uiState: StateFlow<RouteScaffoldUIState> = combine(expandCollapseStates, useCase.loadResults(input.routeId)) { expandedState, state ->
        createScaffoldState(expandedState, state)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RouteScaffoldUIState(input.name))

    fun onAction(act: RouteDetailsAction) {
        when (act) {
            RouteDetailsAction.RetryClicked -> {
                viewModelScope.launch {
                    useCase.reload()
                }
            }

            is RouteDetailsAction.SectionExpandCollapseClicked -> {
                expandCollapseStates.update {
                    it.toMutableMap().apply {
                        val enabled = get(act.legId) ?: false
                        put(act.legId, !enabled)
                    }
                }
            }
        }
    }

    private fun createScaffoldState(
        expandCollapseFlags: Map<String, Boolean>,
        state: LoadableResult<ExpandedRouteDetails>
    ): RouteScaffoldUIState {
        return RouteScaffoldUIState(
            title = input.name,
            subtitle = RouteDetailsUIMapper.createScaffoldSubtitle(state),
            mainContent = RouteDetailsUIMapper.createScaffoldContent(state),
            sheetContent = RouteDetailsUIMapper.createLegsListing(state, expandCollapseFlags)
        )
    }
}

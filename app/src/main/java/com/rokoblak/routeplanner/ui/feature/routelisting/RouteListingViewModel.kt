package com.rokoblak.routeplanner.ui.feature.routelisting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rokoblak.routeplanner.data.repo.model.LoadErrorType
import com.rokoblak.routeplanner.data.repo.model.LoadableResult
import com.rokoblak.routeplanner.domain.model.Route
import com.rokoblak.routeplanner.domain.model.RoutesListing
import com.rokoblak.routeplanner.domain.usecases.DarkModeHandlingUseCase
import com.rokoblak.routeplanner.domain.usecases.RouteListingUseCase
import com.rokoblak.routeplanner.ui.feature.routedetails.RouteDetailsRoute
import com.rokoblak.routeplanner.ui.feature.routelisting.composables.RouteDisplayData
import com.rokoblak.routeplanner.ui.feature.routelisting.composables.RouteListingDrawerUIState
import com.rokoblak.routeplanner.ui.feature.routelisting.composables.RouteListingScaffoldUIState
import com.rokoblak.routeplanner.ui.feature.routelisting.composables.RoutesListingData
import com.rokoblak.routeplanner.ui.navigation.RouteNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RouteListingViewModel @Inject constructor(
    private val routeNavigator: RouteNavigator,
    private val listingUseCase: RouteListingUseCase,
    private val darkModeUseCase: DarkModeHandlingUseCase,
) : ViewModel(), RouteNavigator by routeNavigator {

    val uiState: StateFlow<RouteListingScaffoldUIState> = combine(listingUseCase.flow, darkModeUseCase.darkModeEnabled()) { listing, darkModeEnabled ->
        createState(darkModeEnabled, listing)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), initialValue = RouteListingScaffoldUIState())

    private fun createState(
        darkMode: Boolean?,
        listing: LoadableResult<RoutesListing>,
    ): RouteListingScaffoldUIState {
        val drawer = RouteListingDrawerUIState(
            darkMode = darkMode,
        )

        val content = when (listing) {
            is LoadableResult.Error -> RoutesListingData.Error(isNoConnection = listing.type == LoadErrorType.NoNetwork)
            LoadableResult.Loading -> RoutesListingData.Initial
            is LoadableResult.Success -> RoutesListingData.Loaded(
                items = listing.value.routes.map { it.toDisplay() }.toImmutableList(),
                showLoadingAtEnd = listing.value.loadingMore,
            )
        }

        return RouteListingScaffoldUIState(
            drawer = drawer,
            innerContent = content,
        )
    }

    fun onAction(act: RouteListingAction) {
        viewModelScope.launch {
            when (act) {
                RouteListingAction.NextPageTriggerReached -> listingUseCase.loadNext()
                RouteListingAction.RefreshTriggered -> listingUseCase.reload()
                is RouteListingAction.SetDarkMode -> setDarkMode(act.enabled)
                is RouteListingAction.OpenRoute -> navigateToRoute(RouteDetailsRoute.get(act.input))
            }
        }
    }

    private fun setDarkMode(enabled: Boolean?) = viewModelScope.launch {
        darkModeUseCase.updateDarkMode(enabled)
    }

    private fun Route.toDisplay() = RouteDisplayData(
        itemId = itemId,
        routeId = routeId,
        name = name,
    )
}
package com.rokoblak.routeplanner.ui.feature.routelisting.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rokoblak.routeplanner.ui.common.AppThemePreviews
import com.rokoblak.routeplanner.ui.common.PreviewDataUtils
import com.rokoblak.routeplanner.ui.feature.routelisting.RouteListingAction
import com.rokoblak.routeplanner.ui.theme.RoutePlannerTheme
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


data class RouteListingScaffoldUIState(
    val drawer: RouteListingDrawerUIState = RouteListingDrawerUIState(null),
    val innerContent: RoutesListingData = RoutesListingData.Initial,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteListingScaffold(
    state: RouteListingScaffoldUIState,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    onAction: (RouteListingAction) -> Unit,
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                RouteListingDrawer(state.drawer) {
                    onAction(it)
                }
            }

        }
    ) {
        ListingScaffoldContent(
            state = state.innerContent,
            drawerState = drawerState,
            onAction = onAction
        )
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ListingScaffoldContent(
    state: RoutesListingData,
    drawerState: DrawerState,
    onAction: (RouteListingAction) -> Unit,
) {
    Scaffold(
        topBar = {
            val coroutineScope = rememberCoroutineScope()
            RouteListingTopAppbar {
                coroutineScope.launch {
                    drawerState.open()
                }
            }
        }
    ) { paddingValues ->
        val topPadding = paddingValues.calculateTopPadding()

        val refreshScope = rememberCoroutineScope()

        var refreshing by remember { mutableStateOf(false) }

        val pullRefreshState = rememberPullRefreshState(refreshing, {
            refreshScope.launch {
                refreshing = true
                onAction(RouteListingAction.RefreshTriggered)
                delay(500)
                refreshing = false
            }
        })

        Box(
            Modifier
                .padding(top = topPadding)
                .pullRefresh(pullRefreshState)) {
            RoutesListing(state) { action ->
                onAction(action)
            }
            PullRefreshIndicator(
                refreshing,
                pullRefreshState,
                Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@AppThemePreviews
@Composable
private fun ListingScaffoldMaterialPreview() {
    val drawerState = RouteListingDrawerUIState(
        darkMode = true,
    )
    val state = RouteListingScaffoldUIState(
        drawerState,
        innerContent = RoutesListingData.Loaded(
            items = PreviewDataUtils.routes.toImmutableList(),
            showLoadingAtEnd = true
        )
    )
    RoutePlannerTheme {
        RouteListingScaffold(
            state = state,
            drawerState = DrawerState(DrawerValue.Closed),
            onAction = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@AppThemePreviews
@Composable
private fun ListingScaffoldMaterialWithDrawerPreview() {
    val drawerState = RouteListingDrawerUIState(
        darkMode = true,
    )
    val state = RouteListingScaffoldUIState(
        drawerState,
        innerContent = RoutesListingData.Initial
    )
    RoutePlannerTheme {
        RouteListingScaffold(
            state = state,
            drawerState = DrawerState(DrawerValue.Open),
            onAction = {})
    }
}

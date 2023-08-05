package com.rokoblak.routeplanner.ui.feature.routedetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.rokoblak.routeplanner.ui.common.composables.DetailsContent
import com.rokoblak.routeplanner.ui.feature.routedetails.composables.RouteContentUIState
import com.rokoblak.routeplanner.ui.feature.routedetails.composables.RouteDetailsContent


data class RouteDetailsUIState(
    val title: String,
    val inner: RouteContentUIState = RouteContentUIState.Loading,
)

@Composable
fun RouteDetailsScreen(viewModel: RouteDetailsViewModel = hiltViewModel()) {
    val state = viewModel.uiState.collectAsState().value

    DetailsContent(title = state.title, onBackPressed = {
        viewModel.navigateUp()
    }) {
        RouteDetailsContent(state = state.inner, onAction = { act ->
            viewModel.onAction(act)
        })
    }
}

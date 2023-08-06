package com.rokoblak.routeplanner.ui.feature.routedetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.rokoblak.routeplanner.ui.feature.routedetails.composables.RouteDetailsScaffold


@Composable
fun RouteDetailsScreen(viewModel: RouteDetailsViewModel = hiltViewModel()) {
    val state = viewModel.uiState.collectAsState().value

    RouteDetailsScaffold(state = state, onBackClicked = {
        viewModel.navigateUp()
    }, onAction = { act ->
        viewModel.onAction(act)
    })
}


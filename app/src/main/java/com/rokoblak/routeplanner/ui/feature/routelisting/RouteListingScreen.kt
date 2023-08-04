package com.rokoblak.routeplanner.ui.feature.routelisting

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.rokoblak.routeplanner.ui.feature.routelisting.composables.RouteListingScaffold


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteListingScreen(viewModel: RouteListingViewModel = hiltViewModel()) {
    val state = viewModel.uiState.collectAsState().value
    RouteListingScaffold(state = state, onAction = {
        viewModel.onAction(it)
    })
}

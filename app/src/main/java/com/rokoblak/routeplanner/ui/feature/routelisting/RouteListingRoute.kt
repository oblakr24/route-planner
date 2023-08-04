package com.rokoblak.routeplanner.ui.feature.routelisting

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.rokoblak.routeplanner.ui.navigation.NavRoute

object RouteListingRoute : NavRoute<RouteListingViewModel> {

    override val route = "routes/"

    @Composable
    override fun viewModel(): RouteListingViewModel = hiltViewModel()

    @Composable
    override fun Content(viewModel: RouteListingViewModel) = RouteListingScreen(viewModel)
}
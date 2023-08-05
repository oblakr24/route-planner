package com.rokoblak.routeplanner.ui.feature.routedetails

sealed interface RouteDetailsAction {
    data object RetryClicked : RouteDetailsAction
    data class SectionExpandCollapseClicked(val legId: String): RouteDetailsAction
}

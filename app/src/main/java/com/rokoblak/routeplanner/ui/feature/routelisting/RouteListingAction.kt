package com.rokoblak.routeplanner.ui.feature.routelisting

import com.rokoblak.routeplanner.ui.feature.routedetails.RouteDetailsRoute


sealed interface RouteListingAction {
    data object RefreshTriggered : RouteListingAction
    data object NextPageTriggerReached : RouteListingAction
    data class OpenRoute(val input: RouteDetailsRoute.Input): RouteListingAction
    data class SetDarkMode(val enabled: Boolean?) : RouteListingAction
}

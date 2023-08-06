package com.rokoblak.routeplanner.ui.feature.routedetails.composables

import androidx.compose.runtime.Composable
import com.rokoblak.routeplanner.R
import com.rokoblak.routeplanner.ui.common.AppThemePreviews
import com.rokoblak.routeplanner.ui.common.PreviewDataUtils
import com.rokoblak.routeplanner.ui.common.TextRes
import com.rokoblak.routeplanner.ui.common.composables.ErrorCell
import com.rokoblak.routeplanner.ui.common.composables.LoadingCell
import com.rokoblak.routeplanner.ui.feature.routedetails.RouteDetailsAction
import com.rokoblak.routeplanner.ui.theme.RoutePlannerTheme

sealed interface RouteContentUIState {
    data object Loading : RouteContentUIState
    data class Error(val isNoConnection: Boolean) : RouteContentUIState
    data class Loaded(
        val header: RouteHeaderDisplayData,
        val listingData: RouteLegsListingData?,
        val loadingRouting: Boolean,
    ) : RouteContentUIState
}

@Composable
fun RouteDetailsContent(state: RouteContentUIState, onAction: (RouteDetailsAction) -> Unit) {
    when (state) {
        is RouteContentUIState.Error -> {
            ErrorCell(
                title = TextRes.Res(if (state.isNoConnection) R.string.error_no_connection else R.string.error_generic_desc),
                subtitle = TextRes.Res(if (state.isNoConnection) R.string.error_no_connection else R.string.error_generic_desc)
            ) {
                onAction(RouteDetailsAction.RetryClicked)
            }
        }

        RouteContentUIState.Loading -> {
            LoadingCell()
        }

        is RouteContentUIState.Loaded -> {
            RouteDetailsItemsListing(headerDisplayData = state.header, listing = state.listingData,
                loadingRouting = state.loadingRouting, onItemExpanded = { id ->
                onAction(RouteDetailsAction.SectionExpandCollapseClicked(id))
            })
        }
    }
}

@AppThemePreviews
@Composable
private fun RouteDetailsContentPreview() {
    RoutePlannerTheme {
        val state = RouteContentUIState.Loaded(
            header = PreviewDataUtils.routeHeader(),
            listingData = PreviewDataUtils.legsListing,
            loadingRouting = false,
        )
        RouteDetailsContent(state = state, onAction = {})
    }
}

package com.rokoblak.routeplanner.ui.feature.routelisting.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.rokoblak.routeplanner.R
import com.rokoblak.routeplanner.ui.common.TextRes
import com.rokoblak.routeplanner.ui.common.composables.ErrorCell
import com.rokoblak.routeplanner.ui.common.composables.LoadingCell
import com.rokoblak.routeplanner.ui.common.verticalScrollbar
import com.rokoblak.routeplanner.ui.feature.routedetails.RouteDetailsRoute
import com.rokoblak.routeplanner.ui.feature.routelisting.RouteListingAction
import kotlinx.collections.immutable.ImmutableList

const val TAG_ROUTE = "tag-route"

sealed interface RoutesListingData {
    data object Initial : RoutesListingData
    data class Error(val isNoConnection: Boolean) : RoutesListingData
    data class Loaded(val items: ImmutableList<RouteDisplayData>, val showLoadingAtEnd: Boolean) :
        RoutesListingData
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoutesListing(data: RoutesListingData, onAction: (RouteListingAction) -> Unit) {
    when (data) {
        is RoutesListingData.Error -> {
            ErrorCell(title = TextRes.Res(R.string.error_generic_desc),
                subtitle = TextRes.Res(R.string.error_generic_desc)) {
                onAction(RouteListingAction.RefreshTriggered)
            }
        }

        RoutesListingData.Initial -> {
            Column(modifier = Modifier.fillMaxSize()) {
                (0..10).forEach { _ ->
                    LoadingCell()
                    Divider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                }
            }
        }

        is RoutesListingData.Loaded -> {
            val lazyListState = rememberLazyListState()
            ListingScrollTracker(
                state = data,
                listState = lazyListState,
                onScrollSettledNearEnd = {
                    onAction(RouteListingAction.NextPageTriggerReached)
                }
            )
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.verticalScrollbar(lazyListState)
            ) {
                items(
                    count = data.items.size,
                    key = { data.items[it].itemId },
                    itemContent = { idx ->
                        val item = data.items[idx]
                        RouteDisplay(
                            modifier = Modifier
                                .testTag(TAG_ROUTE)
                                .animateItemPlacement()
                                .clickable {
                                    val input = RouteDetailsRoute.Input(
                                        routeId = item.routeId,
                                        name = item.name,
                                    )
                                    onAction(RouteListingAction.OpenRoute(input))
                                },
                            data = item,
                        )
                        if (idx < data.items.lastIndex) {
                            Divider(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            )
                        }
                    }
                )
                if (data.showLoadingAtEnd) {
                    item {
                        LoadingCell()
                    }
                }
            }
        }
    }
}

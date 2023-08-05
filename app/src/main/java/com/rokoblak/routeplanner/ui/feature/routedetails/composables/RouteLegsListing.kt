package com.rokoblak.routeplanner.ui.feature.routedetails.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.rokoblak.routeplanner.ui.common.AppThemePreviews
import com.rokoblak.routeplanner.ui.common.PreviewDataUtils
import com.rokoblak.routeplanner.ui.common.composables.LoadingCell
import com.rokoblak.routeplanner.ui.theme.RoutePlannerTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

const val TAG_ROUTE_HEADER = "tag-route-header"

data class RouteLegsListingData(
    val items: ImmutableList<LegSection>,
)

data class LegSection(
    val expanded: Boolean,
    val leg: LegDisplayData,
    val steps: ImmutableList<StepDisplayData>,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RouteDetailsItemsListing(
    headerDisplayData: RouteHeaderDisplayData,
    listing: RouteLegsListingData?,
    loadingRouting: Boolean,
    onItemExpanded: (id: String) -> Unit,
) {
    val lazyListState = rememberLazyListState()

    val cameraPositionState = rememberCameraPositionState {
        val center = LatLng(headerDisplayData.center.lat, headerDisplayData.center.long)
        position = CameraPosition.fromLatLngZoom(center, 12f)
    }

    var interactingWithMap by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            interactingWithMap = false
        }
    }

    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        state = lazyListState,
        userScrollEnabled = interactingWithMap.not()
    ) {
        item {
            RouteHeaderDisplay(
                modifier = Modifier.testTag(TAG_ROUTE_HEADER),
                data = headerDisplayData,
                cameraPositionState = cameraPositionState,
                onMapTouched = {
                    interactingWithMap = true
                })
        }

        if (loadingRouting) {
            item {
                LoadingCell()
            }
        }

        listing?.items?.forEachIndexed { index, section ->
            val lastSection = index == listing.items.lastIndex
            item(key = section.leg.id) {
                LegDisplay(modifier = Modifier.animateItemPlacement(), data = section.leg,
                    expanded = section.expanded,
                    isFirst = index == 0,
                    isLast = lastSection && section.expanded.not(),
                    onMarkerClicked = {
                        coroutineScope.launch {
                            lazyListState.animateScrollToItem(0)
                            val destinationLatLng =
                                LatLng(section.leg.markerLat, section.leg.markerLong)
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLng(destinationLatLng),
                                durationMs = 250,
                            )
                        }
                    },
                    onExpandClicked = {
                        onItemExpanded(section.leg.id)
                    })
            }
            items(
                count = section.steps.size,
                key = { section.steps[it].id },
                itemContent = { idx ->
                    val step = section.steps[idx]
                    StepDisplay(
                        modifier = Modifier.animateItemPlacement(),
                        curveBack = !lastSection && idx == section.steps.lastIndex,
                        isLast = lastSection && idx == section.steps.lastIndex,
                        data = step,
                    )
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
@AppThemePreviews
fun RouteLegsListingPrevie() {
    RoutePlannerTheme {
        RouteDetailsItemsListing(
            headerDisplayData = PreviewDataUtils.routeHeader(),
            listing = PreviewDataUtils.legsListing,
            loadingRouting = false,
            onItemExpanded = {},
        )
    }
}

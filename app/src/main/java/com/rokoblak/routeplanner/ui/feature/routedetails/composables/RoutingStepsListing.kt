package com.rokoblak.routeplanner.ui.feature.routedetails.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.rokoblak.routeplanner.ui.common.AppThemePreviews
import com.rokoblak.routeplanner.ui.common.PreviewDataUtils
import com.rokoblak.routeplanner.ui.theme.RoutePlannerTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoutingStepsListing(
    data: RouteLegsListingData,
    lazyListState: LazyListState,
    onMarkerClicked: (newPos: LatLng) -> Unit,
    onExpandClicked: (legId: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),
        state = lazyListState,
    ) {
        val items = data.items
        items.forEachIndexed { index, section ->
            val lastSection = index == items.lastIndex
            item(key = section.leg.id) {
                LegDisplay(modifier = Modifier.animateItemPlacement(),
                    data = section.leg,
                    expanded = section.expanded,
                    isFirst = index == 0,
                    isLast = lastSection && section.expanded.not(),
                    onMarkerClicked = {
                        onMarkerClicked(LatLng(section.leg.markerLat, section.leg.markerLong))
                    },
                    onExpandClicked = {
                        onExpandClicked(section.leg.id)
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
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@AppThemePreviews
@Composable
private fun RoutingStepsListingPreview() {
    RoutePlannerTheme {
        val data = PreviewDataUtils.legsListing

        RoutingStepsListing(
            data = data,
            lazyListState = rememberLazyListState(),
            onMarkerClicked = {},
            onExpandClicked = {})
    }
}

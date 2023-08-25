package com.rokoblak.routeplanner.ui.feature.routedetails.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.rokoblak.routeplanner.R
import com.rokoblak.routeplanner.ui.common.AppThemePreviews
import com.rokoblak.routeplanner.ui.common.PreviewDataUtils
import com.rokoblak.routeplanner.ui.common.TextRes
import com.rokoblak.routeplanner.ui.common.composables.ErrorCell
import com.rokoblak.routeplanner.ui.common.composables.LargeLoadingCell
import com.rokoblak.routeplanner.ui.common.composables.LoadingCell
import com.rokoblak.routeplanner.ui.feature.routedetails.RouteDetailsAction
import com.rokoblak.routeplanner.ui.theme.RoutePlannerTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

data class RouteScaffoldUIState(
    val title: String,
    val subtitle: TextRes = TextRes.Res(R.string.details_loading_route),
    val mainContent: MainContentState = MainContentState.Loading,
    val sheetContent: RouteLegsListingData? = null,
) {
    sealed interface MainContentState {
        data object Loading : MainContentState
        data class Error(val type: Type) : MainContentState {
            enum class Type { NoConnection, Generic, NoKeys }

            fun title() = when (type) {
                Type.NoConnection -> R.string.error_no_connection
                Type.Generic -> R.string.error_generic
                Type.NoKeys -> R.string.error_keys_missing
            }.let { TextRes.Res(it) }

            fun subtitle() = when (type) {
                Type.NoConnection -> R.string.error_no_connection_desc
                Type.Generic -> R.string.error_generic_desc
                Type.NoKeys -> R.string.no_keys_warning
            }.let { TextRes.Res(it) }
        }

        data class Loaded(val data: RouteMapsData) : MainContentState
    }
}

data class RouteLegsListingData(
    val items: ImmutableList<LegSection>,
)

data class LegSection(
    val expanded: Boolean,
    val leg: LegDisplayData,
    val steps: ImmutableList<StepDisplayData>,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailsScaffold(
    state: RouteScaffoldUIState,
    onBackClicked: () -> Unit,
    onAction: (RouteDetailsAction) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()

    val cameraPositionState = when (state.mainContent) {
        is RouteScaffoldUIState.MainContentState.Error -> null
        RouteScaffoldUIState.MainContentState.Loading -> null
        is RouteScaffoldUIState.MainContentState.Loaded -> rememberCameraPositionState {
            val center =
                LatLng(state.mainContent.data.center.lat, state.mainContent.data.center.long)
            position = CameraPosition.fromLatLngZoom(center, 12.5f)
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 128.dp,
        containerColor = MaterialTheme.colorScheme.background,
        sheetContent = {
            if (state.sheetContent != null && cameraPositionState != null) {
                val lazyListState = rememberLazyListState()

                RoutingStepsListing(data = state.sheetContent, lazyListState = lazyListState, onMarkerClicked = { destLatLng ->
                    coroutineScope.launch {
                        lazyListState.animateScrollToItem(0)
                        scaffoldState.bottomSheetState.partialExpand()
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLng(destLatLng),
                            durationMs = 250,
                        )
                    }
                }, onExpandClicked = { legId ->
                    onAction(RouteDetailsAction.SectionExpandCollapseClicked(legId))
                })

            } else {
                LoadingCell()
            }
        }) {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            when (val content = state.mainContent) {
                is RouteScaffoldUIState.MainContentState.Error -> {
                    ErrorCell(title = content.title(), subtitle = content.subtitle()) {
                        onAction(RouteDetailsAction.RetryClicked)
                    }
                }

                is RouteScaffoldUIState.MainContentState.Loaded -> {
                    RouteMapsDisplay(
                        modifier = Modifier.padding(bottom = 90.dp),
                        data = content.data,
                        cameraPositionState = cameraPositionState
                    )
                }

                RouteScaffoldUIState.MainContentState.Loading -> {
                    LargeLoadingCell()
                }
            }

            RouteDetailsTitleBar(title = state.title, subtitle = state.subtitle, onBackClicked = onBackClicked)
        }
    }
}

@AppThemePreviews
@Composable
private fun RouteDetailsScreenScaffoldPreview() {
    RoutePlannerTheme {
        val state = PreviewDataUtils.detailsScaffoldData()
        RouteDetailsScaffold(state = state, onBackClicked = {}, onAction = {})
    }
}

@AppThemePreviews
@Composable
private fun RouteDetailsScreenScaffoldPreviewLoading() {
    RoutePlannerTheme {
        val state = PreviewDataUtils.detailsScaffoldDataLoading()
        RouteDetailsScaffold(state = state, onBackClicked = {}, onAction = {})
    }
}

@AppThemePreviews
@Composable
private fun RouteDetailsScreenScaffoldPreviewError() {
    RoutePlannerTheme {
        val state = PreviewDataUtils.detailsScaffoldDataError()
        RouteDetailsScaffold(state = state, onBackClicked = {}, onAction = {})
    }
}
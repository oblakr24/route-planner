package com.rokoblak.routeplanner.ui.feature.routedetails.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
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
import com.rokoblak.routeplanner.ui.theme.alpha
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

const val TAG_ROUTE_HEADER = "tag-route-header"

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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    state = lazyListState,
                ) {
                    val items = state.sheetContent.items
                    items.forEachIndexed { index, section ->
                        val lastSection = index == items.lastIndex
                        item(key = section.leg.id) {
                            LegDisplay(modifier = Modifier.animateItemPlacement(),
                                data = section.leg,
                                expanded = section.expanded,
                                isFirst = index == 0,
                                isLast = lastSection && section.expanded.not(),
                                onMarkerClicked = {
                                    coroutineScope.launch {
                                        lazyListState.animateScrollToItem(0)
                                        scaffoldState.bottomSheetState.partialExpand()
                                        val destinationLatLng =
                                            LatLng(section.leg.markerLat, section.leg.markerLong)
                                        cameraPositionState.animate(
                                            update = CameraUpdateFactory.newLatLng(destinationLatLng),
                                            durationMs = 250,
                                        )
                                    }
                                },
                                onExpandClicked = {
                                    onAction(RouteDetailsAction.SectionExpandCollapseClicked(section.leg.id))
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

            } else {
                LoadingCell()
            }
        }) {
        Box(
            Modifier
                .background(Color.Red)
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

            TitleBar(title = state.title, subtitle = state.subtitle, onBackClicked = onBackClicked)
        }
    }
}

@Composable
private fun TitleBar(title: String, subtitle: TextRes, onBackClicked: () -> Unit) {
    ConstraintLayout(
        modifier = Modifier.testTag(TAG_ROUTE_HEADER)
            .background(Color.Black.alpha(0.15f))
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 8.dp, bottom = 4.dp, start = 4.dp, end = 4.dp)
    ) {

        val (backBtnRef, titleRef, subtitleRef) = createRefs()

        Box(
            modifier = Modifier
                .padding(8.dp)
                .constrainAs(backBtnRef) {
                    width = Dimension.wrapContent
                    height = Dimension.wrapContent
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
                .size(36.dp)
                .background(Color.Black.alpha(0.20f), shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(modifier = Modifier
                .size(36.dp),
                onClick = {
                    onBackClicked()
                }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    tint = Color.Black,
                    contentDescription = "Back"
                )
            }
        }

        Text(
            modifier = Modifier.constrainAs(titleRef) {
                width = Dimension.wrapContent
                height = Dimension.wrapContent
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            text = title,
            color = Color.Black,
            style = MaterialTheme.typography.titleMedium,
        )

        Text(
            modifier = Modifier.constrainAs(subtitleRef) {
                width = Dimension.wrapContent
                height = Dimension.wrapContent
                top.linkTo(titleRef.bottom)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            text = subtitle.resolve(),
            color = Color.Black,
            style = MaterialTheme.typography.labelMedium,
        )
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
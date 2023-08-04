package com.rokoblak.routeplanner.ui.feature.routelisting.composables

import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

private data class ListScrollState(
    val isScrollInProgress: Boolean,
    val layoutInfo: LazyListLayoutInfo,
)

/**
 * Used for propagating when a scroll settles near the end of the list, so that we can trigger a new page load
 */
@Composable
fun ListingScrollTracker(
    state: RoutesListingData.Loaded,
    listState: LazyListState,
    onScrollSettledNearEnd: () -> Unit,
) {
    val items = state.items
    LaunchedEffect(items) {
        snapshotFlow {
            ListScrollState(
                isScrollInProgress = listState.isScrollInProgress,
                layoutInfo = listState.layoutInfo,
            )
        }.filter { !it.isScrollInProgress }.distinctUntilChanged { old, new ->
            val oldKeys = old.layoutInfo.visibleItemsInfo.map { it.key }
            val newKeys = new.layoutInfo.visibleItemsInfo.map { it.key }
            old.isScrollInProgress == new.isScrollInProgress && oldKeys == newKeys
        }.collect { state ->
            val indices = state.layoutInfo.visibleItemsInfo.map {
                it.index
            }
            val bottom = indices.maxOrNull() ?: return@collect
            if (state.layoutInfo.totalItemsCount - bottom < THRESHOLD_CLOSE_TO_END) {
                onScrollSettledNearEnd()
            }
        }
    }
}

// If we are less than 5 items before the end, load the next page
private const val THRESHOLD_CLOSE_TO_END = 5
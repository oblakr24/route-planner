package com.rokoblak.routeplanner.ui.feature.routedetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rokoblak.routeplanner.ui.common.AppThemePreviews
import com.rokoblak.routeplanner.ui.common.composables.LoadingCell
import com.rokoblak.routeplanner.ui.feature.routedetails.composables.RouteContentUIState
import com.rokoblak.routeplanner.ui.feature.routedetails.composables.RouteDetailsScaffold
import com.rokoblak.routeplanner.ui.theme.RoutePlannerTheme
import kotlinx.coroutines.launch


data class RouteDetailsUIState(
    val title: String,
    val content: RouteContentUIState = RouteContentUIState.Loading,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailsScreen(viewModel: RouteDetailsViewModel = hiltViewModel()) {
//    val state = viewModel.uiState.collectAsState().value
    val state = viewModel.uiStateScaffold.collectAsState().value

    RouteDetailsScaffold(state = state, onBackClicked = {
        viewModel.navigateUp()
    }, onAction = { act ->
        viewModel.onAction(act)
    })

//    DetailsContent(title = state.title, onBackPressed = {
//        viewModel.navigateUp()
//    }) {
//        RouteDetailsContent(state = state.inner, onAction = { act ->
//            viewModel.onAction(act)
//        })
//    }
}


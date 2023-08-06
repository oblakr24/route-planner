package com.rokoblak.routeplanner.ui.feature.routedetails.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.rokoblak.routeplanner.R
import com.rokoblak.routeplanner.ui.common.AppThemePreviews
import com.rokoblak.routeplanner.ui.common.PreviewDataUtils
import com.rokoblak.routeplanner.ui.common.TextRes
import com.rokoblak.routeplanner.ui.theme.RoutePlannerTheme

data class RouteMapsData(
    val center: Point,
    val markers: List<Point>,
    val polylines: List<PolylineSegment>,
) {

    data class Point(
        val lat: Double,
        val long: Double,
        val title: TextRes? = null,
        val subtitle: TextRes? = null,
    )

    data class PolylineSegment(
        val color: Color,
        val points: List<LatLng>
    )
}

@Composable
fun RouteMapsDisplay(
    data: RouteMapsData,
    cameraPositionState: CameraPositionState?,
    modifier: Modifier = Modifier,
) {
    val camPositionState = cameraPositionState ?: rememberCameraPositionState()

    val markerStates = data.markers.map {
        it to rememberMarkerState(position = LatLng(it.lat, it.long))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopEnd,
    ) {
        var markersEnabled by remember {
            mutableStateOf(true)
        }
        GoogleMap(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 48.dp),
            cameraPositionState = camPositionState,
            content = {
                data.polylines.forEach { polyline ->
                    Polyline(
                        points = polyline.points,
                        color = polyline.color,
                        width = 12f,
                    )
                }
                if (markersEnabled) {
                    markerStates.forEach { (point, state) ->
                        MarkerInfoWindow(
                            state = state,
                            title = point.title?.resolve() ?: "Stop",
                        ) {
                            Column(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.background)
                                    .border(1.dp, Color.Black)
                                    .padding(8.dp)
                            ) {
                                if (point.title != null) {
                                    Text(point.title.resolve())
                                }
                                if (point.subtitle != null) {
                                    Text(point.subtitle.resolve())
                                }
                            }
                        }
                    }
                }
            }
        )

        Column(
            Modifier
                .padding(top = 64.dp, end = 8.dp)
                .wrapContentSize()
                .padding(horizontal = 8.dp, vertical = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Switch(
                checked = markersEnabled,
                colors = SwitchDefaults.colors(),
                onCheckedChange = { enabled ->
                    markersEnabled = enabled
                },
            )
            Text(
                stringResource(id = R.string.show_markers),
                color = Color.Black,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }

}

@Composable
@AppThemePreviews
fun RouteMapsDisplayPreview() {
    RoutePlannerTheme {
        val data = PreviewDataUtils.routeMapsData()
        RouteMapsDisplay(
            data = data,
            cameraPositionState = rememberCameraPositionState(),
        )
    }
}

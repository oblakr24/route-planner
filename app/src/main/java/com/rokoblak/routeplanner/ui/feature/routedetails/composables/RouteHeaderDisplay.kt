package com.rokoblak.routeplanner.ui.feature.routedetails.composables

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.rokoblak.routeplanner.ui.common.AppThemePreviews
import com.rokoblak.routeplanner.ui.common.PreviewDataUtils
import com.rokoblak.routeplanner.ui.common.TextRes
import com.rokoblak.routeplanner.ui.theme.RoutePlannerTheme

data class RouteHeaderDisplayData(
    val showNoKeysWarning: Boolean,
    val center: RouteContentUIState.Loaded.Point,
    val markers: List<RouteContentUIState.Loaded.Point>,
    val polyline: List<RouteContentUIState.Loaded.Point>,
    val subtitle: TextRes,
    val extraSubtitle: TextRes,
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RouteHeaderDisplay(
    data: RouteHeaderDisplayData,
    cameraPositionState: CameraPositionState,
    onMapTouched: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(MaterialTheme.colorScheme.background)
        ) {

        val markerStates = data.markers.map {
            it to rememberMarkerState(position = LatLng(it.lat, it.long))
        }

        if (data.showNoKeysWarning) {
            Text(
                modifier = Modifier.fillMaxWidth(), text = "Google maps and/or GeoApify keys missing",
                textAlign = TextAlign.Center, style = MaterialTheme.typography.titleMedium,
                color = Color.Red,
            )
            Text(
                modifier = Modifier.fillMaxWidth(), text = "In your local.properties file, add:",
                textAlign = TextAlign.Center, style = MaterialTheme.typography.labelMedium,
            )
            Text(
                modifier = Modifier.fillMaxWidth(), text = "MAPS_API_KEY=YOUR_KEY",
                textAlign = TextAlign.Center, style = MaterialTheme.typography.labelMedium,
            )
            Text(
                modifier = Modifier.fillMaxWidth(), text = "GEOAPIFY_API_KEY=YOUR_KEY",
                textAlign = TextAlign.Center, style = MaterialTheme.typography.labelMedium,
            )
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp), text = data.subtitle.resolve(),
            textAlign = TextAlign.Center, style = MaterialTheme.typography.labelLarge
        )
        Text(
            modifier = Modifier
                .height(24.dp)
                .fillMaxWidth(), textAlign = TextAlign.Center, text = data.extraSubtitle.resolve(),
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (data.showNoKeysWarning.not()) {
            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .padding(bottom = 8.dp)
                    .pointerInteropFilter(
                        onTouchEvent = {
                            when (it.action) {
                                MotionEvent.ACTION_DOWN -> {
                                    onMapTouched()
                                    false
                                }

                                else -> {
                                    true
                                }
                            }
                        }
                    ),
                cameraPositionState = cameraPositionState,
                content = {
                    Polyline(
                        points = data.polyline.map { LatLng(it.lat, it.long) },
                        color = Color.Blue,
                    )
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

            )
        }

    }
}

@Composable
@AppThemePreviews
fun RouteHeaderDisplayPreview() {
    RoutePlannerTheme {
        val data = PreviewDataUtils.routeHeader()
        RouteHeaderDisplay(
            data = data,
            cameraPositionState = rememberCameraPositionState(),
            onMapTouched = {},
        )
    }
}

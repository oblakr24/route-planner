package com.rokoblak.routeplanner.ui.feature.routedetails.composables

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.style.TextAlign
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
import com.rokoblak.routeplanner.ui.feature.routelisting.composables.TAG_SWITCH_USE_SYSTEM_THEME
import com.rokoblak.routeplanner.ui.theme.RoutePlannerTheme

data class RouteHeaderDisplayData(
    val showNoKeysWarning: Boolean,
    val center: Point,
    val markers: List<Point>,
    val polylines: List<PolylineSegment>,
    val subtitle: TextRes,
    val extraSubtitle: TextRes,
) {

    data class Point(
        val lat: Double,
        val long: Double,
        val title: TextRes? = null,
        val subtitle: TextRes? = null,
    )

    data class PolylineSegment(
        val color: Color,
        val points : List<LatLng>
    )
}

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
            noKeysWarning()
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
            var markersEnabled by remember {
                mutableStateOf(true)
            }

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
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(id = R.string.show_markers),
                    style = MaterialTheme.typography.labelLarge,
                )
                Switch(
                    modifier = Modifier.semantics { testTag = TAG_SWITCH_USE_SYSTEM_THEME },
                    checked = markersEnabled,
                    colors = SwitchDefaults.colors(),
                    onCheckedChange = { enabled ->
                        markersEnabled = enabled
                    },
                )
            }
        }

    }
}

@Composable
private fun noKeysWarning() {
    Spacer(modifier = Modifier.height(16.dp))
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

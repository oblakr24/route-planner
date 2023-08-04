package com.rokoblak.routeplanner.ui.feature.routelisting.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rokoblak.routeplanner.ui.common.AppThemePreviews
import com.rokoblak.routeplanner.ui.common.PreviewDataUtils
import com.rokoblak.routeplanner.ui.theme.RoutePlannerTheme


data class RouteDisplayData(
    val routeId: String,
    val itemId: String,
    val name: String,
)

@Composable
fun RouteDisplay(data: RouteDisplayData, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp),

    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.DirectionsBus,
                contentDescription = "Route"
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = data.name,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@AppThemePreviews
@Composable
fun RouteDisplayPreview() {
    RoutePlannerTheme {
        RouteDisplay(data = PreviewDataUtils.routeData)
    }
}

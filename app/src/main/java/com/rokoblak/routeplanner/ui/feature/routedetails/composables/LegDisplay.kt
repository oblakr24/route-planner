package com.rokoblak.routeplanner.ui.feature.routedetails.composables

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rokoblak.routeplanner.R
import com.rokoblak.routeplanner.ui.common.AppThemePreviews
import com.rokoblak.routeplanner.ui.common.PreviewDataUtils
import com.rokoblak.routeplanner.ui.common.TextRes
import com.rokoblak.routeplanner.ui.theme.RoutePlannerTheme
import kotlinx.collections.immutable.ImmutableList

data class LegDisplayData(
    val id: String,
    val title: String,
    val subtitleCollapsed: TextRes,
    val subtitleExpanded: TextRes,
    val markerLat: Double,
    val markerLong: Double,
    val students: ImmutableList<StudentDisplayData>,
)

@Composable
fun LegDisplay(
    data: LegDisplayData,
    expanded: Boolean,
    onExpandClicked: () -> Unit,
    onMarkerClicked: () -> Unit,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    modifier: Modifier = Modifier
) {
    val endXF: Dp by animateDpAsState(if (expanded) {
        BgPathUtils.largeXOffset
    } else {
        BgPathUtils.smallXOffset
    }, label = "end X")
    val pathColor = MaterialTheme.colorScheme.primary
    Column(modifier = modifier .drawWithCache {
        BgPathUtils.curveDownRight(
            color = pathColor,
            startX = BgPathUtils.smallXOffset.toPx(),
            startY = if (isFirst) 24.dp.toPx() else 0f,
            midY = size.height - 16.dp.toPx(),
            endY = if (isLast) size.height - 16.dp.toPx() else size.height,
            endX = endXF.toPx(),
            drawScope = this,
        )
    }) {
        Card(
            modifier = Modifier
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.Top,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(modifier = Modifier.size(32.dp), onClick = {
                        onMarkerClicked()
                    }) {
                        Icon(
                            imageVector =  Icons.Filled.MyLocation,
                            contentDescription = "Marker"
                        )
                    }

                    Text(
                        text = data.title,
                        modifier = Modifier.padding(top = 4.dp),
                        style = MaterialTheme.typography.labelLarge,
                    )

                    IconButton(modifier = Modifier.size(32.dp), onClick = {
                        onExpandClicked()
                    }) {
                        Icon(
                            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = "ExpandCollapse"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                if (!expanded) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = data.subtitleCollapsed.resolve(),
                        style = MaterialTheme.typography.labelSmall,
                    )
                } else {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        text = data.subtitleExpanded.resolve(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                    )
                    if (data.students.isNotEmpty()) {
                        Text(
                            modifier = Modifier
                                .padding(top = 4.dp, start = 16.dp)
                                .fillMaxWidth(),
                            text = stringResource(
                                R.string.details_num_students_to_pick_up,
                                data.students.size
                            ),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall,
                            fontStyle = FontStyle.Italic,
                        )
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp),
                        ) {
                            items(
                                count = data.students.size,
                                key = { data.students[it].id },
                                itemContent = { idx ->
                                    val item = data.students[idx]
                                    StudentDisplay(
                                        modifier = Modifier,
                                        data = item,
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
        if (expanded) {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
@AppThemePreviews
fun LegDisplayPreviewExpanded() {
    RoutePlannerTheme {
        val data = PreviewDataUtils.leg()
        LegDisplay(data = data, expanded = true, onExpandClicked = {}, onMarkerClicked = {})
    }
}

@Composable
@AppThemePreviews
fun LegDisplayPreviewCollapsed() {
    RoutePlannerTheme {
        val data = PreviewDataUtils.leg()
        LegDisplay(data = data, expanded = false, onExpandClicked = {}, onMarkerClicked = {})
    }
}

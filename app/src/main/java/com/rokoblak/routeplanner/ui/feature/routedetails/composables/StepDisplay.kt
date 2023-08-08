package com.rokoblak.routeplanner.ui.feature.routedetails.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.rokoblak.routeplanner.ui.common.AppThemePreviews
import com.rokoblak.routeplanner.ui.common.PreviewDataUtils
import com.rokoblak.routeplanner.ui.common.TextRes
import com.rokoblak.routeplanner.ui.theme.RoutePlannerTheme

data class StepDisplayData(
    val id: String,
    val name: String,
    val instruction: String?,
    val subtitle: TextRes?,
    val fallbackName: TextRes?,
)

@Composable
fun StepDisplay(data: StepDisplayData,
                curveBack: Boolean = false,
                isLast: Boolean = false,
                modifier: Modifier = Modifier) {
    val pathColor = MaterialTheme.colorScheme.primary
    Column(modifier = modifier.drawWithCache {

        if (curveBack) {
            BgPathUtils.curveBack(
                color = pathColor,
                startX = BgPathUtils.largeXOffset.toPx(),
                midY = size.height - 16.dp.toPx(),
                endX = BgPathUtils.smallXOffset.toPx(),
                drawScope = this,
            )
        } else if (isLast) {
            BgPathUtils.vertical(
                color = pathColor,
                startX = BgPathUtils.largeXOffset.toPx(),
                startY = 0f,
                endY = size.height - 24.dp.toPx(),
                drawScope = this,
            )
        } else {
            BgPathUtils.vertical(
                color = pathColor,
                startX = BgPathUtils.largeXOffset.toPx(),
                startY = 0f,
                endY = size.height,
                drawScope = this,
            )
        }

    }) {
        Card(
            modifier = modifier
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(12.dp)
                    .padding(start = 8.dp),

                ) {
                if (data.name.isNotBlank()) {
                    Text(
                        modifier = Modifier,
                        text = data.name,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
                if (data.fallbackName != null) {
                    Text(
                        modifier = Modifier,
                        text = data.fallbackName.resolve(),
                        style = MaterialTheme.typography.labelMedium,
                        fontStyle = FontStyle.Italic,
                    )
                }
                if (data.subtitle != null) {
                    Text(
                        modifier = Modifier,
                        text = data.subtitle.resolve(),
                        style = MaterialTheme.typography.labelMedium,
                        fontStyle = FontStyle.Italic,
                    )
                }
                if (data.instruction != null) {
                    Text(
                        modifier = Modifier,
                        text = data.instruction,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
        if (curveBack) {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
@AppThemePreviews
fun StepDisplayPreview() {
    RoutePlannerTheme {
        val data = PreviewDataUtils.step()
        StepDisplay(data = data)
    }
}

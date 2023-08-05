package com.rokoblak.routeplanner.ui.feature.routedetails.composables

import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.DrawResult
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

object BgPathUtils {

    val smallXOffset = 28.dp
    val largeXOffset = 38.dp

    fun curveDownRight(color: Color, startX: Float, startY: Float, midY: Float, endY: Float, endX: Float, drawScope: CacheDrawScope): DrawResult = with(drawScope) {
        val path = Path()
        path.moveTo(startX, startY)
        path.lineTo(startX, midY)

        path.cubicTo(
            x1 = startX,
            y1 = endY,
            x2 = endX,
            y2 = midY,
            x3 = endX,
            y3 = endY,
        )
        drawPath(path, color)
    }

    fun curveBack(color: Color, startX: Float, midY: Float, endX: Float, drawScope: CacheDrawScope): DrawResult =
        with(drawScope) {
            val path = Path()
            path.moveTo(startX, 0f)
            path.lineTo(startX, midY)

            path.cubicTo(
                x1 = startX,
                y1 = size.height,
                x2 = endX,
                y2 = midY,
                x3 = endX,
                y3 = size.height,
            )

            drawPath(path, color)
        }

    fun vertical(color: Color, startX: Float, startY: Float, endY: Float, drawScope: CacheDrawScope): DrawResult =
        with(drawScope) {
            val path = Path()
            path.moveTo(startX, startY)
            path.lineTo(startX, endY)

            drawPath(path, color)
        }

    private fun CacheDrawScope.drawPath(path: Path, color: Color) = onDrawBehind {
            drawPath(path, color, style = Stroke(width = 10f))
        }
}
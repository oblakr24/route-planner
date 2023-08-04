package com.rokoblak.routeplanner.ui.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rokoblak.routeplanner.ui.common.AppThemePreviews
import com.rokoblak.routeplanner.ui.theme.RoutePlannerTheme
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun LoadingCell(modifier: Modifier = Modifier) {
    val shimmerInstance = rememberShimmer(shimmerBounds = ShimmerBounds.Window)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 16.dp, horizontal = 12.dp)
            .shimmer(shimmerInstance),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .size(36.dp)
                .background(Color.LightGray, shape = CircleShape),
        )
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .width(50.dp)
                    .height(10.dp)
                    .background(Color.LightGray, shape = CircleShape),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(Color.LightGray, shape = CircleShape),
            )
        }
    }
}

@AppThemePreviews
@Composable
fun LoadingCellDisplayPreview() {
    RoutePlannerTheme {
        LoadingCell()
    }
}
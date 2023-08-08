package com.rokoblak.routeplanner.ui.feature.routedetails.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rokoblak.routeplanner.ui.common.AppThemePreviews
import com.rokoblak.routeplanner.ui.common.TextRes
import com.rokoblak.routeplanner.ui.theme.RoutePlannerTheme
import com.rokoblak.routeplanner.ui.theme.alpha

const val TAG_ROUTE_HEADER = "tag-route-header"

@Composable
fun RouteDetailsTitleBar(title: String, subtitle: TextRes, onBackClicked: () -> Unit) {
    ConstraintLayout(
        modifier = Modifier
            .testTag(TAG_ROUTE_HEADER)
            .background(Color.Black.alpha(0.15f))
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 8.dp, bottom = 4.dp, start = 4.dp, end = 4.dp)
    ) {

        val (backBtnRef, titleRef, subtitleRef) = createRefs()

        Box(
            modifier = Modifier
                .padding(8.dp)
                .constrainAs(backBtnRef) {
                    width = Dimension.wrapContent
                    height = Dimension.wrapContent
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
                .size(36.dp)
                .background(Color.Black.alpha(0.20f), shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(modifier = Modifier
                .size(36.dp),
                onClick = {
                    onBackClicked()
                }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    tint = Color.Black,
                    contentDescription = "Back"
                )
            }
        }

        Text(
            modifier = Modifier.constrainAs(titleRef) {
                width = Dimension.wrapContent
                height = Dimension.wrapContent
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            text = title,
            color = Color.Black,
            style = MaterialTheme.typography.titleMedium,
        )

        Text(
            modifier = Modifier.constrainAs(subtitleRef) {
                width = Dimension.wrapContent
                height = Dimension.wrapContent
                top.linkTo(titleRef.bottom)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            text = subtitle.resolve(),
            color = Color.Black,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@AppThemePreviews
@Composable
private fun RouteDetailsTitleBarPreview() {
    RoutePlannerTheme {
        RouteDetailsTitleBar(
            title = "Title",
            subtitle = TextRes.Text("Subtitle with more info")
        ) {}
    }
}
package com.rokoblak.routeplanner.ui.feature.routedetails.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rokoblak.routeplanner.ui.common.AppThemePreviews
import com.rokoblak.routeplanner.ui.common.PreviewDataUtils
import com.rokoblak.routeplanner.ui.common.TextRes
import com.rokoblak.routeplanner.ui.theme.RoutePlannerTheme

data class StudentDisplayData(
    val id: String,
    val name: String,
    val subtitle: TextRes?,
)

@Composable
fun StudentDisplay(data: StudentDisplayData, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(6.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.PersonAdd,
            contentDescription = "Student"
        )
        Text(
            text = data.name,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
        )
        if (data.subtitle != null) {
            Text(
                text = data.subtitle.resolve(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
@AppThemePreviews
fun StudentDisplayPreview() {
    RoutePlannerTheme {
        val data = PreviewDataUtils.student()
        StudentDisplay(data = data)
    }
}

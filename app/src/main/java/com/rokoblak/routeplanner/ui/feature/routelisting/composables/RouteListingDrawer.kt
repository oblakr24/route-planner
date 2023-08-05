package com.rokoblak.routeplanner.ui.feature.routelisting.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rokoblak.routeplanner.BuildConfig
import com.rokoblak.routeplanner.R
import com.rokoblak.routeplanner.ui.feature.routelisting.RouteListingAction
import com.rokoblak.routeplanner.ui.theme.RoutePlannerTheme
import com.rokoblak.routeplanner.ui.theme.alpha


const val TAG_DRAWER = "tag-drawer"
const val TAG_SWITCH_USE_SYSTEM_THEME = "tag-switch-use-system-theme"
const val TAG_SWITCH_DARK_MODE = "tag-switch-dark-mode"

data class RouteListingDrawerUIState(
    val darkMode: Boolean?,
)

@Composable
fun RouteListingDrawer(
    state: RouteListingDrawerUIState,
    onAction: (RouteListingAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .semantics { testTag = TAG_DRAWER }
            .background(color = MaterialTheme.colorScheme.background)
            .padding(top = 16.dp)
            .fillMaxWidth(), horizontalAlignment = Alignment.Start
    ) {
        Text(
            modifier = Modifier.padding(12.dp),
            text = stringResource(id = R.string.drawer_title),
            style = MaterialTheme.typography.labelMedium,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.weight(1f))
        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onBackground.alpha(0.1f))
                .padding(16.dp)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(id = R.string.use_system_theme),
                    style = MaterialTheme.typography.labelLarge,
                )
                Switch(
                    modifier = Modifier.semantics { testTag = TAG_SWITCH_USE_SYSTEM_THEME },
                    checked = state.darkMode == null,
                    colors = SwitchDefaults.colors(),
                    onCheckedChange = { enabled ->
                        val newDarkMode = if (enabled) null else (state.darkMode ?: false)
                        onAction(RouteListingAction.SetDarkMode(newDarkMode))
                    },
                )
            }
            AnimatedVisibility(visible = state.darkMode != null) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        stringResource(id = R.string.dark_mode),
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Switch(
                        modifier = Modifier.semantics { testTag = TAG_SWITCH_DARK_MODE },
                        checked = state.darkMode ?: false,
                        enabled = state.darkMode != null,
                        colors = SwitchDefaults.colors(),
                        onCheckedChange = { enabled ->
                            onAction(RouteListingAction.SetDarkMode(enabled))
                        },
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Version ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})",
                style = MaterialTheme.typography.labelSmall,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview
@Composable
private fun ListingDrawerPreview() {
    val darkMode: Boolean? = null
    RoutePlannerTheme(overrideDarkMode = darkMode) {
        RouteListingDrawer(
            RouteListingDrawerUIState(
                darkMode = darkMode,
            ),
            onAction = {})
    }
}

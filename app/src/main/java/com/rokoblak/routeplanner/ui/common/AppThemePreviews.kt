package com.rokoblak.routeplanner.ui.common

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Dark theme",
    group = "themes",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    name = "Light theme",
    group = "themes",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
annotation class AppThemePreviews
package com.rokoblak.routeplanner.ui.theme

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.Color


val PrimaryDark = Color.DarkGray
val PrimaryLight = Color(0xFFD6D6EC)

val SecondaryLight = Color.DarkGray
val SecondaryDark = Color.LightGray

val ButtonBg = Color(0xFF0014D3)

fun Color.alpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float): Color = this.copy(alpha = alpha)
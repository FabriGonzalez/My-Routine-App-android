package com.myroutine.app.ui.theme


import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

fun autoContentColor(background: Color): Color {
    return if (background.luminance() > 0.5f) {
        Color.Black
    } else {
        Color.White
    }
}

fun generateSecondary(primary: Color): Color {
    return if (primary.luminance() > 0.5f) {
        primary.copy(alpha = 0.85f)
    } else {
        primary.copy(alpha = 0.9f)
    }
}
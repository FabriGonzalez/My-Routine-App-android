package com.myroutine.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun MyRoutineTheme(
    themeConfig: AppThemeConfig,
    content: @Composable () -> Unit
) {
    val colorScheme = remember(themeConfig) { buildColorScheme(themeConfig) }

    // Con fondos claros (ej. Atardecer) los íconos del sistema tienen que ser oscuros
    val view = LocalView.current
    if (!view.isInEditMode) {
        val lightBars = themeConfig.background.luminance() > 0.5f
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = lightBars
                isAppearanceLightNavigationBars = lightBars
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

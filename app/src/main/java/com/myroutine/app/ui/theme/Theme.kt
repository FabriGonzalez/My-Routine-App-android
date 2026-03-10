package com.myroutine.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

@Composable
fun MyRoutineTheme(
    themeConfig: AppThemeConfig,
    content: @Composable () -> Unit
) {

    val primary = themeConfig.primary
    val background = themeConfig.background
    val surface = themeConfig.surface

    val secondary = generateSecondary(primary)

    val outline = generateOutline(primary)

    val colorScheme = darkColorScheme(
        primary = primary,
        secondary = secondary,
        background = background,
        surface = surface,
        onPrimary = autoContentColor(primary),
        onBackground = autoContentColor(background),
        onSurface = autoContentColor(surface),
        outline = outline
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
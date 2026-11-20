package com.myroutine.app.ui.theme

import androidx.compose.ui.graphics.Color

data class AppThemeConfig(
    val primary: Color,
    val background: Color,
    val surface: Color
)

data class ThemePreset(
    val name: String,
    val config: AppThemeConfig
)

val themePresets = listOf(
    ThemePreset(
        name = "Verde",
        config = AppThemeConfig(
            primary = Color(0xFF2EE59D),
            background = Color(0xFF0E1211),
            surface = Color(0xFF171C1A)
        )
    ),
    ThemePreset(
        name = "Rosa",
        config = AppThemeConfig(
            primary = Color(0xFFFF6FAE),
            background = Color(0xFF140D11),
            surface = Color(0xFF21151B)
        )
    ),
    ThemePreset(
        name = "Atardecer",
        config = AppThemeConfig(
            primary = Color(0xFFB177D5),
            background = Color(0xFFFFFFBD),
            surface = Color(0xFFFFDFA6)
        )
    ),
    ThemePreset(
        name = "Lila nube",
        config = AppThemeConfig(
            primary = Color(0xFF7C5CC4),
            background = Color(0xFFF3EEFA),
            surface = Color(0xFFFFFFFF)
        )
    )
)

val defaultThemeConfig = themePresets.first().config

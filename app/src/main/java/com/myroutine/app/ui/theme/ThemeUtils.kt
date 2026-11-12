package com.myroutine.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance

private val InkDark = Color(0xFF0B0F0D)
private val InkLight = Color(0xFFF0F4F2)

// Elige la tinta (clara u oscura) con mayor contraste WCAG sobre el fondo dado
fun autoContentColor(background: Color): Color =
    if (contrast(background, InkDark) >= contrast(background, InkLight)) InkDark else InkLight

private fun contrast(a: Color, b: Color): Float {
    val la = a.luminance()
    val lb = b.luminance()
    return (maxOf(la, lb) + 0.05f) / (minOf(la, lb) + 0.05f)
}

/**
 * Arma el esquema completo a partir de los 3 colores que elige el usuario.
 * Material 3 trae valores por defecto violáceos para los roles que no se definen,
 * por eso derivamos todos (contenedores, bordes, texto secundario) mezclando
 * superficie, texto y primario.
 */
fun buildColorScheme(config: AppThemeConfig): ColorScheme {
    val primary = config.primary
    val background = config.background
    val surface = config.surface

    val onSurface = autoContentColor(surface)
    val onPrimary = autoContentColor(primary)
    val isDark = background.luminance() < 0.5f

    val base = if (isDark) darkColorScheme() else lightColorScheme()

    return base.copy(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = lerp(surface, primary, 0.18f),
        onPrimaryContainer = if (isDark) primary else lerp(primary, Color.Black, 0.5f),
        inversePrimary = lerp(primary, if (isDark) Color.Black else Color.White, 0.45f),

        secondary = primary,
        onSecondary = onPrimary,
        secondaryContainer = lerp(surface, primary, 0.10f),
        onSecondaryContainer = onSurface,

        tertiary = primary,
        onTertiary = onPrimary,

        background = background,
        onBackground = autoContentColor(background),

        surface = surface,
        onSurface = onSurface,
        surfaceTint = surface,
        surfaceVariant = lerp(surface, onSurface, 0.06f),
        onSurfaceVariant = lerp(onSurface, surface, 0.32f),

        surfaceContainerLowest = background,
        surfaceContainerLow = lerp(background, surface, 0.5f),
        surfaceContainer = surface,
        surfaceContainerHigh = lerp(surface, onSurface, 0.05f),
        surfaceContainerHighest = lerp(surface, onSurface, 0.09f),
        surfaceBright = lerp(surface, onSurface, 0.12f),
        surfaceDim = background,

        inverseSurface = onSurface,
        inverseOnSurface = background,

        outline = lerp(onSurface, surface, 0.6f),
        outlineVariant = lerp(surface, onSurface, 0.10f),
        scrim = Color.Black
    )
}

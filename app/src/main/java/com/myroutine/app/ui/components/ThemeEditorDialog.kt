package com.myroutine.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.myroutine.app.ui.theme.ThemePreset
import com.myroutine.app.ui.theme.ThemeViewModel
import com.myroutine.app.ui.theme.autoContentColor
import com.myroutine.app.ui.theme.themePresets

@Composable
fun ThemeEditorDialog(
    themeViewModel: ThemeViewModel,
    onDismiss: () -> Unit
) {
    val themeConfig by themeViewModel.themeConfig.collectAsState()
    var pickerFor by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = MaterialTheme.colorScheme.primary)
            }
        },
        title = { Text("Personalizar tema") },
        text = {
            Column {
                SectionLabel("PALETAS")
                Spacer(Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    themePresets.forEach { preset ->
                        PresetCard(
                            preset = preset,
                            selected = preset.config == themeConfig,
                            onClick = { themeViewModel.applyPreset(preset.config) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
                SectionLabel("PERSONALIZADO")
                Spacer(Modifier.height(4.dp))

                ColorOptionRow("Primario", themeConfig.primary) {
                    pickerFor = "primary"
                }

                ColorOptionRow("Fondo", themeConfig.background) {
                    pickerFor = "background"
                }

                ColorOptionRow("Tarjetas y paneles", themeConfig.surface) {
                    pickerFor = "surface"
                }
            }
        }
    )

    if (pickerFor != null) {
        ColorPickerDialog(
            onDismiss = { pickerFor = null },
            onColorSelected = { color ->
                when (pickerFor) {
                    "primary" -> themeViewModel.updatePrimary(color)
                    "background" -> themeViewModel.updateBackground(color)
                    "surface" -> themeViewModel.updateSurface(color)
                }
            }
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

// Muestra la paleta con sus propios colores, así se ve cómo queda antes de aplicarla
@Composable
private fun PresetCard(
    preset: ThemePreset,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val config = preset.config

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = config.background,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) config.primary else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                listOf(config.primary, config.surface).forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .border(2.dp, config.background, CircleShape)
                            .padding(1.dp)
                            .background(color, CircleShape)
                    )
                }
            }
            Text(
                text = preset.name,
                style = MaterialTheme.typography.titleSmall,
                color = autoContentColor(config.background),
                modifier = Modifier.weight(1f)
            )
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Paleta activa",
                    tint = autoContentColor(config.background),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

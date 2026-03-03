package com.myroutine.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.myroutine.app.ui.theme.ThemeViewModel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun ThemeEditorDialog(
    themeViewModel: ThemeViewModel,
    onDismiss: () -> Unit
){
    val themeConfig by themeViewModel.themeConfig.collectAsState()
    var pickerFor by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        confirmButton = {
            TextButton (onClick = onDismiss) {
                Text("Cerrar", color = MaterialTheme.colorScheme.primary)
            }
        },
        title = { Text("Personalizar tema") },
        text = {
            Column {

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
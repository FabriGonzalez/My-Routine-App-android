package com.myroutine.app.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsDrawerContent(
    onChangeDays: () -> Unit,
    onChangeColors: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier,
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerContentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Text(
            text = "Ajustes",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(20.dp)
        )
        NavigationDrawerItem(
            label = { Text("Cambiar cantidad de días", color = MaterialTheme.colorScheme.onSurface) },
            selected = false,
            onClick = onChangeDays,
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
        )
        NavigationDrawerItem(
            label = { Text("Cambiar colores de la app", color = MaterialTheme.colorScheme.onSurface) },
            selected = false,
            onClick = onChangeColors,
            icon = { Icon(Icons.Default.ColorLens, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
        )
    }
}
package com.myroutine.app.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenCalendar: () -> Unit,
    onDayCompleted: (routineDayNumber: Int, timestamp: Long) -> Unit = { _, _ -> }
) {
    var daysCount by rememberSaveable { mutableStateOf<Int?>(null) }
    var showDialog by remember { mutableStateOf(true) }
    var currentDayIndex by rememberSaveable { mutableIntStateOf(0) }

    // Dialog para pedir cantidad de días
    if (daysCount == null && showDialog) {
        var input by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { /* no dismiss */ },
            title = { Text(text = "¿De cuántos días es tu rutina?") },
            text = {
                OutlinedTextField(
                    value = input,
                    onValueChange = { new ->
                        input = new.filter { it.isDigit() }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("Ej: 3") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val n = input.toIntOrNull() ?: 0
                    if (n > 0) {
                        daysCount = n
                        currentDayIndex = 0
                        showDialog = false
                    }
                }) {
                    Text("Aceptar")
                }
            }
        )
    }

    val days = remember(daysCount) { daysCount?.let { (1..it).toList() } ?: emptyList() }
    val currentDay = if (days.isNotEmpty()) days[currentDayIndex] else 0

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Título
                TopAppBar(
                    title = { Text("Mi Rutina") },
                    actions = {
                        IconButton(onClick = onOpenCalendar) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Ver calendario"
                            )
                        }
                    }
                )
                // Barra horizontal deslizable con los días
                if (days.isNotEmpty()) {
                    val scrollState = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState)
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        days.forEachIndexed { index, day ->
                            val selected = index == currentDayIndex
                            Surface(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clickable { currentDayIndex = index },
                                shape = CircleShape,
                                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                tonalElevation = if (selected) 4.dp else 1.dp
                            ) {
                                Text(
                                    text = "Día $day",
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (days.isNotEmpty()) {
                BottomAppBar {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Botón agregar ejercicio (izquierda)
                        IconButton(onClick = {
                            // TODO: Navegar a pantalla de agregar ejercicio
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Agregar ejercicio"
                            )
                        }

                        // Botón día completo (derecha)
                        Button(onClick = {
                            // Marcar día como entrenado con timestamp actual
                            val timestamp = System.currentTimeMillis()
                            onDayCompleted(currentDay, timestamp)
                            // Avanza al siguiente día (ciclo)
                            if (days.isNotEmpty()) {
                                currentDayIndex = (currentDayIndex + 1) % days.size
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Día completo"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Día completo")
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (days.isEmpty()) {
                // Mensaje mientras no se configure la rutina
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Configura tu rutina para comenzar")
                }
            } else {
                Text(
                    text = "Día $currentDay",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Placeholder para ejercicios
                Text(
                    text = "Ejercicios del día",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Presiona + para agregar ejercicios",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

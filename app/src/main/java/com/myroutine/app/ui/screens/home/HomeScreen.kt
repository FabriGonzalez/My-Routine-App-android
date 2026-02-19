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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenCalendar: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val daysCount by viewModel.daysCount.collectAsState()
    val currentDayIndex by viewModel.currentDayIndex.collectAsState()
    val exercises by viewModel.exercisesOfDay.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    val days = remember(daysCount) { daysCount?.let { (1..it).toList() } ?: emptyList() }
    val currentDay = if (days.isNotEmpty()) days[currentDayIndex] else 0


    if(daysCount == null){
        var input by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { },
            title = { Text("¿De cuántos días es tu rutina?") },
            text = {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it.filter { c -> c.isDigit() } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("Ej: 3") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val n = input.toIntOrNull() ?: 0
                    if (n > 0) {
                        viewModel.saveRoutine(n)
                    }
                }) {
                    Text("Aceptar")
                }
            }
        )
    }


    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Mi Rutina") },
                    actions = {
                        IconButton(onClick = onOpenCalendar) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Calendario")
                        }
                    }
                )

                if (days.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(8.dp)
                    ) {
                        days.forEachIndexed { index, day ->
                            val selected = index == currentDayIndex

                            Surface(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clickable { viewModel.selectDay(index) },
                                shape = CircleShape,
                                color = if (selected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "Día $day",
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    color = if (selected)
                                        MaterialTheme.colorScheme.onPrimary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
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
                        IconButton(onClick = { /* TODO: navegar a agregar ejercicio */ }) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar ejercicio")
                        }

                        Button(
                            onClick = {
                                viewModel.completeDay(
                                    daysSize = days.size,
                                    currentIndex = currentDayIndex
                                )
                            }

                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
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

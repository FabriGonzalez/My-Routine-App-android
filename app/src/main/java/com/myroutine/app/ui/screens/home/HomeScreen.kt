package com.myroutine.app.ui.screens.home

import androidx.compose.foundation.background
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
import com.myroutine.app.ui.components.AddExerciseDialog
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import com.myroutine.app.ui.components.TableContent
import com.myroutine.app.ui.components.TableHeader

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
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        days.forEachIndexed { index, day ->
                            val selected = index == currentDayIndex

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clickable { viewModel.selectDay(index) },
                                shape = CircleShape,
                                color = if (selected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Día $day",
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
            }
        },
        bottomBar = {
            if (days.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Transparent,
                    tonalElevation = 3.dp
                ) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showAddDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = ButtonDefaults.outlinedButtonBorder
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Agregar ejercicio",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.completeDay(
                                    daysSize = days.size,
                                    currentIndex = currentDayIndex
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Día completo",
                                style = MaterialTheme.typography.titleSmall
                            )
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
                return@Column
            }
            if (exercises.isEmpty()) {
                Text(
                    text = "Presiona + para agregar ejercicios",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {

                    TableHeader()

                    TableContent(
                        exercises = exercises,
                        onDelete = { viewModel.deleteExercise(it) },
                        onMove = { from, to ->
                            viewModel.reorderExercises(from, to, exercises)
                        },
                        onUpdate = { viewModel.updateExercise(it) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    if(showAddDialog){
        AddExerciseDialog(
            show = showAddDialog,
            currentDayIndex = currentDayIndex,
            onDismiss = { showAddDialog = false },
            onAddExercise = {name, sets, reps, weight, dayIndex ->
                viewModel.addExercise(name = name, sets = sets, reps = reps, weight = weight, dayIndex)
                showAddDialog = false
            }
        )
    }
}

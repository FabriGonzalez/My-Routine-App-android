package com.myroutine.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.myroutine.app.ui.components.AddExerciseDialog
import com.myroutine.app.ui.components.RoutineDaysDialog
import com.myroutine.app.ui.components.SettingsDrawerContent
import com.myroutine.app.ui.components.TableContent
import com.myroutine.app.ui.components.ThemeEditorDialog
import com.myroutine.app.ui.components.TrainingDayPickerDialog
import com.myroutine.app.ui.theme.ThemeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenCalendar: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel
) {

    val daysCount by viewModel.daysCount.collectAsState()
    val loadedDayIndex by viewModel.currentDayIndex.collectAsState()
    val loadedExercises by viewModel.exercisesOfDay.collectAsState()
    val currentDayIndex = loadedDayIndex ?: 0
    val exercises = loadedExercises.orEmpty()
    val hasTrainedToday by viewModel.hasTrainedToday.collectAsState()
    val isCompletingDay by viewModel.isCompletingDay.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var showDaysDialog by remember { mutableStateOf(false) }
    var showThemeEditor by remember { mutableStateOf(false) }
    var showTrainingDayPicker by remember { mutableStateOf(false) }

    LaunchedEffect(daysCount) {
        if (daysCount != null && daysCount == 0) {
            showDaysDialog = true
        }
    }

    val days = remember(daysCount) { daysCount?.let { (1..it).toList() } ?: emptyList() }

    // Mientras se lee la configuración y la base no se muestra contenido, así no parpadean los estados vacíos
    val isLoading = daysCount == null ||
        (days.isNotEmpty() && (loadedDayIndex == null || loadedExercises == null))

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SettingsDrawerContent(
                onChangeDays = {
                    scope.launch { drawerState.close() }
                    showDaysDialog = true
                },
                onChangeColors = {
                    scope.launch { drawerState.close() }
                    showThemeEditor = true
                }
            )
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                Column {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            titleContentColor = MaterialTheme.colorScheme.onBackground,
                            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                            actionIconContentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Tune, contentDescription = "Ajustes")
                            }
                        },
                        title = {
                            if (!isLoading && days.isNotEmpty()) {
                                Column {
                                    Text(
                                        text = "Día ${currentDayIndex + 1}",
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Text(
                                        text = "de ${days.size} · ${exerciseCountLabel(exercises.size)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        actions = {
                            IconButton(onClick = onOpenCalendar) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = "Calendario")
                            }
                        }
                    )

                    if (!isLoading && days.isNotEmpty()) {
                        DaySelector(
                            days = days,
                            selectedIndex = currentDayIndex,
                            onSelect = { viewModel.selectDay(it) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            },
            bottomBar = {
                if (days.isNotEmpty() && exercises.isNotEmpty()) {
                    BottomActions(
                        hasTrainedToday = hasTrainedToday,
                        isCompletingDay = isCompletingDay,
                        onAdd = { showAddDialog = true },
                        onComplete = { showTrainingDayPicker = true }
                    )
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when {
                    isLoading -> Unit

                    days.isEmpty() -> Text(
                        text = "Configurá tu rutina para empezar",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    exercises.isEmpty() -> EmptyExercises(
                        dayNumber = currentDayIndex + 1,
                        onAdd = { showAddDialog = true },
                        modifier = Modifier.align(Alignment.Center)
                    )

                    else -> TableContent(
                        exercises = exercises,
                        onDelete = { exercise ->
                            viewModel.deleteExercise(exercise)
                            scope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                val result = snackbarHostState.showSnackbar(
                                    message = "Borraste «${exercise.name}»",
                                    actionLabel = "Deshacer",
                                    duration = SnackbarDuration.Short
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    viewModel.restoreExercise(exercise)
                                }
                            }
                        },
                        onMove = { from, to ->
                            viewModel.reorderExercises(from, to, exercises)
                        },
                        onUpdate = { viewModel.updateExercise(it) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    if (showDaysDialog) {
        RoutineDaysDialog(
            allowDismiss = daysCount != null,
            onDismiss = { showDaysDialog = false },
            onConfirm = {
                viewModel.saveRoutine(it)
                showDaysDialog = false
            }
        )
    }

    if (showAddDialog) {
        AddExerciseDialog(
            show = showAddDialog,
            currentDayIndex = currentDayIndex,
            onDismiss = { showAddDialog = false },
            onAddExercise = { name, sets, reps, measureValue, measureType, failureValue, dayIndex ->
                viewModel.addExercise(
                    name = name,
                    sets = sets,
                    reps = reps,
                    measureValue = measureValue,
                    measureType = measureType,
                    failureValue = failureValue,
                    dayIndex = dayIndex
                )
                showAddDialog = false
            }
        )
    }

    if (showTrainingDayPicker) {
        TrainingDayPickerDialog(
            onDismiss = { showTrainingDayPicker = false },
            onConfirm = { selectedTimestamp ->
                viewModel.completeDay(
                    daysSize = days.size,
                    currentIndex = currentDayIndex,
                    trainingTimestamp = selectedTimestamp
                )
                showTrainingDayPicker = false
            }
        )
    }

    if (showThemeEditor) {
        ThemeEditorDialog(
            themeViewModel = themeViewModel,
            onDismiss = { showThemeEditor = false }
        )
    }
}

private fun exerciseCountLabel(count: Int): String = when (count) {
    0 -> "sin ejercicios"
    1 -> "1 ejercicio"
    else -> "$count ejercicios"
}

@Composable
private fun DaySelector(
    days: List<Int>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        days.forEachIndexed { index, day ->
            val selected = index == selectedIndex

            Surface(
                onClick = { onSelect(index) },
                shape = CircleShape,
                color = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier.height(40.dp)
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Día $day",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomActions(
    hasTrainedToday: Boolean,
    isCompletingDay: Boolean,
    onAdd: () -> Unit,
    onComplete: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surfaceContainerLow) {
        Column {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onAdd,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Agregar", style = MaterialTheme.typography.titleSmall)
                }

                Button(
                    onClick = onComplete,
                    enabled = !hasTrainedToday && !isCompletingDay,
                    modifier = Modifier
                        .weight(1.4f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    if (isCompletingDay) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = when {
                            hasTrainedToday -> "Ya entrenaste hoy"
                            isCompletingDay -> "Guardando…"
                            else -> "Completar día"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyExercises(
    dayNumber: Int,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }
        Text(
            text = "Todavía no hay ejercicios",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Agregá los ejercicios del Día $dayNumber para empezar a registrar tus cargas.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onAdd,
            modifier = Modifier
                .padding(top = 8.dp)
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(8.dp))
            Text("Agregar ejercicio", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
    }
}

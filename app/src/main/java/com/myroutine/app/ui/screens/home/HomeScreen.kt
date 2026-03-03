package com.myroutine.app.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.myroutine.app.ui.components.AddExerciseDialog
import androidx.compose.foundation.shape.RoundedCornerShape
import com.myroutine.app.ui.components.RoutineDaysDialog
import com.myroutine.app.ui.components.SettingsDrawerContent
import com.myroutine.app.ui.components.TableContent
import com.myroutine.app.ui.components.TableHeader
import com.myroutine.app.ui.components.ThemeEditorDialog
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
    val currentDayIndex by viewModel.currentDayIndex.collectAsState()
    val exercises by viewModel.exercisesOfDay.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var showDaysDialog by remember { mutableStateOf(false) }
    var showThemeEditor by remember { mutableStateOf(false) }

    LaunchedEffect(daysCount) {
        if (daysCount != null && daysCount == 0) {
            showDaysDialog = true
        }
    }

    val days = remember(daysCount) { daysCount?.let { (1..it).toList() } ?: emptyList() }


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
            topBar = {
                Column {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface,
                            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                            actionIconContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Settings, contentDescription = "Ajustes")
                            }
                        },
                        title = {},
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
                                        MaterialTheme.colorScheme.onBackground
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
                                                MaterialTheme.colorScheme.background
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
                        color = MaterialTheme.colorScheme.surface
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
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Agregar",
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
                        Text(
                            text = "Configura tu rutina para comenzar",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    return@Column
                }
                if (exercises.isEmpty()) {
                    Text(
                        text = "Presiona + para agregar ejercicios",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
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

    if(showAddDialog){
        AddExerciseDialog(
            show = showAddDialog,
            currentDayIndex = currentDayIndex,
            onDismiss = { showAddDialog = false },
            onAddExercise = { name, sets, reps, measureValue, measureType, dayIndex ->
                viewModel.addExercise(
                    name = name,
                    sets = sets,
                    reps = reps,
                    measureValue = measureValue,
                    measureType = measureType,
                    dayIndex = dayIndex
                )
                showAddDialog = false
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

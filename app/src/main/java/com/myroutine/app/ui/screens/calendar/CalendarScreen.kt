package com.myroutine.app.ui.screens.calendar

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.myroutine.app.data.local.relation.TrainingSessionWithExercises
import com.myroutine.app.ui.components.ExerciseHistoryTable
import com.myroutine.app.ui.components.StreakSection
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onBack: () -> Unit,
    viewModel: CalendarViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Calendario", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MonthNavigator(
                year = uiState.currentYear,
                month = uiState.currentMonth,
                onPreviousMonth = { viewModel.goToPreviousMonth() },
                onNextMonth = { viewModel.goToNextMonth() }
            )

            WeekDaysHeader()

            CalendarGrid(
                year = uiState.currentYear,
                month = uiState.currentMonth,
                trainedDates = uiState.trainedDates,
                selectedDate = uiState.selectedDate,
                onDateSelected = { viewModel.selectDate(it) }
            )

            CalendarLegend(showHint = uiState.selectedDate == null)

            Spacer(Modifier.height(4.dp))

            AnimatedContent(
                targetState = uiState.selectedDate to uiState.trainingsForSelectedDate,
                label = "selectedDate"
            ) { (selectedDate, sessions) ->
                if (selectedDate == null) {
                    StreakSection(uiState)
                } else {
                    SelectedDateInfo(
                        date = selectedDate,
                        sessions = sessions,
                        // Volver a tocar la fecha elegida la deselecciona (ver CalendarViewModel.selectDate)
                        onShowSummary = { viewModel.selectDate(selectedDate) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthNavigator(
    year: Int,
    month: Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val monthNames = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = monthNames[month],
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = year.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 3.dp)
            )
        }

        MonthButton(onClick = onPreviousMonth) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Mes anterior")
        }
        Spacer(Modifier.width(4.dp))
        MonthButton(onClick = onNextMonth) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Mes siguiente")
        }
    }
}

@Composable
private fun MonthButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier.size(44.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        content = content
    )
}

@Composable
private fun WeekDaysHeader() {
    val locale = Locale.getDefault()

    val initials = remember(locale) {
        val calendar = Calendar.getInstance(locale)
        val firstDay = calendar.firstDayOfWeek
        val format = SimpleDateFormat("EEE", locale)

        (0..6).map { index ->
            calendar.set(Calendar.DAY_OF_WEEK, (firstDay + index - 1) % 7 + 1)
            format.format(calendar.time).take(1).uppercase(locale)
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        initials.forEach { initial ->
            Text(
                text = initial,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    year: Int,
    month: Int,
    trainedDates: Set<Long>,
    selectedDate: Long?,
    onDateSelected: (Long) -> Unit
) {
    val weeks = remember(year, month) { getCalendarDays(year, month).chunked(7) }
    val today = remember { normalizeToStartOfDay(System.currentTimeMillis()) }

    // Filas fijas en vez de LazyVerticalGrid: la pantalla entera scrollea y un lazy anidado no puede
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        weeks.forEach { week ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                week.forEach { day ->
                    val dayStart = day?.let { normalizeToStartOfDay(it.timeInMillis) }

                    CalendarDayCell(
                        day = day,
                        isTrained = dayStart != null && trainedDates.contains(dayStart),
                        isSelected = dayStart != null && dayStart == selectedDate,
                        isToday = dayStart == today,
                        isFuture = dayStart != null && dayStart > today,
                        onClick = { day?.let { onDateSelected(it.timeInMillis) } },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    day: Calendar?,
    isTrained: Boolean,
    isSelected: Boolean,
    isToday: Boolean,
    isFuture: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .then(
                if (isSelected) Modifier.border(2.dp, colors.onBackground, CircleShape)
                else Modifier
            )
            .padding(3.dp)
            .clip(CircleShape)
            .then(if (day != null) Modifier.clickable(onClick = onClick) else Modifier)
            .then(if (isTrained) Modifier.background(colors.primary) else Modifier)
            .then(
                if (isToday) Modifier.border(1.5.dp, colors.onBackground, CircleShape)
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (day != null) {
            Text(
                text = day.get(Calendar.DAY_OF_MONTH).toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isTrained || isToday) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    isTrained -> colors.onPrimary
                    isFuture -> colors.onSurfaceVariant.copy(alpha = 0.5f)
                    else -> colors.onBackground
                }
            )
        }
    }
}

@Composable
private fun CalendarLegend(showHint: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LegendItem(label = "Entrenado") {
            Box(
                Modifier
                    .size(12.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
        }
        LegendItem(label = "Hoy") {
            Box(
                Modifier
                    .size(12.dp)
                    .border(1.5.dp, MaterialTheme.colorScheme.onBackground, CircleShape)
            )
        }
        if (showHint) {
            Text(
                text = "Tocá un día para ver el detalle",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun LegendItem(label: String, marker: @Composable () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        marker()
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SelectedDateInfo(
    date: Long,
    sessions: List<TrainingSessionWithExercises>,
    onShowSummary: () -> Unit
) {
    val formattedDate = remember(date) {
        SimpleDateFormat("EEEE d 'de' MMMM", Locale.getDefault())
            .format(Date(date))
            .replaceFirstChar { it.uppercase() }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            Surface(
                onClick = onShowSummary,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Row(
                    modifier = Modifier
                        .height(36.dp)
                        .padding(start = 10.dp, end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("Ver resumen", style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        if (sessions.isEmpty()) {
            Text(
                text = "Sin entrenamientos registrados",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        sessions.forEach { session ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Día ${session.session.routineDayNumber}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = if (session.exercises.size == 1) "1 ejercicio" else "${session.exercises.size} ejercicios",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                ExerciseHistoryTable(exercises = session.exercises)
            }
        }
    }
}

private fun getCalendarDays(year: Int, month: Int): List<Calendar?> {

    val days = mutableListOf<Calendar?>()

    val locale = Locale.getDefault()

    val calendar = Calendar.getInstance(locale).apply {
        firstDayOfWeek = Calendar.getInstance(locale).firstDayOfWeek
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val firstDayOffset =
        (calendar.get(Calendar.DAY_OF_WEEK) - calendar.firstDayOfWeek + 7) % 7

    repeat(firstDayOffset) {
        days.add(null)
    }

    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    repeat(daysInMonth) {
        days.add(calendar.clone() as Calendar)
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }

    while (days.size % 7 != 0) {
        days.add(null)
    }

    return days
}

private fun normalizeToStartOfDay(timestamp: Long): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = timestamp
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}

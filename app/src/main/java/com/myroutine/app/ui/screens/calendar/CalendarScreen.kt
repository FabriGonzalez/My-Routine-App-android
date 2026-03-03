package com.myroutine.app.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
                title = { Text("Calendario", color = MaterialTheme.colorScheme.onSurface) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
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
                .padding(16.dp)
        ) {
            // Navegación de mes
            MonthNavigator(
                year = uiState.currentYear,
                month = uiState.currentMonth,
                onPreviousMonth = { viewModel.goToPreviousMonth() },
                onNextMonth = { viewModel.goToNextMonth() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Encabezados de días de la semana
            WeekDaysHeader()

            Spacer(modifier = Modifier.height(8.dp))

            // Grilla del calendario
            CalendarGrid(
                year = uiState.currentYear,
                month = uiState.currentMonth,
                trainedDates = uiState.trainedDates,
                selectedDate = uiState.selectedDate,
                onDateSelected = { viewModel.selectDate(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Leyenda
            CalendarLegend()

            // Información del día seleccionado
            uiState.selectedDate?.let { selectedDate ->
                Spacer(modifier = Modifier.height(16.dp))
                SelectedDateInfo(
                    date = selectedDate,
                    trainingsCount = uiState.trainingsForSelectedDate.size
                )
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Mes anterior"
            )
        }

        Text(
            text = "${monthNames[month]} $year",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        IconButton(onClick = onNextMonth) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Mes siguiente"
            )
        }
    }
}

@Composable
private fun WeekDaysHeader() {
    val daysOfWeek = listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        daysOfWeek.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
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
    val calendarDays = remember(year, month) {
        getCalendarDays(year, month)
    }

    val today = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxWidth(),
        userScrollEnabled = false
    ) {
        items(calendarDays) { day ->
            CalendarDayCell(
                day = day,
                isTrained = day?.let { trainedDates.contains(normalizeToStartOfDay(it.timeInMillis)) } ?: false,
                isSelected = day?.let { normalizeToStartOfDay(it.timeInMillis) == selectedDate } ?: false,
                isToday = day?.let { normalizeToStartOfDay(it.timeInMillis) == today } ?: false,
                currentMonth = month,
                onDateSelected = { day?.let { onDateSelected(it.timeInMillis) } }
            )
        }
    }
}

@Composable
private fun CalendarDayCell(
    day: Calendar?,
    isTrained: Boolean,
    isSelected: Boolean,
    isToday: Boolean,
    currentMonth: Int,
    onDateSelected: () -> Unit
) {
    val isCurrentMonth = day?.get(Calendar.MONTH) == currentMonth

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .then(
                if (day != null) {
                    Modifier.clickable { onDateSelected() }
                } else {
                    Modifier
                }
            )
            .then(
                when {
                    isSelected -> Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    isToday -> Modifier.border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    else -> Modifier
                }
            )
            .then(
                if (isTrained) {
                    Modifier.background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        day?.let {
            Text(
                text = it.get(Calendar.DAY_OF_MONTH).toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    isTrained -> MaterialTheme.colorScheme.onPrimaryContainer
                    !isCurrentMonth -> MaterialTheme.colorScheme.outline
                    else -> MaterialTheme.colorScheme.onSurface
                },
                fontWeight = if (isToday || isTrained) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun CalendarLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Día entrenado
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
            )
            Text(
                text = "Día entrenado",
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Hoy
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            )
            Text(
                text = "Hoy",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun SelectedDateInfo(
    date: Long,
    trainingsCount: Int
) {
    val dateFormat = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "ES"))
    val formattedDate = dateFormat.format(Date(date))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = formattedDate.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (trainingsCount > 0) {
                    "✅ $trainingsCount entrenamiento(s) completado(s)"
                } else {
                    "Sin entrenamientos registrados"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = if (trainingsCount > 0) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

private fun getCalendarDays(year: Int, month: Int): List<Calendar?> {
    val days = mutableListOf<Calendar?>()

    val firstDayOfMonth = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val firstDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1

    // Agregar días vacíos al inicio
    repeat(firstDayOfWeek) {
        days.add(null)
    }

    // Agregar días del mes
    val daysInMonth = firstDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    for (day in 1..daysInMonth) {
        days.add(Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        })
    }

    // Completar la última fila si es necesario
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


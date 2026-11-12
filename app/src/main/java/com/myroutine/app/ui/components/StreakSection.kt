package com.myroutine.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.myroutine.app.ui.screens.calendar.CalendarUiState

@Composable
fun StreakSection(uiState: CalendarUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "TU CONSTANCIA",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        // IntrinsicSize.Max + fillMaxHeight: las tres tarjetas toman la altura de la más alta
        Row(
            modifier = Modifier.height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatTile(
                icon = Icons.Default.LocalFireDepartment,
                value = uiState.currentStreakWeeks.toString(),
                unit = weeksLabel(uiState.currentStreakWeeks),
                label = "Racha actual",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            StatTile(
                icon = Icons.Default.EmojiEvents,
                value = uiState.bestStreakWeeks.toString(),
                unit = weeksLabel(uiState.bestStreakWeeks),
                label = "Mejor racha",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            StatTile(
                icon = Icons.Default.DateRange,
                value = uiState.weekProgress.toString(),
                unit = "/ ${uiState.routineDaysPerWeek}",
                label = "Esta semana",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                WeekProgressBar(
                    done = uiState.weekProgress,
                    total = uiState.routineDaysPerWeek
                )
            }
        }
    }
}

private fun weeksLabel(count: Int) = if (count == 1) "semana" else "semanas"

@Composable
private fun StatTile(
    icon: ImageVector,
    value: String,
    unit: String,
    label: String,
    modifier: Modifier = Modifier,
    extra: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(20.dp)
        )
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        extra?.invoke()
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun WeekProgressBar(done: Int, total: Int) {
    if (total <= 0) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        repeat(total) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(
                        color = if (index < done) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceContainerHighest,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

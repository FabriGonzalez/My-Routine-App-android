package com.myroutine.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.myroutine.app.ui.screens.calendar.CalendarUiState
import androidx.compose.ui.Alignment
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

@Composable
fun StreakSection(uiState: CalendarUiState) {

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        StreakCard(
            title = "Racha actual",
            value = "${uiState.currentStreakWeeks} semanas",
            icon = Icons.Default.LocalFireDepartment
        )

        StreakCard(
            title = "Mejor racha",
            value = "${uiState.bestStreakWeeks} semanas",
            icon = Icons.Default.EmojiEvents
        )

        StreakCard(
            title = "Semana actual",
            value = "${uiState.weekProgress}/${uiState.routineDaysPerWeek}",
            icon = Icons.Default.DateRange
        )
    }
}

@Composable
private fun StreakCard(
    title: String,
    value: String,
    icon: ImageVector
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )

            Spacer(Modifier.width(16.dp))

            Column {

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
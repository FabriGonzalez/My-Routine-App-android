package com.myroutine.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.myroutine.app.data.local.entity.ExerciseHistory
import com.myroutine.app.data.local.entity.MeasureType

@Composable
fun ExerciseHistoryTable(
    exercises: List<ExerciseHistory>,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {

        ExerciseHistoryTableHeader()

        exercises.forEach { exercise ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = exercise.exerciseName,
                    modifier = Modifier.weight(2f),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                val measureText = when (exercise.measureType) {
                    MeasureType.WEIGHT -> {
                        val value = if (exercise.measureValue % 1.0 == 0.0)
                            exercise.measureValue.toInt().toString()
                        else
                            exercise.measureValue.toString()
                        "$value Kg"
                    }
                    MeasureType.TIME -> {
                        val value = if (exercise.measureValue % 1.0 == 0.0)
                            exercise.measureValue.toInt().toString()
                        else
                            exercise.measureValue.toString()
                        "$value s"
                    }
                }
                Text(
                    text = measureText,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = if (exercise.failureValue) "Fallo" else "${exercise.reps}",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "${exercise.sets}",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}
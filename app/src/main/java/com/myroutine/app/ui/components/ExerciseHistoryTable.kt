package com.myroutine.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.myroutine.app.data.local.entity.ExerciseHistory
import com.myroutine.app.data.local.entity.MeasureType

@Composable
fun ExerciseHistoryTable(
    exercises: List<ExerciseHistory>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        exercises.forEach { exercise ->
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = exercise.exerciseName,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = historyDetail(exercise),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun historyDetail(exercise: ExerciseHistory): String {
    val value = if (exercise.measureValue % 1.0 == 0.0) {
        exercise.measureValue.toLong().toString()
    } else {
        exercise.measureValue.toString()
    }

    val measure = when (exercise.measureType) {
        MeasureType.WEIGHT -> if (exercise.measureValue == 0.0) "Peso corporal" else "$value kg"
        MeasureType.TIME -> "$value s"
    }

    val reps = when {
        exercise.failureValue -> "al fallo"
        exercise.reps != null -> "${exercise.reps} reps"
        else -> null
    }

    val sets = if (exercise.sets == 1) "1 serie" else "${exercise.sets} series"

    return listOfNotNull(measure, reps, sets).joinToString(" · ")
}

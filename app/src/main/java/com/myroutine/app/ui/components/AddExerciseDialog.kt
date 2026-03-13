package com.myroutine.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.myroutine.app.data.local.entity.MeasureType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExerciseDialog(
    show: Boolean,
    currentDayIndex: Int,
    onDismiss: () -> Unit,
    onAddExercise: (name: String, sets: Int, reps: Int, measureValue: Double, measureType: MeasureType, failureValue: Boolean, dayIndex: Int) -> Unit
) {
    if (!show) return

    var name by remember { mutableStateOf("") }
    var setsText by remember { mutableStateOf("") }
    var measureValueText by remember { mutableStateOf("") }
    var repsText by remember { mutableStateOf("") }
    var failureValueText by remember { mutableStateOf(false) }
    var measureType by remember { mutableStateOf(MeasureType.WEIGHT) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        title = {
            Text(
                "Agregar ejercicio",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(Modifier.height(12.dp))

                // Series y Repeticiones en la misma fila
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = setsText,
                        onValueChange = { setsText = it },
                        label = { Text("Series") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    OutlinedTextField(
                        value = repsText,
                        onValueChange = { repsText = it },
                        label = { Text("Repeticiones") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        enabled = !failureValueText
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Entrenar al fallo",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = failureValueText,
                        onCheckedChange = { failureValueText = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    "TIPO DE MEDIDA",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (measureType == MeasureType.WEIGHT)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.01f)
                            )
                            .clickable { measureType = MeasureType.WEIGHT }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Kilogramos (Kg)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (measureType == MeasureType.WEIGHT)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (measureType == MeasureType.TIME)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.01f)
                            )
                            .clickable { measureType = MeasureType.TIME }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Segundos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (measureType == MeasureType.TIME)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = measureValueText,
                    onValueChange = { measureValueText = it },
                    label = {
                        Text(
                            if (measureType == MeasureType.WEIGHT)
                                "Peso (kg)"
                            else
                                "Tiempo (segundos)"
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (measureType == MeasureType.WEIGHT)
                            KeyboardType.Decimal
                        else
                            KeyboardType.Number
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val sets = setsText.toIntOrNull() ?: 0
                    val reps = repsText.toIntOrNull() ?: 0
                    val measureValue = measureValueText.toDoubleOrNull() ?: 0.0

                    val validRepsOrFailure = failureValueText || reps > 0
                    val validMeasure = measureValue >= 0

                    if (name.isNotBlank() && sets > 0 && validRepsOrFailure && validMeasure) {
                        onAddExercise(
                            name.trim(),
                            sets,
                            reps,
                            measureValue,
                            measureType,
                            failureValueText,
                            currentDayIndex
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Cancelar")
            }
        }
    )
}
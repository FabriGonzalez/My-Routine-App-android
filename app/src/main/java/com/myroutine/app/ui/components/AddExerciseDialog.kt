package com.myroutine.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun AddExerciseDialog(
    show: Boolean,
    currentDayIndex: Int,
    onDismiss: () -> Unit,
    onAddExercise: (name: String, sets: Int, reps: Int, weight: Double, dayIndex: Int) -> Unit
){
    if(!show) return

    var name by remember { mutableStateOf("") }
    var setsText by remember { mutableStateOf("") }
    var weightText by remember { mutableStateOf("") }
    var repsText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar ejercicio") },
        text = {
            Column{
                OutlinedTextField(
                    value = name,
                    onValueChange = {name = it},
                    label = {Text("Nombre") },
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = setsText,
                    onValueChange = {setsText = it},
                    label = {Text ("Series")},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = repsText,
                    onValueChange = {repsText = it},
                    label = {Text ("Repeticiones")},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = weightText,
                    onValueChange = {weightText = it},
                    label = {Text ("Peso *Opcional")},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
            }
        },
        confirmButton = {
            Button(onClick = {
                val sets = setsText.toIntOrNull() ?: 0
                val weight = weightText.toDoubleOrNull() ?: 0.0
                val reps = repsText.toIntOrNull() ?: 0
                if (name.isNotBlank() && sets > 0 && reps > 0) {
                    onAddExercise(name.trim(), sets, reps, weight, currentDayIndex)
                }
            }) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}